import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import {
  Eye,
  Pencil,
  BadgeCheck,
  X,
  MoreVertical,
  History,
  ScrollText,
  Loader2,
} from "lucide-react";
import { ModalTesteSendSupplier } from "@/components/client-add-supplier";
import { ModalCadastroSubcontratado } from "@/components/modal-cadastro-subcontratado";

import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { useWatch } from "react-hook-form";

import likeImage from "@/assets/like.png";
import { useNavigate } from "react-router-dom";

// --- Enum enxuto (apenas os 3 usados no front)
enum ContractStatusEnum {
  ACTIVE = "ACTIVE",
  FINISHED = "FINISHED",
  SUSPENDED = "SUSPENDED",
}

const ACTIVE_STATES = new Set<ContractStatusEnum>([ContractStatusEnum.ACTIVE]);
const SUSPENDED_STATES = new Set<ContractStatusEnum>([
  ContractStatusEnum.SUSPENDED,
]);
const FINISHED_STATES = new Set<ContractStatusEnum>([
  ContractStatusEnum.FINISHED,
]);

// --- NORMALIZAÇÃO ---
function getContractStatusFromSupplier(
  supplier: any
): ContractStatusEnum | undefined {
  const raw =
    supplier?.contractStatus ??
    supplier?.status ??
    supplier?.statusEnum ??
    supplier?.contractStatusEnum;

  const s = typeof raw === "string" ? raw.toUpperCase() : undefined;
  if (!s) return undefined;

  if (
    ["SUSPENDED", "SUSPEND_REQUESTED", "REACTIVATION_REQUESTED"].includes(s)
  ) {
    return ContractStatusEnum.SUSPENDED;
  }
  if (["FINISHED", "DENIED"].includes(s)) {
    return ContractStatusEnum.FINISHED;
  }
  if (["ACTIVE", "PENDING", "FINISH_REQUESTED"].includes(s)) {
    return ContractStatusEnum.ACTIVE;
  }
  return ContractStatusEnum.ACTIVE;
}

function isActiveStatus(status?: ContractStatusEnum) {
  return !!status && ACTIVE_STATES.has(status);
}
function isSuspendedStatus(status?: ContractStatusEnum) {
  return !!status && SUSPENDED_STATES.has(status);
}
function isFinishedStatus(status?: ContractStatusEnum) {
  return !!status && FINISHED_STATES.has(status);
}

const editContractSchema = z.object({
  contractReference: z.string().optional(),
  providerSupplierName: z.string().optional(),
  serviceName: z.string().optional(),
  idResponsible: z.string().optional(),
  dateStart: z.string().optional(),
  expenseType: z.enum(["CAPEX", "OPEX", "NENHUM"]).optional(),
  idServiceType: z.string().optional(),
  subcontractPermission: z.enum(["true", "false"]).optional(),
  hse: z.boolean().optional(),
  labor: z.boolean().optional(),
  description: z.string().optional(),
});

function StatusBadge({ status }: { status?: ContractStatusEnum }) {
  const baseClass = "w-3 h-3 rounded-full mx-auto my-auto block";
  let statusStyle = "bg-gray-400";
  if (isSuspendedStatus(status)) statusStyle = "bg-orange-400";
  else if (isFinishedStatus(status)) statusStyle = "bg-red-600";
  else if (isActiveStatus(status)) statusStyle = "bg-green-600";
  return <span className={`${baseClass} ${statusStyle}`} />;
}

function Modal({
  title,
  onClose,
  children,
}: {
  title: string;
  onClose: () => void;
  children: React.ReactNode;
}) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div className="bg-white rounded-md shadow-lg w-[90vw] md:w-[640px] relative">
        <div className="flex items-center justify-between bg-[#2E3C4D] px-5 py-4 rounded-t-md h-[60px]">
          <div className="flex items-center gap-2">
            <div className="bg-yellow-400 p-[6px] rounded-sm flex items-center justify-center">
              <ScrollText className="w-4 h-4 text-[#2E3C4D]" />
            </div>
            <h2 className="text-white text-base font-semibold">{title}</h2>
          </div>
          <button
            onClick={onClose}
            className="text-white hover:text-gray-300"
            title="Fechar"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-4">{children}</div>
      </div>
    </div>
  );
}

