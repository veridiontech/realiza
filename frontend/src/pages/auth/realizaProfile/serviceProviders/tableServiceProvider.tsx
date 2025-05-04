import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { Eye, Pencil, UserPlus, Ban, X } from "lucide-react";
import bgModalRealiza from "@/assets/modalBG.jpeg";

function StatusBadge({ status }: { status: string }) {
  const baseClass = "px-3 py-1 rounded font-semibold text-white text-sm";

  const statusStyles: Record<string, string> = {
    "Contrato Cancelado": "bg-red-600",
    "Contrato Suspenso": "bg-yellow-400 text-black",
    "Contrato Concluído": "bg-green-600",
    "Indefinido": "bg-gray-400",
  };

  const style = statusStyles[status] || "bg-gray-400";

  return (
    <span className={`${baseClass} ${style}`}>
      {status}
    </span>
  );
}

function Modal({ title, onClose, children }: { title: string, onClose: () => void, children: React.ReactNode }) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
      <div
        className="bg-white p-6 rounded-lg shadow-lg w-96 relative"
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center'
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
  const [isAllocateModalOpen, setIsAllocateModalOpen] = useState(false);
  const [isFinalizeModalOpen, setIsFinalizeModalOpen] = useState(false);
  const [finalizeStep, setFinalizeStep] = useState(1);
  const [selectedReason, setSelectedReason] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [allocateStep, setAllocateStep] = useState(1);
  const [selectedSupplierId, setSelectedSupplierId] = useState<string | null>(null);

  const getSupplier = async () => {
    if (!selectedBranch?.idBranch) return;
    setLoading(true);
    try {
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch.idBranch}`
      );
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar prestadores de serviço", err);
    } finally {
      setLoading(false);
    }
  };


  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
      setSuppliers([]);
    }
  }, [selectedBranch]);

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
              <p className="text-sm font-semibold text-gray-700">Nome:</p>
              <p className="mb-2 text-realizaBlue">{supplier.tradeName}</p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">{supplier.cnpj}</p>
              <p className="text-sm font-semibold text-gray-700">Filiais:</p>
              <p className="text-gray-800">
                {supplier.branches && supplier.branches.length > 0
                  ? supplier.branches.map((b: any) => b.nameBranch).join(", ")
                  : "Nenhuma filial associada"}
              </p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button title="Visualizar contrato" onClick={() => setIsViewModalOpen(true)}>
                  <Eye className="w-5 h-5" />
                </button>
                <button title="Editar" onClick={() => setIsEditModalOpen(true)}>
                  <Pencil className="w-5 h-5" />
                </button>
                <button title="Alocar funcionário" onClick={() => setIsAllocateModalOpen(true)}>
                  <UserPlus className="w-5 h-5" />
                </button>
                <button
                  title="Finalizar"
                  onClick={() => {
                    setSelectedSupplierId(supplier.contract?.idContract);
                    setIsFinalizeModalOpen(true);
                  }}
                >
                  <Ban className="w-5 h-5" />
                </button>

              </div>
              <p className="text-sm font-semibold text-gray-700">Status:</p>
              <StatusBadge status={supplier.status || "Indefinido"} />
            </div>
          ))
        ) : (
          <p className="text-center text-gray-600">Nenhum fornecedor encontrado.</p>
        )}
      </div>

      <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr>
              <th className="border border-gray-300 p-2 text-left">Nome do Fornecedor</th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
              <th className="border border-gray-300 p-2 text-left">Filiais que Atua</th>
              <th className="border border-gray-300 p-2 text-left">Ações</th>
              <th className="border border-gray-300 p-2 text-left">Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="border border-gray-300 p-2 text-center">
                  Carregando...
                </td>
              </tr>
            ) : suppliers.length > 0 ? (
              suppliers.map((supplier: any) => (
                <tr key={supplier.idProvider}>
                  <td className="border border-gray-300 p-2">{supplier.tradeName}</td>
                  <td className="border border-gray-300 p-2">{supplier.cnpj}</td>
                  <td className="border border-gray-300 p-2">
                    {supplier.branches && supplier.branches.length > 0
                      ? supplier.branches.map((b: any) => b.nameBranch).join(", ")
                      : "Nenhuma filial associada"}
                  </td>
                  <td className="border border-gray-300 p-2 space-x-2">
                    <button title="Visualizar contrato" onClick={() => setIsViewModalOpen(true)}>
                      <Eye className="w-5 h-5" />
                    </button>
                    <button title="Editar" onClick={() => setIsEditModalOpen(true)}>
                      <Pencil className="w-5 h-5" />
                    </button>
                    <button title="Alocar funcionário" onClick={() => setIsAllocateModalOpen(true)}>
                      <UserPlus className="w-5 h-5" />
                    </button>
                    <button
                      title="Finalizar"
                      onClick={() => {
                        setSelectedSupplierId(supplier.contract?.idContract);
                        setIsFinalizeModalOpen(true);
                      }}
                    >
                      <Ban className="w-5 h-5" />
                    </button>

                  </td>
                  <td className="border border-gray-300 p-2">
                    <StatusBadge status={supplier.status || "Indefinido"} />
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={5} className="border border-gray-300 p-2 text-center">
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {isViewModalOpen && (
        <Modal title="Visualizar Contrato" onClose={() => setIsViewModalOpen(false)}>
          <p className="text-white">Conteúdo para visualizar o contrato.</p>
        </Modal>
      )}

      {isEditModalOpen && (
        <Modal title="Editar Fornecedor" onClose={() => setIsEditModalOpen(false)}>
          <p className="text-white">Formulário de edição aqui.</p>
        </Modal>
      )}

      {isAllocateModalOpen && (
        <Modal
          title="Alocar Funcionário"
          onClose={() => {
            setIsAllocateModalOpen(false);
            setSearchTerm("");
            setAllocateStep(1);
          }}
        >
          {allocateStep === 1 ? (
            <div className="text-white space-y-4">
              <label className="block text-sm font-semibold">Buscar Colaborador:</label>
              <input
                type="text"
                placeholder="Digite o nome do colaborador..."
                className="w-full px-3 py-2 rounded text-black"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <div className="flex justify-end">
                <button
                  onClick={() => setAllocateStep(2)}
                  className="bg-realizaBlue px-4 py-2 rounded"
                >
                  Próximo
                </button>
              </div>
            </div>
          ) : (
            <div className="text-white space-y-4">
              <p>Você confirma que esses colaboradores selecionados serão alocados ao contrato?</p>
              <div className="flex justify-end gap-4">
                <button
                  onClick={() => setAllocateStep(1)}
                  className="bg-gray-500 px-4 py-2 rounded"
                >
                  Voltar
                </button>
                <button
                  onClick={() => {
                    setIsAllocateModalOpen(false);
                    setSearchTerm("");
                    setAllocateStep(1);
                  }}
                  className="bg-green-600 px-4 py-2 rounded"
                >
                  Confirmar
                </button>
              </div>
            </div>
          )}
        </Modal>
      )}

      {isFinalizeModalOpen && (
        <Modal title="Finalizar Contrato" onClose={() => {
          setIsFinalizeModalOpen(false);
          setFinalizeStep(1);
          setSelectedReason("");
          setSelectedSupplierId(null);
        }}>
          {finalizeStep === 1 && (
            <div className="text-white">
              <p className="mb-4">Deseja finalizar este contrato?</p>
              <div className="flex gap-4 justify-end">
                <button
                  onClick={() => setIsFinalizeModalOpen(false)}
                  className="bg-gray-600 px-4 py-2 rounded"
                >
                  Não
                </button>
                <button
                  onClick={() => setFinalizeStep(2)}
                  className="bg-red-600 px-4 py-2 rounded"
                >
                  Sim
                </button>
              </div>
            </div>
          )}

          {finalizeStep === 2 && (
            <div className="text-white space-y-3">
              <p className="mb-2">Selecione o motivo da finalização:</p>
              <div className="flex flex-col gap-2">
                <button
                  onClick={() => {
                    setSelectedReason("Contrato Cancelado");
                    setFinalizeStep(3);
                  }}
                  className="bg-red-600 px-4 py-2 rounded"
                >
                  Contrato Cancelado
                </button>
                <button
                  onClick={() => {
                    setSelectedReason("Contrato Suspenso");
                    setFinalizeStep(3);
                  }}
                  className="bg-yellow-400 text-black px-4 py-2 rounded"
                >
                  Contrato Suspenso
                </button>
                <button
                  onClick={() => {
                    setSelectedReason("Contrato Concluído");
                    setFinalizeStep(3);
                  }}
                  className="bg-green-600 px-4 py-2 rounded"
                >
                  Contrato Concluído
                </button>
              </div>
            </div>
          )}

          {finalizeStep === 3 && (
            <div className="text-white">
              <p className="mb-4">
                Você confirma que está finalizando o contrato pelo motivo de:{" "}
                <span className="font-bold">{selectedReason}</span>?
              </p>
              <div className="flex gap-4 justify-end">
                <button
                  onClick={() => {
                    setFinalizeStep(2);
                    setSelectedReason("");
                  }}
                  className="bg-gray-600 px-4 py-2 rounded"
                >
                  Voltar
                </button>
                <button
                  onClick={async () => {
                    if (!selectedSupplierId) return;
                    try {
                      await axios.post(
                        `${ip}/contract/finish/${selectedSupplierId}`,
                        null,
                        {
                          headers: {
                            Authorization: `Bearer ${localStorage.getItem("tokenClient")}`,
                          },
                        }
                      );
                      await getSupplier();
                    } catch (err) {
                      console.error("Erro ao finalizar contrato", err);
                    } finally {
                      setIsFinalizeModalOpen(false);
                      setFinalizeStep(1);
                      setSelectedReason("");
                      setSelectedSupplierId(null);
                    }
                  }}
                  className="bg-realizaBlue px-4 py-2 rounded"
                >
                  Confirmar
                </button>
              </div>
            </div>
          )}
        </Modal>
      )}
    </div>
  );
}