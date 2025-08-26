import { ScrollArea } from "@/components/ui/scroll-area";
import { ip } from "@/utils/ip";
import axios from "axios";
import {
  Eye,
  Notebook,
  NotebookText,
  Upload,
  User,
  MoreVertical,
  FileX2,
  Ban,
  CheckCircle,
  AlertCircle,
  Lock,
  Unlock,
} from "lucide-react";
import { useEffect, useState, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import { AddDocument } from "../employee/modals/addDocumentForSupplier";
import { DocumentViewer } from "../employee/modals/viewDocumentForSupplier";
import { toast } from "sonner";

interface Contract {
  idContract: string;
  serviceName: string;
}

interface Document {
  id: string;
  title: string;
  ownerName: string;
  hasDoc: boolean;
  status: string;
  expirationDate?: string;
  isUnique?: boolean;
  lastCheck?: string;
  uploadDate?: string;
  bloqueia: boolean;
}

export function ContarctsByProvider() {
  const id = useParams();
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [collaborators, setCollaborators] = useState([]);
  const [subcontractors, setSubcontractors] = useState([]);
  const [selectedContractName, setSelectedContractName] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [provider, setProvider] = useState<any>(null);
  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);
  const [isViewerModalOpen, setIsViewerModalOpen] = useState(false);
  const [selectedDocumentId, setSelectedDocumentId] = useState<string | null>(
    null
  );
  const [selectedDocumentTitle, setSelectedDocumentTitle] = useState<
    string | null
  >(null);
  const [openMenuDocumentId, setOpenMenuDocumentId] = useState<string | null>(
    null
  );
  const [viewOption, setViewOption] = useState<
    "documents" | "collaborators" | "subcontractors"
  >("documents");
  const [isExemptionModalOpen, setIsExemptionModalOpen] = useState(false);
  const [documentToExempt, setDocumentToExempt] = useState<{
    id: string;
    title: string;
  } | null>(null);
  const [description, setDescription] = useState("");

  const token = localStorage.getItem("tokenClient");

  const getContractsByProvider = async () => {
    try {
      const res = await axios.get(`${ip}/contract/supplier/filtered-supplier`, {
        params: { idSearch: id.id },
        headers: { Authorization: `Bearer ${token}` },
      });
      setContracts(res.data.content);
    } catch (err) {
      console.error("Error fetching contracts:", err);
    }
  };

  const [isLoadingSubs, setIsLoadingSubs] = useState(false);

  const getAllSubcontractors = useCallback(async () => {
    try {
      setIsLoadingSubs(true);
      const res = await axios.get(`${ip}/contract/subcontractor`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSubcontractors(res.data?.content || res.data || []);
    } catch (err) {
      console.error("Erro ao buscar subcontratados:", err);
    } finally {
      setIsLoadingSubs(false);
    }
  }, [token]);

  const getAllDatas = useCallback(
    async (idContract: string, serviceName: string) => {
      try {
        const res = await axios.get(`${ip}/document/contract/${idContract}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setDocuments(res.data.documentDtos || []);
        setCollaborators(res.data.employeeDtos || []);
        setSubcontractors(res.data.subcontractorDtos || []);
        setSelectedContractName(serviceName);
        setSearchTerm("");
      } catch (err) {
        console.error("Error fetching all data for contract:", err);
      }
    },
    [token]
  );

  const getProvider = async () => {
    try {
      const res = await axios.get(`${ip}/supplier/${id.id}`);
      setProvider(res.data);
    } catch (err) {
      console.error("Error fetching provider:", err);
    }
  };

  useEffect(() => {
    if (id) {
      getProvider();
      getContractsByProvider();
    }
  }, [id]);

  useEffect(() => {
    if (selectedContractName) {
      const currentContract = contracts.find(
        (c) => c.serviceName === selectedContractName
      );
      if (currentContract) {
        getAllDatas(currentContract.idContract, currentContract.serviceName);
      }
    }
  }, [selectedContractName, getAllDatas, contracts]);

  const filteredDocuments = documents.filter(
    (doc: Document) =>
      doc.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      doc.ownerName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenUploadModal = (documentId: string, documentTitle: string) => {
    setSelectedDocumentId(documentId);
    setSelectedDocumentTitle(documentTitle);
    setIsUploadModalOpen(true);
  };

  const handleCloseUploadModal = () => {
    setIsUploadModalOpen(false);
    setSelectedDocumentId(null);
    setSelectedDocumentTitle(null);
    if (selectedContractName) {
      const currentContract = contracts.find(
        (c) => c.serviceName === selectedContractName
      );
      if (currentContract) {
        getAllDatas(currentContract.idContract, currentContract.serviceName);
      }
    }
  };

  const handleOpenViewerModal = (documentId: string) => {
    setSelectedDocumentId(documentId);
    setIsViewerModalOpen(true);
  };

  const handleCloseViewerModal = () => {
    setIsViewerModalOpen(false);
    setSelectedDocumentId(null);
  };

  const handleStatusChangeForDocument = useCallback(
    (documentIdChanged: string, newStatus: string) => {
      console.log(
        `Documento ${documentIdChanged} mudou para status: ${newStatus}`
      );
      setDocuments((prev) =>
        prev.map((d) =>
          d.id === documentIdChanged ? { ...d, status: newStatus } : d
        )
      );
      const currentContract = contracts.find(
        (c) => c.serviceName === selectedContractName
      );
      if (currentContract) {
        getAllDatas(currentContract.idContract, currentContract.serviceName);
      }
      toast(
        `Status do documento atualizado para "${newStatus.replace(/_/g, " ")}".`
      );
    },
    [contracts, selectedContractName, getAllDatas]
  );

  const handleOpenExemptionModal = (
    documentId: string,
    documentTitle: string
  ) => {
    const doc = documents.find((d) => d.id === documentId);
    if (!doc) {
      toast("Documento não encontrado.");
      return;
    }

    if (doc.status === "ISENTO") {
      toast(`O documento "${documentTitle}" já está isento.`);
      return;
    }
    setDocumentToExempt({ id: documentId, title: documentTitle });
    setIsExemptionModalOpen(true);
  };

  const handleCloseExemptionModal = () => {
    setIsExemptionModalOpen(false);
    setDescription("");
    setDocumentToExempt(null);
  };

  const confirmExemptDocument = async () => {
    if (!documentToExempt) return;

    if (description.trim() === "") {
      toast("A justificativa é obrigatória.");
      return;
    }
    try {
      const selectedContract = contracts.find(
        (contract) => contract.serviceName === selectedContractName
      );

      if (!selectedContract) {
        toast("Contrato selecionado não encontrado.");
        return;
      }
      await axios.post(
        `${ip}/document/${documentToExempt.id}/exempt`,
        { motivo: description },
        {
          params: { contractId: selectedContract.idContract },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast(`Documento "${documentToExempt.title}" isento com sucesso!`);
      handleCloseExemptionModal();

      getAllDatas(selectedContract.idContract, selectedContract.serviceName);
    } catch (error: any) {
      console.error(
        "Erro ao isentar documento:",
        error.response?.data || error.message
      );
      toast("Erro ao isentar o documento.");
    }
  };

  const formatarData = (dataString?: string) => {
    if (!dataString) return "-";
    const data = new Date(dataString);
    if (isNaN(data.getTime())) return "-";
    return data.toLocaleDateString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "2-digit",
    });
  };

  // ====================== ALTERAÇÃO 1 ======================
  const getStatusClass = (status: string) => {
    if (status === "PENDENTE") return "text-yellow-500";
    if (status === "PENDENTE_ISENCAO") return "text-yellow-500";
    if (status === "EM_ANALISE") return "text-blue-500";
    if (status === "APROVADO" || status === "APROVADO_IA")
      return "text-green-600";
    if (status === "REPROVADO" || status === "REPROVADO_IA")
      return "text-red-600";
    if (status === "VENCIDO") return "text-orange-500"; // <--- LINHA ADICIONADA
    if (status === "ISENTO") return "text-blue-500";
    return "";
  };
  // =========================================================

  return (
    <div className="flex items-start gap-10 px-10 relative bottom-[4vw]">
      <div className="bg-realizaBlue border rounded-md flex flex-col w-[25vw]">
        <div className="p-5 flex items-center gap-1">
          <Notebook className="text-[#C0B15B]" />
          <h1 className="text-white font-medium">
            Fornecedor: {provider ? provider.corporateName : "Carregando..."}
          </h1>
        </div>
        <div className="bg-neutral-600 h-[1px]" />
        <div className="w-full flex flex-col gap-5">
          <span className="text-neutral-400 text-[14px] pt-5 px-5">
            Selecione um contrato:
          </span>
          <ScrollArea className="flex items-start flex-col gap-1 h-[60vh]">
            {contracts.map((contract: any, index) => (
              <div
                key={contract.idContract}
                className={`w-full p-2 cursor-pointer ${
                  index % 2 === 1 ? "bg-realizaBlue" : "bg-[#4D657A]"
                }`}
                onClick={() =>
                  getAllDatas(contract.idContract, contract.serviceName)
                }
              >
                <p className="text-white text-[18px] px-5">
                  {contract.serviceName}
                </p>
              </div>
            ))}
          </ScrollArea>
        </div>
      </div>

      <div className="w-full flex flex-col gap-5">
        <div className="bg-white p-5 rounded-md shadow-md w-full relative">
          <span className="text-neutral-500 text-[14px]">
            Contrato selecionado:
          </span>
          <div>
            <h2 className="text-[#34495E] text-[20px] font-semibold underline">
              {selectedContractName || "Nenhum contrato selecionado"}
            </h2>
          </div>

          <div className="absolute top-5 right-5 flex gap-4">
            <button
              onClick={() => setViewOption("documents")}
              className={`${
                viewOption === "documents"
                  ? "bg-realizaBlue text-white"
                  : "bg-neutral-200 text-[#34495E]"
              } py-2 px-4 rounded-md hover:bg-blue-700 transition duration-300`}
            >
              Documentos Empresa
            </button>
            <button
              onClick={() => setViewOption("collaborators")}
              className={`${
                viewOption === "collaborators"
                  ? "bg-realizaBlue text-white"
                  : "bg-neutral-200 text-[#34495E]"
              } py-2 px-4 rounded-md hover:bg-blue-700 transition duration-300`}
            >
              Colaboradores
            </button>
            <button
              onClick={() => {
                setViewOption("subcontractors");
                getAllSubcontractors();
              }}
              className={`${
                viewOption === "subcontractors"
                  ? "bg-realizaBlue text-white"
                  : "bg-neutral-200 text-[#34495E]"
              } py-2 px-4 rounded-md hover:bg-blue-700 transition duration-300`}
            >
              Subcontratados
            </button>
          </div>
        </div>

        <div className="bg-white rounded-md p-5 border border-neutral-400 shadow-md flex flex-col gap-10 h-[64vh]">
          {viewOption === "documents" && (
            <div className="border border-neutral-400 rounded-md shadow-md p-5 w-full flex flex-col gap-6">
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-2 text-[#34495E]">
                  <NotebookText />
                  <h2 className="text-[20px]">Documentos vinculados:</h2>
                </div>
              </div>

              <input
                type="text"
                placeholder="Buscar por título ou empresa..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="border border-neutral-300 rounded-md px-3 py-2 text-sm w-full"
              />

              <div
                className="overflow-y-auto max-h-[35vh] pr-2"
                style={{ scrollbarGutter: "stable" }}
              >
                <div
                  className="grid grid-cols-[1fr_2fr_0.5fr_1fr_1fr_1fr_0.5fr] gap-4
                                     text-sm font-semibold text-neutral-600 py-2
                                     border-b border-neutral-300 items-center
                                     sticky top-0 bg-white z-10"
                >
                  <div>Status</div>
                  <div>Documento</div>
                  <div className="text-center">Bloqueia</div>
                  <div className="text-center">Envio</div>
                  <div className="text-center">Checagem</div>
                  <div className="text-center">Validade</div>
                  <div className="text-center">Ações</div>
                </div>

                <div className="flex flex-col gap-4">
                  {filteredDocuments.length > 0 ? (
                    filteredDocuments.map((doc: Document) => (
                      <div
                        className="grid grid-cols-[1fr_2fr_0.5fr_1fr_1fr_1fr_0.5fr] gap-4 items-center py-2 border-b border-neutral-200 last:border-b-0"
                        key={doc.id}
                      >
                        {/* ====================== ALTERAÇÃO 2 ====================== */}
                        <div>
                          {doc.status === "EM_ANALISE" ? (
                            <div className="flex items-center gap-2">
                              <AlertCircle className="w-4 h-4 text-blue-500" />
                              <span className="text-sm font-semibold text-blue-500">
                                EM ANÁLISE
                              </span>
                            </div>
                          ) : (
                            <div className="flex items-center gap-2">
                              {doc.status === "REPROVADO" ||
                              doc.status === "REPROVADO_IA" ? (
                                <Ban className="w-4 h-4 text-red-500" />
                              ) : doc.status === "VENCIDO" ? ( // <--- BLOCO ADICIONADO
                                <AlertCircle className="w-4 h-4 text-orange-500" />
                              ) : doc.status === "APROVADO" ||
                                doc.status === "APROVADO_IA" ? (
                                <CheckCircle className="w-4 h-4 text-green-600" />
                              ) : doc.status === "ISENTO" ? (
                                <FileX2 className="w-4 h-4 text-blue-500" />
                              ) : doc.status === "PENDENTE_ISENCAO" ? (
                                <AlertCircle className="w-4 h-4 text-yellow-500" />
                              ) : null}

                              <span
                                className={`text-sm font-medium ${getStatusClass(
                                  doc.status
                                )}`}
                              >
                                {doc.status.replace(/_/g, " ")}
                              </span>
                            </div>
                          )}

                          {doc.status === "VENCIDO" && ( // <--- BLOCO ADICIONADO
                            <div className="flex items-center gap-2 mt-1">
                              <AlertCircle className="w-4 h-4 text-orange-500" />
                              <span className="text-xs font-semibold text-orange-500">
                                ⚠️ Documento expirado
                              </span>
                            </div>
                          )}

                          {doc.status === "EM_ANALISE" && (
                            <div className="flex items-center gap-2">
                              <AlertCircle className="w-4 h-4 text-blue-500" />
                              <span className="text-xs font-semibold text-blue-500">
                                ⚠️ Necessita análise humana
                              </span>
                            </div>
                          )}
                          {doc.status === "PENDENTE_ISENCAO" && (
                            <div className="flex items-center gap-2">
                              <AlertCircle className="w-4 h-4 text-yellow-500" />
                              <span className="text-xs font-semibold text-yellow-500">
                                ⚠️ Aguardando aprovação de isenção
                              </span>
                            </div>
                          )}
                        </div>
                        {/* ========================================================= */}
                        <div className="col-span-1">
                          <h3 className="text-[16px] font-medium">
                            {doc.title}
                          </h3>
                          {doc.isUnique !== undefined && (
                            <span className="text-[12px] text-neutral-600">
                              {doc.isUnique
                                ? "Documento exclusivo para este contrato"
                                : "Documento válido para outros contratos"}
                            </span>
                          )}
                        </div>

                        <div className="flex justify-center items-center">
                          {doc.bloqueia ? (
                            <div className="flex items-center gap-2 text-red-500">
                              <Lock className="w-4 h-4" />
                            </div>
                          ) : (
                            <div className="flex items-center gap-2 text-green-600">
                              <Unlock className="w-4 h-4" />
                            </div>
                          )}
                        </div>

                        <div className="text-center">
                          <span className="text-sm text-neutral-600">
                            {formatarData(doc.uploadDate)}
                          </span>
                        </div>
                        <div className="text-center">
                          <span className="text-sm text-neutral-600">
                            {formatarData(doc.lastCheck)}
                          </span>
                        </div>
                        <div className="text-center">
                          <span className="text-sm text-neutral-600">
                            {formatarData(doc.expirationDate)}
                          </span>
                        </div>

                        <div className="flex justify-center relative">
                          <button
                            onClick={() =>
                              setOpenMenuDocumentId(
                                openMenuDocumentId === doc.id ? null : doc.id
                              )
                            }
                            className="p-1 hover:bg-gray-200 rounded"
                          >
                            <MoreVertical className="w-5 h-5" />
                          </button>
                          {openMenuDocumentId === doc.id && (
                            <div className="absolute right-0 mt-2 w-32 bg-white border border-gray-200 rounded-md shadow-lg z-50">
                              {doc.hasDoc && doc.status !== "PENDENTE" && (
                                <button
                                  onClick={() => handleOpenViewerModal(doc.id)}
                                  className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <Eye className="w-5 h-5 text-base" />
                                  Visualizar
                                </button>
                              )}

                              <>
                                <button
                                  onClick={() =>
                                    handleOpenUploadModal(doc.id, doc.title)
                                  }
                                  className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <Upload className="w-5 h-5 text-base" />
                                  Enviar
                                </button>

                                <button
                                  type="button"
                                  onClick={() =>
                                    handleOpenExemptionModal(doc.id, doc.title)
                                  }
                                  className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <FileX2
                                    className="w-5 h-5 text-base"
                                    color="#b31933"
                                  />
                                  Isentar
                                </button>
                              </>
                            </div>
                          )}
                        </div>
                      </div>
                    ))
                  ) : (
                    <span className="text-neutral-400">
                      Nenhum documento encontrado
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}

          {viewOption === "collaborators" && (
            <div className="w-full">
              <div className="flex items-center gap-2 text-[#34495E] mb-6">
                <User />
                <h2 className="text-[20px]">Colaboradores</h2>
              </div>
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3 overflow-y-auto max-h-[50vh] pr-2">
                {collaborators.length > 0 ? (
                  // @ts-ignore
                  collaborators.map((employee: any) => (
                    <Link
                      to={`/sistema/detailsEmployees/${employee.id}`}
                      key={employee.id}
                    >
                      <div className="border border-neutral-200 rounded-lg p-3 flex flex-col items-center text-center shadow-sm hover:shadow-md transition-shadow duration-300 cursor-pointer h-full">
                        <div className="bg-neutral-100 p-2 rounded-full mb-2">
                          <User className="w-6 h-6 text-neutral-500" />
                        </div>
                        <p className="text-sm font-semibold text-[#34495E] leading-tight">
                          {employee.name}
                        </p>
                        <span className="text-xs text-realizaBlue font-medium mt-1">
                          {employee.cboTitle}
                        </span>
                      </div>
                    </Link>
                  ))
                ) : (
                  <div className="col-span-full text-center text-neutral-400 mt-10">
                    Nenhum colaborador encontrado
                  </div>
                )}
              </div>
            </div>
          )}

          {viewOption === "subcontractors" && (
            <div className="flex flex-col items-start gap-10">
              <div className="flex items-center gap-2 text-[#34495E]">
                <User />
                <h2 className="text-[20px]">Subcontratados</h2>
              </div>

              <input
                type="text"
                placeholder="Buscar por nome ou CNPJ..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="border border-neutral-300 rounded-md px-3 py-2 text-sm w-full max-w-md"
              />

              <div className="flex flex-col gap-8 overflow-y-auto max-h-[35vh] pr-2 w-full">
                {isLoadingSubs ? (
                  <span className="text-neutral-400">Carregando...</span>
                ) : subcontractors.length > 0 ? (
                  // @ts-ignore
                  subcontractors.map((sub: any) => (
                    <div key={sub.id} className="flex flex-col gap-5">
                      {/* ======================= ALTERAÇÃO COMEÇA AQUI ======================= */}
                      <div className="flex items-center justify-between w-full">
                        {/* Informações do subcontratado (lado esquerdo) */}
                        <div className="flex items-center gap-5">
                          <div className="bg-neutral-400 p-2 rounded-full">
                            <User />
                          </div>
                          <div>
                            <h1>{sub.responsible}</h1>
                            <p className="text-[18px]">
                              {sub.contractReference}
                            </p>
                            <span className="text-[12px] text-realizaBlue font-semibold underline">
                              {sub.nameSubcontractor}
                            </span>
                          </div>
                        </div>

                        {/* Botão para ver detalhes (lado direito) */}
                        <Link to={`/sistema/subcontracts-details/${sub.idContract}`}>
                          <button className="flex items-center gap-2 bg-realizaBlue text-white py-2 px-4 rounded-md hover:bg-blue-700 transition duration-300">
                            <NotebookText className="w-5 h-5" />
                            <span>Ver Detalhes</span>
                          </button>
                        </Link>
                      </div>
                      {/* ======================= ALTERAÇÃO TERMINA AQUI ======================= */}
                      <div className="bg-neutral-400 h-[1px]" />
                    </div>
                  ))
                ) : (
                  <span className="text-neutral-400">
                    Nenhum subcontratado encontrado
                  </span>
                )}
              </div>
            </div>
          )}
        </div>

        <AddDocument
          isOpen={isUploadModalOpen}
          onClose={handleCloseUploadModal}
          documentId={selectedDocumentId}
          preSelectedTitle={selectedDocumentTitle}
        />

        {selectedDocumentId && (
          <DocumentViewer
            isOpen={isViewerModalOpen}
            onClose={handleCloseViewerModal}
            documentId={selectedDocumentId}
            onStatusChange={handleStatusChangeForDocument}
          />
        )}

        {isExemptionModalOpen && documentToExempt && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md mx-4">
              <h2 className="text-xl font-semibold mb-4 text-[#34495E]">
                Justificativa para Isenção
              </h2>
              <p className="text-sm text-neutral-600 mb-4">
                Você está isentando o documento:{" "}
                <span className="font-medium text-neutral-800">
                  {documentToExempt.title}
                </span>
              </p>
              <textarea
                className="w-full h-24 p-2 border border-gray-300 rounded-md resize-none focus:outline-none focus:ring-2 focus:ring-realizaBlue"
                placeholder="Insira o motivo da isenção aqui..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              />
              <div className="flex justify-end gap-3 mt-4">
                <button
                  onClick={handleCloseExemptionModal}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition"
                >
                  Cancelar
                </button>
                <button
                  onClick={confirmExemptDocument}
                  className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 transition"
                >
                  Confirmar Isenção
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