// util para unificar listas (por idContract)
function uniqueBy<T>(
  arr: T[],
  keySelector: (x: T) => string | number | undefined
) {
  const seen = new Set<string | number>();
  return arr.filter((item) => {
    const key = keySelector(item);
    if (key === undefined) return true;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

export function TableServiceProvider() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const { selectedBranch } = useBranch();
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isFinalizeModalOpen, setIsFinalizeModalOpen] = useState(false);
  const [isFinalizingContract, setIsFinalizingContract] = useState(false);
  const [isSuspendingContract, setIsSuspendingContract] = useState(false);
  const [editFormData, setEditFormData] = useState<any | null>(null);
  const [selectedSupplier, setSelectedSupplier] = useState<any | null>(null);

  // >>> separa menu aberto do contrato alvo da ação
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [targetContractId, setTargetContractId] = useState<string | null>(null);

  const [managers, setManagers] = useState<any[]>([]);
  const [servicesType, setServicesType] = useState<any[]>([]);
  const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);
  const [contractHistory, setContractHistory] = useState<any[]>([]);
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);
  const [isSuspendModalOpen, setIsSuspendModalOpen] = useState(false);

  const [statusFilter, setStatusFilter] = useState<
    "Todos" | "Ativo" | "Finalizado" | "Suspenso"
  >("Todos");

  const { register, handleSubmit, reset, control } = useForm<
    z.infer<typeof editContractSchema>
  >({
    resolver: zodResolver(editContractSchema),
  });

  const hseWatch = useWatch({ control, name: "hse" });
  const laborWatch = useWatch({ control, name: "labor" });

  const [activities, setActivities] = useState<any[]>([]);
  const [selectedSsmaActivitiesEdit, setSelectedSsmaActivitiesEdit] = useState<
    string[]
  >([]);
  const [selectedLaborActivitiesEdit, setSelectedLaborActivitiesEdit] =
    useState<string[]>([]);
  const [searchSsmaActivityEdit, setSearchSsmaActivityEdit] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  // Carregamentos iniciais
  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getManager();
      getServicesType();
      getActivities();
      getSupplier(); // primeira carga
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedBranch]);

  // >>> refetch ao trocar de aba (crítico para ver Suspensos)
  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter]);

  useEffect(() => {
    if (editFormData && managers.length > 0 && servicesType.length > 0) {
      reset({
        ...editFormData,
        subcontractPermission: editFormData.subcontractPermission
          ? "true"
          : "false",
        hse: editFormData.hse ?? false,
        labor: editFormData.labor ?? false,
        idResponsible: editFormData.idResponsible || "",
        idServiceType: editFormData.idServiceType || "",
      });
    }
  }, [editFormData, managers, servicesType, reset]);

  // --- Buscar fornecedores do back, cobrindo todos os casos
  // --- Buscar fornecedores do back, cobrindo todos os casos
  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const base = { idSearch: selectedBranch.idBranch };

      const fetchPage = async (extraParams: Record<string, any>) => {
        const params = { ...base, ...extraParams };
        const res = await axios.get(`${ip}/contract/supplier/filtered-client`, {
          params,
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });
        return res.data?.content ?? [];
      };

      let list: any[] = [];

      if (statusFilter === "Todos") {
        // ⚠️ Agora também busca explicitamente os status suspensos e finalizados
        const [active, finished, suspended, suspendReq, reactivateReq] =
          await Promise.all([
            fetchPage({ isActive: true }),
            fetchPage({ status: "FINISHED" }),
            fetchPage({ status: "SUSPENDED" }),
            fetchPage({ status: "SUSPEND_REQUESTED" }),
            fetchPage({ status: "REACTIVATION_REQUESTED" }),
          ]);
        list = uniqueBy(
          [
            ...active,
            ...finished,
            ...suspended,
            ...suspendReq,
            ...reactivateReq,
          ],
          (x) => x.idContract
        );
      } else if (statusFilter === "Ativo") {
        list = await fetchPage({ isActive: true });
      } else if (statusFilter === "Finalizado") {
        list = await fetchPage({ status: "FINISHED" });
      } else if (statusFilter === "Suspenso") {
        const [inactive, suspendReq, reactivateReq, suspended] =
          await Promise.all([
            fetchPage({ isActive: false }),
            fetchPage({ status: "SUSPEND_REQUESTED" }),
            fetchPage({ status: "REACTIVATION_REQUESTED" }),
            fetchPage({ status: "SUSPENDED" }),
          ]);
        list = uniqueBy(
          [...inactive, ...suspendReq, ...reactivateReq, ...suspended],
          (x) => x.idContract
        );
      }

      setSuppliers(list);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    } finally {
      setLoading(false);
    }
  };

  const onSubmitEdit = async (data: z.infer<typeof editContractSchema>) => {
    try {
      const token = localStorage.getItem("tokenClient");
      const payload = {
        ...data,
        subcontractPermission: data.subcontractPermission === "true",
        idActivities: [
          ...selectedSsmaActivitiesEdit,
          ...selectedLaborActivitiesEdit,
        ],
      };
      await axios.put(
        `${ip}/contract/supplier/${editFormData.idContract}`,
        payload,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      toast.success("Contrato atualizado com sucesso");
      await getSupplier();
      setIsEditModalOpen(false);
      setIsEditing(false);
    } catch (err) {
      console.error(err);
      toast.error("Erro ao atualizar contrato");
    }
  };

  const getManager = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setManagers(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar gestores", err);
    }
  };

  const handleHistoryClick = (supplier: any) => {
    setSelectedSupplier(supplier);
    setIsHistoryModalOpen(true);
    getContractHistory(supplier.idContract);
  };

  const getServicesType = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/contract/service-type`, {
        params: { owner: "BRANCH", idOwner: selectedBranch?.idBranch },
        headers: { Authorization: `Bearer ${token}` },
      });
      setServicesType(res.data);
    } catch (err) {
      console.error("Erro ao buscar tipos de serviço", err);
    }
  };

  const getActivities = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setActivities(res.data);
    } catch (err) {
      console.error("Erro ao buscar atividades", err);
    }
  };

  const getContractHistory = async (contractId: string) => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/audit-log`, {
        params: { id: contractId, auditLogTypeEnum: "CONTRACT" },
        headers: { Authorization: `Bearer ${token}` },
      });
      setContractHistory(res.data.content || []);
    } catch (error) {
      console.error("Erro ao buscar histórico do contrato", error);
      toast.error("Erro ao buscar histórico");
    }
  };

  const handleCheckboxChangeEdit = (
    type: "ssma" | "labor",
    activityId: string,
    isChecked: boolean
  ) => {
    const setFunc =
      type === "ssma"
        ? setSelectedSsmaActivitiesEdit
        : setSelectedLaborActivitiesEdit;
    setFunc((prev) =>
      isChecked ? [...prev, activityId] : prev.filter((id) => id !== activityId)
    );
  };

  const handleEditClick = (supplier: any) => {
    setSelectedSupplier(supplier);
    setEditFormData({ ...supplier });
    setTargetContractId(supplier.idContract);
    setIsEditing(false);
    setIsEditModalOpen(true);
  };

  const handleSuspendClick = (supplier: any) => {
    console.log("handleSuspendClick -> contrato:", supplier.idContract);
    setSelectedSupplier(supplier);
    setTargetContractId(supplier.idContract); // <<< usar o alvo da ação
    setIsSuspendModalOpen(true);
  };

  const handleSuspendContract = async () => {
    console.log("handleSuspendContract iniciado. target:", targetContractId);
    if (!targetContractId) {
      console.log("targetContractId está nulo/indefinido. Abortando.");
      return;
    }

    setIsSuspendingContract(true);
    const token = localStorage.getItem("tokenClient");
    const endpoint = `${ip}/contract/suspend/${targetContractId}`;
    console.log("POST", endpoint);

    try {
      const response = await axios.post(
        endpoint,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      console.log("Resposta da suspensão:", response.status, response.data);
      toast.success("Contrato suspenso com sucesso");
      await getSupplier();
      setIsSuspendModalOpen(false);
      setTargetContractId(null);
      setOpenMenuId(null);
    } catch (err) {
      toast.error("Erro ao suspender contrato");
      if (axios.isAxiosError(err)) {
        console.error(
          "Erro Axios (suspender):",
          err.response?.data || err.message
        );
      } else {
        console.error("Erro ao suspender contrato:", err);
      }
    } finally {
      setIsSuspendingContract(false);
    }
  };

  const filteredSuppliers = suppliers.filter((supplier) => {
    const term = searchTerm.toLowerCase();
    const matchesSearchTerm =
      supplier.contractReference?.toLowerCase().includes(term) ||
      supplier.providerSupplierName?.toLowerCase().includes(term) ||
      supplier.providerSupplierCnpj?.includes(term) ||
      supplier.serviceName?.toLowerCase().includes(term);

    const status = getContractStatusFromSupplier(supplier);
    const isFinished = isFinishedStatus(status);
    const isSuspended = isSuspendedStatus(status);
    const isActive = isActiveStatus(status);

    const shouldShow =
      statusFilter === "Todos"
        ? matchesSearchTerm
        : statusFilter === "Ativo"
          ? matchesSearchTerm && isActive
          : statusFilter === "Finalizado"
            ? matchesSearchTerm && isFinished
            : matchesSearchTerm && isSuspended;

    console.log(
      `Contrato ${supplier.contractReference} | raw=${
        supplier?.contractStatus ??
        supplier?.status ??
        supplier?.statusEnum ??
        supplier?.contractStatusEnum
      } | normalized=${status} | isActive=${isActive} | isSusp=${isSuspended} | isFin=${isFinished}`
    );

    return shouldShow;
  });

  useEffect(() => {
    console.log("Contratos exibidos (após filtro):", filteredSuppliers.length);
    if (filteredSuppliers.length === 0 && !loading && suppliers.length > 0) {
      console.log("Nenhum contrato encontrado após a filtragem.");
    }
  }, [filteredSuppliers, loading, suppliers]);

  function traduzirAcao(acao: string) {
    const traducoes: { [key: string]: string } = {
      ALL: "Todas",
      CREATE: "Criado",
      UPDATE: "Atualizado",
      DELETE: "Deletado",
      UPLOAD: "Enviado",
      FINISH: "Finalizado",
      APPROVE: "Aprovado",
      REJECT: "Rejeitado",
      EXEMPT: "Isento",
      ALLOCATE: "Alocado",
      DEALLOCATE: "Desalocado",
      STATUS_CHANGE: "Mudança de Status",
      ACTIVATE: "Ativado",
      LOGIN: "Login",
      LOGOUT: "Logout",
    };
    return traducoes[acao] ?? acao;
  }

  return (
    <div className="p-5 md:p-10">
      <div className="mb-4"></div>
      <div className="block md:hidden space-y-4">
        {loading ? (
          <p className="text-center text-gray-600">Carregando...</p>
        ) : filteredSuppliers.length > 0 ? (
          filteredSuppliers.map((supplier) => (
            <div
              key={supplier.idProvider}
              className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
            >
              <p className="text-sm font-semibold text-gray-700">
                Referência do Contrato:
              </p>
              <p className="mb-2 text-gray-800">{supplier.contractReference}</p>
              <p className="text-sm font-semibold text-gray-700">Nome:</p>
              <p className="mb-2 text-realizaBlue">
                {supplier.providerSupplierName}
              </p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">
                {supplier.providerSupplierCnpj}
              </p>
              <p className="text-sm font-semibold text-gray-700">
                Nome do Serviço:
              </p>
              <p className="mb-2 text-gray-800">{supplier.serviceName}</p>
              <p className="text-sm font-semibold text-gray-700">
                Data de Ínicio:
              </p>
              <p className="mb-2 text-gray-800">
                {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
              </p>
              <p className="mb-2 text-gray-800 ">
                {supplier.dateFinish
                  ? new Date(supplier.dateFinish).toLocaleDateString("pt-BR")
                  : "-"}
              </p>
              <p className="text-sm font-semibold text-gray-700">Gestor:</p>
              <p className="mb-2 text-gray-800">{supplier.responsible}</p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button
                  title="Visualizar contrato"
                  onClick={() =>
                    navigate(`/sistema/fornecedor/${supplier.providerSupplier}`)
                  }
                >
                  <Eye className="w-5 h-5" />
                </button>
                <button
                  title="Editar"
                  onClick={() => handleEditClick(supplier)}
                >
                  <Pencil className="w-5 h-5" />
                </button>
                <button
                  title="Finalizar"
                  onClick={() => {
                    setTargetContractId(supplier.idContract);
                    setIsFinalizeModalOpen(true);
                  }}
                >
                  <BadgeCheck className="w-5 h-5" />
                </button>
              </div>
              <p className="text-sm font-semibold text-gray-700">Status:</p>
              <StatusBadge status={getContractStatusFromSupplier(supplier)} />
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">
            Nenhum fornecedor encontrado.
          </p>
        )}
      </div>

      <div className="rounded-lg border bg-white p-4 shadow-lg flex flex-col gap-5">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4 mb-4">
          <input
            type="text"
            placeholder="Buscar por contrato, fornecedor, CNPJ, serviço..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full md:w-1/2 px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm bg-neutral-100"
          />
          <div className="flex gap-2 flex-wrap justify-center md:justify-start">
            <button
              onClick={() => setStatusFilter("Todos")}
              className={`px-4 py-2 rounded-md text-sm font-medium ${
                statusFilter === "Todos"
                  ? "bg-blue-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Todos
            </button>
            <button
              onClick={() => setStatusFilter("Ativo")}
              className={`px-4 py-2 rounded-md text-sm font-medium ${
                statusFilter === "Ativo"
                  ? "bg-green-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Ativo
            </button>
            <button
              onClick={() => setStatusFilter("Finalizado")}
              className={`px-4 py-2 rounded-md text-sm font-medium ${
                statusFilter === "Finalizado"
                  ? "bg-red-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Finalizado
            </button>
            <button
              onClick={() => setStatusFilter("Suspenso")}
              className={`px-4 py-2 rounded-md text-sm font-medium ${
                statusFilter === "Suspenso"
                  ? "bg-orange-600 text-white"
                  : "bg-gray-200 text-gray-700 hover:bg-gray-300"
              }`}
            >
              Suspenso
            </button>
          </div>
          <ModalTesteSendSupplier />
        </div>

        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-[#345D5C33]">
            <tr>
              <th className="border border-gray-300 p-2 text-left">
                Referência do Contrato
              </th>
              <th className="border border-gray-300 p-2 text-left">
                Nome do Fornecedor
              </th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
              <th className="border border-gray-300 p-2 text-left">
                Nome do Serviço
              </th>
              <th className="border border-gray-300 p-2 text-left">
                Data de Ínicio
              </th>
              <th className="border border-gray-300 p-2 text-left">
                Data de finalização
              </th>
              <th className="border border-gray-300 p-2 text-left">Gestor</th>
              <th className="border border-gray-300 p-2 text-left">Status</th>
              <th className="border border-gray-300 p-2 text-left">Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan={9}
                  className="border border-gray-300 p-2 text-center"
                >
                  Carregando...
                </td>
              </tr>
            ) : filteredSuppliers.length > 0 ? (
              filteredSuppliers.map((supplier) => (
                <tr key={supplier.idProvider}>
                  <td className="border border-gray-300 p-2">
                    {supplier.contractReference}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.providerSupplierName}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.providerSupplierCnpj}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.serviceName}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.dateFinish
                      ? new Date(supplier.dateFinish).toLocaleDateString(
                          "pt-BR"
                        )
                      : "-"}
                  </td>
                  <td className="border border-gray-300 p-2">
                    {supplier.responsible}
                  </td>
                  <td className="border border-gray-300 p-2">
                    <StatusBadge
                      status={getContractStatusFromSupplier(supplier)}
                    />
                  </td>
                  <td className="border border-gray-300 p-2 text-center align-middle">
                    <div className="relative inline-block text-left">
                      <button
                        onClick={() =>
                          setOpenMenuId(
                            openMenuId === supplier.idContract
                              ? null
                              : supplier.idContract
                          )
                        }
                        className="p-1 hover:bg-gray-200 rounded"
                      >
                        <MoreVertical className="w-5 h-5" />
                      </button>

                      {openMenuId === supplier.idContract && (
                        <div className="absolute right-0 mt-2 w-60 bg-white border border-gray-200 rounded-md shadow-lg z-50">
                          <ModalCadastroSubcontratado idContract={supplier.idContract} />
                          <button
                            onClick={() =>
                              navigate(
                                `/sistema/fornecedor/${supplier.providerSupplier}`
                              )
                            }
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Eye className="w-4 h-4" /> Visualizar
                          </button>
                          <button
                            onClick={() => {
                              handleEditClick(supplier);
                              setOpenMenuId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Pencil className="w-4 h-4" /> Editar
                          </button>
                          <button
                            onClick={() => {
                              handleHistoryClick(supplier);
                              setOpenMenuId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <History className="w-4 h-4" /> Histórico
                          </button>
                          <button
                            onClick={() => {
                              setTargetContractId(supplier.idContract);
                              setIsFinalizeModalOpen(true);
                              setOpenMenuId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <BadgeCheck className="w-4 h-4" /> Finalizar
                          </button>
                          <button
                            onClick={() => {
                              handleSuspendClick(supplier);
                              setOpenMenuId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <X className="w-4 h-4" /> Suspender
                          </button>
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td
                  colSpan={9}
                  className="border border-gray-300 p-2 text-center"
                >
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>

        <div className="mt-4 text-sm text-gray-600 flex gap-4 items-center justify-end">
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-green-600 inline-block" />
            <span>Ativo</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-red-600 inline-block" />
            <span>Finalizado</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-orange-400 inline-block" />
            <span>Suspenso</span>
          </div>
        </div>
      </div>

      {isViewModalOpen && selectedSupplier && (
        <Modal
          title="Visualizar detalhes de contrato"
          onClose={() => setIsViewModalOpen(false)}
        >
          <div className="bg-white rounded-md p-4 shadow text-gray-800 max-h-[70vh] overflow-y-auto space-y-4 text-sm">
            <div>
              <p className="font-semibold text-gray-500">
                Referência do contrato:
              </p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.contractReference}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Nome do Fornecedor:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.providerSupplierName}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">CNPJ:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.providerSupplierCnpj}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Nome do serviço:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.serviceName}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Gestor do contrato:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.responsible}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Data de início:</p>
              <p className="border-b border-gray-200 pb-1">
                {new Date(selectedSupplier.dateStart).toLocaleDateString(
                  "pt-BR"
                )}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">
                Data de finalização:
              </p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.dateFinish
                  ? new Date(selectedSupplier.dateFinish).toLocaleDateString(
                      "pt-BR"
                    )
                  : "-"}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Descrição:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.description}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Tipo de Despesa:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.expenseType}
              </p>
            </div>
            <div>
              <p className="font-semibold text-gray-500">Filial:</p>
              <p className="border-b border-gray-200 pb-1">
                {selectedSupplier.branchName}
              </p>
            </div>
          </div>
        </Modal>
      )}

      {isEditModalOpen && editFormData && (
        <Modal
          title="Editar contrato"
          onClose={() => {
            setIsEditModalOpen(false);
            setIsEditing(false);
          }}
        >
          <div className="text-gray-800 space-y-4 max-h-[80vh] overflow-auto w-full p-1">
            <form
              onSubmit={handleSubmit(onSubmitEdit)}
              className="flex flex-col gap-4 text-sm"
            >
              <label className="space-y-1">
                <span className="font-semibold">Nome do fornecedor</span>
                <input
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  {...register("providerSupplierName")}
                  disabled={!isEditing}
                />
              </label>

              <label className="space-y-1">
                <span className="font-semibold">Nome do serviço</span>
                <input
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  {...register("serviceName")}
                  disabled={!isEditing}
                />
              </label>

              <label className="space-y-1">
                <span className="font-semibold">Data de início</span>
                <input
                  type="date"
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  {...register("dateStart")}
                  disabled={!isEditing}
                />
              </label>

              <label className="space-y-1">
                <span className="font-semibold">Gestor do contrato</span>
                <select
                  {...register("idResponsible")}
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  disabled={!isEditing}
                >
                  <option value="">Selecione</option>
                  {managers.map((m) => (
                    <option key={m.idUser} value={m.idUser}>
                      {m.firstName} {m.surname}
                    </option>
                  ))}
                </select>
              </label>

              <label className="space-y-1">
                <span className="font-semibold">Tipo de Despesa</span>
                <select
                  {...register("expenseType")}
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  disabled={!isEditing}
                >
                  <option value="">Selecione</option>
                  <option value="CAPEX">CAPEX</option>
                  <option value="OPEX">OPEX</option>
                  <option value="NENHUM">Nenhuma</option>
                </select>
              </label>

              <label className="space-y-1">
                <span className="font-semibold">Tipo do Serviço</span>
                <select
                  {...register("idServiceType")}
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  disabled={!isEditing}
                >
                  <option value="">Selecione</option>
                  {servicesType.map((service) => (
                    <option
                      key={service.idServiceType}
                      value={service.idServiceType}
                    >
                      {service.title}
                    </option>
                  ))}
                </select>
              </label>

              <div className="space-y-1">
                <p className="font-semibold">Permitir Subcontratação?</p>
                <div className="flex gap-4">
                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      value="true"
                      {...register("subcontractPermission")}
                      disabled={!isEditing}
                    />
                    Sim
                  </label>
                  <label className="flex items-center gap-2">
                    <input
                      type="radio"
                      value="false"
                      {...register("subcontractPermission")}
                      disabled={!isEditing}
                    />
                    Não
                  </label>
                </div>
              </div>

              {hseWatch && (
                <div className="space-y-2">
                  <label className="font-semibold">
                    Tipo de atividade SSMA
                  </label>
                  <input
                    type="text"
                    value={searchSsmaActivityEdit}
                    onChange={(e) => setSearchSsmaActivityEdit(e.target.value)}
                    placeholder="Buscar atividade..."
                    className="w-full rounded border px-3 py-2 text-sm bg-[#F2F3F5]"
                    disabled={!isEditing}
                  />
                  <div className="bg-white text-gray-800 rounded p-2 max-h-[150px] overflow-y-auto border">
                    {activities
                      .filter((a) =>
                        a.title
                          .toLowerCase()
                          .includes(searchSsmaActivityEdit.toLowerCase())
                      )
                      .map((activity) => (
                        <label
                          key={activity.idActivity}
                          className="flex gap-2 py-1"
                        >
                          <input
                            type="checkbox"
                            checked={selectedSsmaActivitiesEdit.includes(
                              activity.idActivity
                            )}
                            onChange={(e) =>
                              handleCheckboxChangeEdit(
                                "ssma",
                                activity.idActivity,
                                e.target.checked
                              )
                            }
                            disabled={!isEditing}
                          />
                          {activity.title}
                        </label>
                      ))}
                  </div>
                </div>
              )}

              {laborWatch && (
                <div className="space-y-2">
                  <label className="font-semibold">
                    Tipo de atividade Trabalhista
                  </label>
                  <div className="bg-white text-gray-800 rounded p-2 max-h-[150px] overflow-y-auto border">
                    {activities.map((activity) => (
                      <label
                        key={activity.idActivity}
                        className="flex gap-2 py-1"
                      >
                        <input
                          type="checkbox"
                          checked={selectedLaborActivitiesEdit.includes(
                            activity.idActivity
                          )}
                          onChange={(e) =>
                            handleCheckboxChangeEdit(
                              "labor",
                              activity.idActivity,
                              e.target.checked
                            )
                          }
                          disabled={!isEditing}
                        />
                        {activity.title}
                      </label>
                    ))}
                  </div>
                </div>
              )}

              <label className="space-y-1">
                <span className="font-semibold">Descrição</span>
                <textarea
                  rows={3}
                  className="w-full rounded border px-3 py-2 bg-[#F2F3F5] text-gray-700"
                  {...register("description")}
                  disabled={!isEditing}
                />
              </label>

              <div className="flex justify-end gap-4 pt-4">
                {!isEditing ? (
                  <button
                    type="button"
                    onClick={() => setIsEditing(true)}
                    className="bg-blue-600 text-white px-4 py-2 rounded-md font-semibold hover:bg-blue-700 transition"
                  >
                    Editar
                  </button>
                ) : (
                  <>
                    <button
                      type="button"
                      onClick={() => {
                        setIsEditModalOpen(false);
                        setIsEditing(false);
                      }}
                      className="border border-red-600 text-red-600 px-4 py-2 rounded-md font-semibold hover:bg-red-100 transition"
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="bg-green-600 text-white px-4 py-2 rounded-md font-semibold hover:bg-green-700 transition"
                    >
                      Salvar alterações
                    </button>
                  </>
                )}
              </div>
            </form>
          </div>
        </Modal>
      )}

      {isFinalizeModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-md w-[90vw] max-w-[520px] p-6 shadow-lg relative">
            <div className="flex items-start justify-between mb-4">
              <h2 className="text-[#2E3C4D] text-lg font-semibold flex items-center gap-2">
                <X className="w-5 h-5 text-[#C9C9C9]" />
                Finalizar contrato
              </h2>
              <button onClick={() => setIsFinalizeModalOpen(false)}>
                <X className="w-5 h-5 text-gray-400 hover:text-gray-600" />
              </button>
            </div>

            <div className="flex flex-col md:flex-row items-center gap-6">
              <img
                src={likeImage}
                alt="Ilustração de confirmação"
                className="w-[120px] md:w-[160px] max-h-[140px] object-contain"
              />
              <div className="flex-1 text-sm text-gray-700 space-y-2">
                <p className="font-semibold">
                  Deseja realmente finalizar este contrato?
                </p>
                <p>
                  Essa ação é <strong>permanente</strong> e{" "}
                  <strong>não poderá ser desfeita</strong>. O contrato ainda
                  ficará visível, mas não poderá ser reativado.
                </p>
              </div>
            </div>

            <div className="mt-6 flex justify-end gap-4">
              <button
                onClick={() => setIsFinalizeModalOpen(false)}
                className="border border-gray-400 text-gray-700 px-4 py-2 rounded-md font-semibold hover:bg-gray-100 transition"
                disabled={isFinalizingContract}
              >
                Voltar
              </button>
              <button
                onClick={async () => {
                  if (!targetContractId) return;
                  setIsFinalizingContract(true);
                  const token = localStorage.getItem("tokenClient");
                  const endpoint = `${ip}/contract/finish/${targetContractId}`;
                  const payload = { status: "Contrato Cancelado" };
                  try {
                    const response = await axios.post(endpoint, payload, {
                      headers: { Authorization: `Bearer ${token}` },
                    });
                    console.log(
                      "Resposta finalização:",
                      response.status,
                      response.data
                    );
                    toast.success("Contrato finalizado com sucesso");
                    await getSupplier();
                    setIsFinalizeModalOpen(false);
                    setTargetContractId(null);
                    setOpenMenuId(null);
                  } catch (err) {
                    toast.error("Erro ao finalizar contrato");
                    if (axios.isAxiosError(err)) {
                      console.error(
                        "Erro Axios (finalizar):",
                        err.response?.data || err.message
                      );
                    } else {
                      console.error("Erro ao finalizar contrato:", err);
                    }
                  } finally {
                    setIsFinalizingContract(false);
                  }
                }}
                className="bg-green-600 text-white px-4 py-2 rounded-md font-semibold hover:bg-green-700 transition flex items-center justify-center gap-2"
                disabled={isFinalizingContract}
              >
                {isFinalizingContract ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />{" "}
                    Finalizando...
                  </>
                ) : (
                  "Finalizar contrato"
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {isSuspendModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white rounded-md w-[90vw] max-w-[520px] p-6 shadow-lg relative">
            <div className="flex items-start justify-between mb-4">
              <h2 className="text-[#2E3C4D] text-lg font-semibold flex items-center gap-2">
                <X className="w-5 h-5 text-[#C9C9C9]" />
                Suspender contrato
              </h2>
              <button
                onClick={() => {
                  console.log("Fechar modal de suspensão.");
                  setIsSuspendModalOpen(false);
                }}
              >
                <X className="w-5 h-5 text-gray-400 hover:text-gray-600" />
              </button>
            </div>

            <div className="flex flex-col md:flex-row items-center gap-6">
              <img
                src={likeImage}
                alt="Ilustração de confirmação"
                className="w-[120px] md:w-[160px] max-h-[140px] object-contain"
              />
              <div className="flex-1 text-sm text-gray-700 space-y-2">
                <p className="font-semibold">
                  Deseja realmente suspender este contrato?
                </p>
                <p>
                  Essa ação é <strong>permanente</strong> e{" "}
                  <strong>não poderá ser desfeita</strong>. O contrato ainda
                  ficará visível, mas não poderá ser reativado.
                </p>
              </div>
            </div>

            <div className="mt-6 flex justify-end gap-4">
              <button
                onClick={() => {
                  console.log("Voltar no modal de suspensão.");
                  setIsSuspendModalOpen(false);
                }}
                className="border border-gray-400 text-gray-700 px-4 py-2 rounded-md font-semibold hover:bg-gray-100 transition"
                disabled={isSuspendingContract}
              >
                Voltar
              </button>
              <button
                onClick={handleSuspendContract}
                className="bg-orange-600 text-white px-4 py-2 rounded-md font-semibold hover:bg-orange-700 transition flex items-center justify-center gap-2"
                disabled={isSuspendingContract}
              >
                {isSuspendingContract ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />{" "}
                    Suspendendo...
                  </>
                ) : (
                  "Suspender contrato"
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {isHistoryModalOpen && selectedSupplier && (
        <Modal
          title="Histórico do contrato"
          onClose={() => setIsHistoryModalOpen(false)}
        >
          <div className="text-black space-y-2 max-h-[400px] overflow-auto">
            {contractHistory.length > 0 ? (
              contractHistory.map((log, index) => (
                <div key={index} className="border-b border-gray-500 pb-2 mb-2">
                  <p>
                    <strong>Ação:</strong> {traduzirAcao(log.action)}
                  </p>
                  <p>
                    <strong>Usuário:</strong> {log.userResponsibleFullName}
                  </p>
                  <p>
                    <strong>Data:</strong>{" "}
                    {new Date(log.createdAt).toLocaleString("pt-BR")}
                  </p>
                  <p>
                    <strong>Descrição: </strong> {log.description}
                  </p>
                  {log.message && (
                    <p>
                      <strong>Mensagem:</strong> {log.message}
                    </p>
                  )}
                </div>
              ))
            ) : (
              <p>Nenhuma entrada de histórico encontrada.</p>
            )}
          </div>
        </Modal>
      )}
    </div>
  );
}
