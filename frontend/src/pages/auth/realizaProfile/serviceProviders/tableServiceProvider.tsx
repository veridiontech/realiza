import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { Eye, Pencil, BadgeCheck, X, MoreVertical, History } from "lucide-react"; // Importe o ícone History
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { ModalTesteSendSupplier } from "@/components/client-add-supplier";

//
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";

const editContractSchema = z.object({
  contractReference: z.string().nonempty("Referência do contrato é obrigatória"),
  providerSupplierName: z.string().nonempty("Nome do fornecedor é obrigatório"),
  serviceName: z.string().nonempty("Nome do serviço é obrigatório"),
  idResponsible: z.string().nonempty("Selecione um gestor"),
  dateStart: z.string().nonempty("Data de início é obrigatória"),
  expenseType: z.enum(["CAPEX", "OPEX", "NENHUM"]),
  idServiceType: z.string().nonempty("Tipo de serviço é obrigatório"),
  subcontractPermission: z.enum(["true", "false"]),
  hse: z.boolean(),
  labor: z.boolean(),
  description: z.string().optional(),
});



function StatusBadge({ finished }: { finished?: boolean }) {
  const baseClass = "w-3 h-3 rounded-full mx-auto my-auto block";
  const isFinalizado = finished === true;

  const statusStyle = isFinalizado ? "bg-red-600" : "bg-green-600";

  return <span className={`${baseClass} ${statusStyle}`}></span>;
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
      <div
        className="p-6 rounded-lg shadow-lg w-[90vw] md:w-[640px] relative text-white"
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
          backgroundSize: "cover",
          backgroundRepeat: "no-repeat",
          backgroundPosition: "center",
          backgroundColor: "#000",
        }}
      >

        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-white hover:text-gray-300"
          title="Fechar"
        >
          <X className="w-5 h-5" />
        </button>
        <h2 className="text-lg font-semibold text-white mb-4 pr-6">{title}</h2>
        <div>{children}</div>
      </div>
    </div>
  );
}

