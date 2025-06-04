import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { Eye, Pencil, BadgeCheck, X } from "lucide-react";
import bgModalRealiza from "@/assets/modalBG.jpeg";

function StatusBadge({ finished }: { finished?: boolean }) {
  const baseClass = "px-3 py-1 rounded font-semibold text-white text-sm";
  const isFinalizado = finished === true;

  const statusText = isFinalizado ? "Finalizado" : "Ativo";
  const statusStyle = isFinalizado ? "bg-red-600" : "bg-green-600";

  return <span className={`${baseClass} ${statusStyle}`}>{statusText}</span>;
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
        className="bg-white p-6 rounded-lg shadow-lg w-96 relative"
        style={{
          backgroundImage: `url(${bgModalRealiza})`,
          backgroundSize: "cover",
          backgroundPosition: "center",
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
  const [selectedSupplierId, setSelectedSupplierId] = useState<string | null>(null);

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
  
  const updateSupplier = async (idContract: string, updatedData: any) => {
    try {
       console.log("Atualizando fornecedor", { idContract, updatedData });
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const payload = {
      branch: updatedData.branch,
      branchName: updatedData.branchName,
      contractReference: updatedData.contractReference,
      dateStart: updatedData.dateStart,
      description: updatedData.description,
      expenseType: updatedData.expenseType,
      finished: updatedData.finished,
      providerSupplier: updatedData.providerSupplier,
      providerSupplierCnpj: updatedData.providerSupplierCnpj,
      providerSupplierName: updatedData.providerSupplierName,
      serviceName: updatedData.serviceName,
    };
    console.log("teste: " , payload);
    
      await axios.put(`${ip}/contract/supplier/${idContract}`, payload, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      await getSupplier();
    } catch (error) {
      console.error("Erro ao atualizar fornecedor", error);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getSupplier();
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
              <p className="text-sm font-semibold text-gray-700">Referência do Contrato:</p>
              <p className="mb-2 text-gray-800">{supplier.contractReference}</p>
              <p className="text-sm font-semibold text-gray-700">Nome:</p>
              <p className="mb-2 text-realizaBlue">{supplier.providerSupplierName}</p>
              <p className="text-sm font-semibold text-gray-700">CNPJ:</p>
              <p className="mb-2 text-gray-800">{supplier.providerSupplierCnpj}</p>
              <p className="text-sm font-semibold text-gray-700">Nome do Serviço:</p>
              <p className="mb-2 text-gray-800">{supplier.serviceName}</p>
              <p className="text-sm font-semibold text-gray-700">Data de Início:</p>
              <p className="mb-2 text-gray-800">
                {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
              </p>
              <p className="text-sm font-semibold text-gray-700">Ações:</p>
              <div className="flex gap-2">
                <button title="Visualizar contrato" onClick={() => handleViewClick(supplier)}>
                  <Eye className="w-5 h-5" />
                </button>
                <button title="Editar" onClick={() => handleEditClick(supplier)}>
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
          <p className="text-center text-gray-600">Nenhum fornecedor encontrado.</p>
        )}
      </div>

      <div className="hidden md:block overflow-x-auto rounded-lg border bg-white p-4 shadow-lg">
        <table className="w-full border-collapse border border-gray-300">
          <thead className="bg-gray-200">
            <tr>
              <th className="border border-gray-300 p-2 text-left">Referência do Contrato</th>
              <th className="border border-gray-300 p-2 text-left">Nome do Fornecedor</th>
              <th className="border border-gray-300 p-2 text-left">CNPJ</th>
              <th className="border border-gray-300 p-2 text-left">Nome do Serviço</th>
              <th className="border border-gray-300 p-2 text-left">Data de Início</th>
              <th className="border border-gray-300 p-2 text-left">Ações</th>
              <th className="border border-gray-300 p-2 text-left">Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="border border-gray-300 p-2 text-center">
                  Carregando...
                </td>
              </tr>
            ) : suppliers.length > 0 ? (
              suppliers.map((supplier: any) => (
                <tr key={supplier.idProvider}>
                  <td className="border border-gray-300 p-2">{supplier.contractReference}</td>
                  <td className="border border-gray-300 p-2">{supplier.providerSupplierName}</td>
                  <td className="border border-gray-300 p-2">{supplier.providerSupplierCnpj}</td>
                  <td className="border border-gray-300 p-2">{supplier.serviceName}</td>
                  <td className="border border-gray-300 p-2">
                    {new Date(supplier.dateStart).toLocaleDateString("pt-BR")}
                  </td>

                  <td className="border border-gray-300 p-2 space-x-2">
                    <button title="Visualizar contrato" onClick={() => handleViewClick(supplier)}>
                      <Eye className="w-5 h-5" />
                    </button>
                    <button title="Editar" onClick={() => handleEditClick(supplier)}>
                      <Pencil className="w-5 h-5" />
                    </button>
                    <button
                      title="Finalizar"
                      onClick={() => {
                        setSelectedSupplierId(supplier.idContract);
                        setIsFinalizeModalOpen(true);
                      }}
                    >
                      <BadgeCheck className="w-5 h-5" />
                    </button>
                  </td>
                  <td className="border border-gray-300 p-2">
                    <StatusBadge finished={supplier.finished} />
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={7} className="border border-gray-300 p-2 text-center">
                  Nenhum fornecedor encontrado.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {isViewModalOpen && selectedSupplier && (
        <Modal title="Visualizar Contrato" onClose={() => setIsViewModalOpen(false)}>
          <div className="text-white space-y-2 max-h-[400px] overflow-auto">
            <p>
              <strong>Referência do Contrato:</strong> {selectedSupplier.contractReference}
            </p>
            <p>
              <strong>Nome do Fornecedor:</strong> {selectedSupplier.providerSupplierName}
            </p>
            <p>
              <strong>CNPJ:</strong> {selectedSupplier.providerSupplierCnpj}
            </p>
            <p>
              <strong>Nome do Serviço:</strong> {selectedSupplier.serviceName}
            </p>
            <p>
              <strong>Data de Início:</strong>{" "}
              {new Date(selectedSupplier.dateStart).toLocaleDateString("pt-BR")}
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
          <div className="flex flex-col gap-4 max-h-[400px] overflow-auto">
            <div>
              <label className="text-white font-semibold block mb-1">Referência do Contrato</label>
              <input
                type="text"
                value={editFormData.contractReference || ""}
                onChange={(e) =>
                  setEditFormData({ ...editFormData, contractReference: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black"
              />
            </div>
            <div>
              <label className="text-white font-semibold block mb-1">Nome do Fornecedor</label>
              <input
                type="text"
                value={editFormData.providerSupplierName || ""}
                onChange={(e) =>
                  setEditFormData({ ...editFormData, providerSupplierName: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black"
              />
            </div>
            <div>
              <label className="text-white font-semibold block mb-1">Nome do Serviço</label>
              <input
                type="text"
                value={editFormData.serviceName || ""}
                onChange={(e) =>
                  setEditFormData({ ...editFormData, serviceName: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black"
              />
            </div>
            <div>
              <label className="text-white font-semibold block mb-1">Data de Início</label>
              <input
                type="date"
                value={
                  editFormData.dateStart
                    ? new Date(editFormData.dateStart).toISOString().slice(0, 10)
                    : ""
                }
                onChange={(e) =>
                  setEditFormData({ ...editFormData, dateStart: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black"
              />
            </div>
            <div>
              <label className="text-white font-semibold block mb-1">Tipo de Despesa</label>
              <input
                type="text"
                value={editFormData.expenseType || ""}
                onChange={(e) =>
                  setEditFormData({ ...editFormData, expenseType: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black"
              />
            </div>
            <div>
              <label className="text-white font-semibold block mb-1">Descrição</label>
              <textarea
                value={editFormData.description || ""}
                onChange={(e) =>
                  setEditFormData({ ...editFormData, description: e.target.value })
                }
                className="w-full rounded-md border border-gray-300 px-3 py-2 text-sm bg-white text-black resize-none"
                rows={3}
              />
            </div>

            <div className="flex justify-end gap-4 mt-4">
              <button
                onClick={() => setIsEditModalOpen(false)}
                className="bg-red-600 px-4 py-2 rounded text-white"
              >
                Cancelar
              </button>
              <button
  onClick={async () => {
    if (!editFormData?.idContract) {
      console.error("ID do contrato não encontrado no formulário de edição");
      return;
    }
    console.log("Salvando contrato", editFormData.idContract, editFormData);
    await updateSupplier(editFormData.idContract, editFormData);
    setIsEditModalOpen(false);
    setSelectedSupplier(null);
    setEditFormData(null);
  }}
  className="bg-green-600 px-4 py-2 rounded text-white"
>
  Salvar
</button>
            </div>
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
                    const tokenFromStorage = localStorage.getItem("tokenClient");
                    await axios.post(
                      `${ip}/contract/finish/${selectedSupplierId}`,
                      { status: "Contrato Cancelado" },
                      {
                        headers: { Authorization: `Bearer ${tokenFromStorage}` },
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
    </div>
  );
}