export function TableServiceProvider() {
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const { selectedBranch } = useBranch();
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isFinalizeModalOpen, setIsFinalizeModalOpen] = useState(false);
  const [selectedSupplier, setSelectedSupplier] = useState<any | null>(null);
  const [editFormData, setEditFormData] = useState<any | null>(null);
  const [selectedSupplierId, setSelectedSupplierId] = useState<string | null>(
    null
  );
  const [managers, setManagers] = useState<any[]>([]);
  const [servicesType, setServicesType] = useState<any[]>([]);
  const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<z.infer<typeof editContractSchema>>({
    resolver: zodResolver(editContractSchema),
  });



  useEffect(() => {
    if (editFormData) {
      reset({
        ...editFormData,
        subcontractPermission: editFormData.subcontractPermission ? "true" : "false",
        hse: editFormData.hse ?? false,
        labor: editFormData.labor ?? false,
      });
    }
  }, [editFormData]);

  const [searchTerm, setSearchTerm] = useState("");

  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      console.log("Requisição ao finalizar");
      const res = await axios.get(`${ip}/contract/supplier/filtered-client`, {
        params: {
          idSearch: selectedBranch.idBranch,
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      console.log("Exemplo de item:", res.data.content);
      setSuppliers(res.data.content);
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
      };

      await axios.put(`${ip}/contract/supplier/${editFormData.idContract}`, payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      toast.success("Contrato atualizado com sucesso");
      await getSupplier();
      setIsEditModalOpen(false);
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
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setManagers(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar gestores", err);
    }
  };

  const handleHistoryClick = (supplier: any) => {
    console.log("Histórico do contrato:", supplier);
    setSelectedSupplier(supplier);
    setIsHistoryModalOpen(true);
  };

  const getServicesType = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/contract/service-type`, {
        params: {
          owner: "BRANCH",
          idOwner: selectedBranch?.idBranch,
        },
        headers: { Authorization: `Bearer ${token}` },
      });
      setServicesType(res.data);
    } catch (err) {
      console.error("Erro ao buscar tipos de serviço", err);
    }
  };


  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      getManager();
      getServicesType();
    }
  }, [selectedBranch]);

  const handleViewClick = (supplier: any) => {
    setSelectedSupplier(supplier);
    setIsViewModalOpen(true);
  };

  const handleEditClick = (supplier: any) => {
    console.log("Supplier editando:", supplier);
    setSelectedSupplier(supplier);
    setEditFormData({ ...supplier });
    setIsEditModalOpen(true);
  };

  const filteredSuppliers = suppliers.filter((supplier) => {
    const term = searchTerm.toLowerCase();
    return (
      supplier.contractReference?.toLowerCase().includes(term) ||
      supplier.providerSupplierName?.toLowerCase().includes(term) ||
      supplier.providerSupplierCnpj?.includes(term) ||
      supplier.serviceName?.toLowerCase().includes(term) ||
      (term === "ativo" && !supplier.finished) ||
      (term === "finalizado" && supplier.finished)
    );
  });

  return (
    <div className="p-5 md:p-10">
      <div className="mb-4"></div>
      <div className="block md:hidden space-y-4">
        {loading ? (
          <p className="text-center text-gray-600">Carregando...</p>
        ) : suppliers.length > 0 ? (
          suppliers.map((supplier: any) => (
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

              <p className="text-sm font-semibold text-gray-700">
                Data de finalização:

              </p>
              <p className="mb-2 text-gray-800 ">
                {supplier.dateFinish ? new Date(supplier.dateFinish).toLocaleDateString("pt-BR") : "-"}

              </p>
              <p className="text-sm font-semibold text-gray-700">Gestor:</p>
              <p className="mb-2 text-gray-800">{supplier.responsible}</p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button
                  title="Visualizar contrato"
                  onClick={() => handleViewClick(supplier)}
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
                    setSelectedSupplierId(supplier.contract?.idContract);
                    setIsFinalizeModalOpen(true);
                  }}
                >
                  <BadgeCheck className="w-5 h-5" />
                </button>
              </div>
              <p className="text-sm font-semibold text-gray-700">Status:</p>
              <StatusBadge finished={supplier.finished} />
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">
            Nenhum fornecedor encontrado.
          </p>
        )}
      </div>

      <div className="rounded-lg border bg-white p-4 shadow-lg flex flex-col gap-5">
        <div className="flex items-center  justify-between">
          <input
            type="text"
            placeholder="Buscar por contrato, fornecedor, CNPJ, serviço ou status..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full md:w-1/2 px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm bg-neutral-100"
          />
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
                  colSpan={7}
                  className="border border-gray-300 p-2 text-center"
                >
                  Carregando...
                </td>
              </tr>
            ) : filteredSuppliers.length > 0 ? (
              filteredSuppliers.map((supplier: any) => (
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
                      ? new Date(supplier.dateFinish).toLocaleDateString("pt-BR") : "-"
                    }

                  </td>

                  <td className="border border-gray-300 p-2">
                    {supplier.responsible}
                  </td>
                  <td className="border border-gray-300 p-2">
                    <StatusBadge finished={supplier.finished} />
                  </td>
                  <td className="border border-gray-300 p-2 text-center align-middle">
                    <div className="relative inline-block text-left">
                      <button
                        onClick={() =>
                          setSelectedSupplierId(
                            selectedSupplierId === supplier.idContract ? null : supplier.idContract
                          )
                        }
                        className="p-1 hover:bg-gray-200 rounded"
                      >
                        <MoreVertical className="w-5 h-5" />
                      </button>

                      {selectedSupplierId === supplier.idContract && (
                        <div className="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-md shadow-lg z-50">
                          <button
                            onClick={() => {
                              handleViewClick(supplier);
                              setSelectedSupplierId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Eye className="w-4 h-4" /> Visualizar
                          </button>
                          <button
                            onClick={() => {
                              handleEditClick(supplier);
                              setSelectedSupplierId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Pencil className="w-4 h-4" /> Editar
                          </button>
                          <button // Novo botão "Histórico"
                            onClick={() => {
                              handleHistoryClick(supplier);
                              setSelectedSupplierId(null);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <History className="w-4 h-4" /> Histórico
                          </button>
                          <button
                            onClick={() => {
                              setSelectedSupplierId(null);
                              setIsFinalizeModalOpen(true);
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <BadgeCheck className="w-4 h-4" /> Finalizar
                          </button>
                          <button
                            disabled
                            className="w-full text-left px-4 py-2 text-sm text-gray-400 flex items-center gap-2 cursor-not-allowed"
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
                  colSpan={7}
                  className="border border-gray-300 p-2 text-center"
                >
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
        {/*legenda dos status dos contratos*/}
        <div className="mt-4 text-sm text-gray-600 flex gap-4 items-center justify-end">
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-green-600 inline-block" />
            <span>Ativo</span>
          </div>
          <div className="flex items-center gap-1">
            <span className="w-3 h-3 rounded-full bg-red-600 inline-block" />
            <span>Finalizado</span>
          </div>
        </div>

      </div>

      {isViewModalOpen && selectedSupplier && (
        <Modal
          title="Visualizar Contrato"
          onClose={() => setIsViewModalOpen(false)}
        >
          <div className="text-white space-y-2 max-h-[400px] overflow-auto">
            <p>
              <strong>Referência do Contrato:</strong>{" "}
              {selectedSupplier.contractReference}
            </p>
            <p>
              <strong>Nome do Fornecedor:</strong>{" "}
              {selectedSupplier.providerSupplierName}
            </p>
            <p>
              <strong>CNPJ:</strong> {selectedSupplier.providerSupplierCnpj}
            </p>
            <p>
              <strong>Nome do Serviço:</strong> {selectedSupplier.serviceName}
            </p>
            <p>
              <strong>Gestor do contrato:</strong>{" "}
              {selectedSupplier.responsible}
            </p>
            <p>
              <strong>Data de Início:</strong>{" "}
              {new Date(selectedSupplier.dateStart).toLocaleDateString("pt-BR")}
            </p>
            <p>
              <strong>Data de Finalização:</strong> {" "}
              {selectedSupplier.dateFinish ? new Date(selectedSupplier.dateFinish).toLocaleDateString("pt-BR") : "-"}
            </p>
            <p>
              <strong>Descrição:</strong> {selectedSupplier.description}
            </p>
            <p>
              <strong>Tipo de Despesa:</strong> {selectedSupplier.expenseType}
            </p>
            <p>
              <strong>Filial:</strong> {selectedSupplier.branchName}
            </p>
          </div>
        </Modal>
      )}

      {isEditModalOpen && editFormData && (
        <Modal title="Editar Contrato" onClose={() => setIsEditModalOpen(false)}>
          <div className="text-white space-y-4 max-h-[80vh] overflow-auto w-[90vw] md:w-[600px]">
            <form
              onSubmit={handleSubmit(onSubmitEdit)}
              className="flex flex-col gap-3 text-black"
            >
              <label>
                Referência do Contrato
                <input
                  className="w-full rounded border px-2 py-1"
                  {...register("contractReference")}
                />
                {errors.contractReference && (
                  <span className="text-red-500">{errors.contractReference.message}</span>
                )}
              </label>

              <label>
                Nome do Fornecedor
                <input
                  className="w-full rounded border px-2 py-1"
                  {...register("providerSupplierName")}
                />
                {errors.providerSupplierName && (
                  <span className="text-red-500">{errors.providerSupplierName.message}</span>
                )}
              </label>

              <label>
                Nome do Serviço
                <input
                  className="w-full rounded border px-2 py-1"
                  {...register("serviceName")}
                />
                {errors.serviceName && (
                  <span className="text-red-500">{errors.serviceName.message}</span>
                )}
              </label>

              <label>
                Gestor
                <select {...register("idResponsible")} className="w-full rounded border px-2 py-1">
                  <option value="">Selecione</option>
                  {managers.map((m: any) => (
                    <option key={m.idUser} value={m.idUser}>
                      {m.firstName} {m.surname}
                    </option>
                  ))}
                </select>
                {errors.idResponsible && (
                  <span className="text-red-500">{errors.idResponsible.message}</span>
                )}
              </label>

              <label>
                Data de Início
                <input
                  type="date"
                  className="w-full rounded border px-2 py-1"
                  {...register("dateStart")}
                />
                {errors.dateStart && (
                  <span className="text-red-500">{errors.dateStart.message}</span>
                )}
              </label>

              <label>
                Tipo de Despesa
                <select {...register("expenseType")} className="w-full rounded border px-2 py-1">
                  <option value="">Selecione</option>
                  <option value="CAPEX">CAPEX</option>
                  <option value="OPEX">OPEX</option>
                  <option value="NENHUM">Nenhuma</option>
                </select>
                {errors.expenseType && (
                  <span className="text-red-500">{errors.expenseType.message}</span>
                )}
              </label>

              <label>
                Tipo do Serviço
                <select {...register("idServiceType")} className="w-full rounded border px-2 py-1">
                  <option value="">Selecione</option>
                  {servicesType.map((service: any) => (
                    <option key={service.idServiceType} value={service.idServiceType}>
                      {service.title}
                    </option>
                  ))}
                </select>
                {errors.idServiceType && (
                  <span className="text-red-500">{errors.idServiceType.message}</span>
                )}
              </label>

              <div className="flex gap-4 items-center">
                <label className="flex gap-2 items-center">
                  <input type="checkbox" {...register("hse")} />
                  SSMA
                </label>
                <label className="flex gap-2 items-center">
                  <input type="checkbox" {...register("labor")} />
                  Trabalhista
                </label>
              </div>


              <label>
                Permitir Subcontratação?
                <div className="flex gap-3">
                  <label className="flex items-center gap-1">
                    <input type="radio" value="true" {...register("subcontractPermission")} />
                    Sim
                  </label>
                  <label className="flex items-center gap-1">
                    <input type="radio" value="false" {...register("subcontractPermission")} />
                    Não
                  </label>
                </div>
                {errors.subcontractPermission && (
                  <span className="text-red-500">{errors.subcontractPermission.message}</span>
                )}
              </label>

              <label>
                Descrição
                <textarea
                  className="w-full rounded border px-2 py-1"
                  rows={3}
                  {...register("description")}
                />
              </label>

              <div className="flex justify-end gap-4 mt-2">
                <button
                  type="button"
                  onClick={() => setIsEditModalOpen(false)}
                  className="bg-red-600 px-4 py-2 rounded text-white"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="bg-green-600 px-4 py-2 rounded text-white"
                >
                  Salvar
                </button>
              </div>
            </form>
          </div>
        </Modal>
      )}


      {isFinalizeModalOpen && (
        <Modal
          title="Finalizar Contrato"
          onClose={() => {
            setIsFinalizeModalOpen(false);
            setSelectedSupplierId(null);
          }}
        >
          <div className="text-white">
            <p className="mb-4">Deseja realmente finalizar este contrato?</p>
            <div className="flex gap-4 justify-end">
              <button
                onClick={() => setIsFinalizeModalOpen(false)}
                className="bg-red-600 px-4 py-2 rounded"
              >
                Não
              </button>
              <button
                onClick={async () => {
                  if (!selectedSupplierId) return;
                  try {
                    const tokenFromStorage =
                      localStorage.getItem("tokenClient");
                    await axios.post(
                      `${ip}/contract/finish/${selectedSupplierId}`,
                      { status: "Contrato Cancelado" },
                      {
                        headers: {
                          Authorization: `Bearer ${tokenFromStorage}`,
                        },
                      }
                    );
                    await getSupplier();
                  } catch (err) {
                    console.error("Erro ao cancelar contrato", err);
                  } finally {
                    setIsFinalizeModalOpen(false);
                    setSelectedSupplierId(null);
                  }
                }}
                className="bg-green-600 px-4 py-2 rounded"
              >
                Sim
              </button>
            </div>
          </div>
        </Modal>
      )}

      {isHistoryModalOpen && selectedSupplier && (
        <Modal title="Histórico do documento" onClose={() => setIsHistoryModalOpen(false)}>
          <div className="text-white space-y-2 max-h-[400px] overflow-auto">
            <p>Historico</p>
          </div>
        </Modal>
      )}
    </div>
  );
}