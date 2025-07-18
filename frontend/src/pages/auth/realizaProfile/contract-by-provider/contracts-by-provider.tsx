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
} from "lucide-react";
import { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import { AddDocument } from "../employee/modals/addDocumentForSupplier";
import { DocumentViewer } from "../employee/modals/viewDocumentForSupplier";

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

  const token = localStorage.getItem("tokenClient");

  const getContractsByProvider = async () => {
    try {
      console.log("Fetching contracts for provider ID:", id.id);
      const res = await axios.get(`${ip}/contract/supplier/filtered-supplier`, {
        params: { idSearch: id.id },
        headers: { Authorization: `Bearer ${token}` },
      });
      setContracts(res.data.content);
      console.log("Contracts fetched:", res.data.content);
    } catch (err) {
      console.error("Error fetching contracts:", err);
    }
  };

  const getAllDatas = useCallback(
    async (idContract: string, serviceName: string) => {
      try {
        console.log("Fetching all data for contract ID:", idContract);
        const res = await axios.get(`${ip}/document/contract/${idContract}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setDocuments(res.data.documentDtos || []);
        setCollaborators(res.data.employeeDtos || []);
        setSubcontractors(res.data.subcontractorDtos || []);
        setSelectedContractName(serviceName);
        setSearchTerm("");
        console.log("Documents fetched:", res.data.documentDtos);
        console.log("Collaborators fetched:", res.data.employeeDtos);
        console.log("Subcontractors fetched:", res.data.subcontractorDtos);
      } catch (err) {
        console.error("Error fetching all data for contract:", err);
      }
    },
    [token]
  );

  const getProvider = async () => {
    try {
      console.log("Fetching provider details for ID:", id.id);
      const res = await axios.get(`${ip}/supplier/${id.id}`);
      setProvider(res.data);
      console.log("Provider details fetched:", res.data);
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
    console.log(
      "Opening upload modal for document:",
      documentTitle,
      "ID:",
      documentId
    );
    setSelectedDocumentId(documentId);
    setSelectedDocumentTitle(documentTitle);
    setIsUploadModalOpen(true);
  };

  const handleCloseUploadModal = () => {
    console.log("Closing upload modal.");
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
    console.log("Opening viewer modal for document ID:", documentId);
    setSelectedDocumentId(documentId);
    setIsViewerModalOpen(true);
  };

  const handleCloseViewerModal = () => {
    console.log("Closing viewer modal.");
    setIsViewerModalOpen(false);
    setSelectedDocumentId(null);
  };

  const handleStatusChangeForDocument = useCallback(
    (documentIdChanged: string, newStatus: string) => {
      console.log(`Documento ${documentIdChanged} mudou para status: ${newStatus}`);
      if (selectedContractName) {
        const currentContract = contracts.find(
          (c) => c.serviceName === selectedContractName
        );
        if (currentContract) {
          getAllDatas(currentContract.idContract, currentContract.serviceName);
        }
      }
    },
    [selectedContractName, contracts, getAllDatas]
  );

  const exemptDocument = async (documentId: string, documentTitle: string) => {
    try {
      console.log(
        "Attempting to exempt document:",
        documentTitle,
        "ID:",
        documentId
      );
      const selectedContract = contracts.find(
        (contract: any) => contract.serviceName === selectedContractName
      );

      if (!selectedContract) {
        console.warn("Selected contract not found for exemption.");
        alert("Contrato selecionado não encontrado.");
        return;
      }

      await axios.post(
        `${ip}/document/${documentId}/exempt`,
        {},
        {
          params: { contractId: selectedContract.idContract },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      alert(`Documento "${documentTitle}" isento com sucesso!`);
      if (selectedContractName) {
        const currentContract = contracts.find(
          (c) => c.serviceName === selectedContractName
        );
        if (currentContract) {
          getAllDatas(currentContract.idContract, currentContract.serviceName);
        }
      }
      console.log(`Document "${documentTitle}" exempted successfully.`);
    } catch (error: any) {
      console.error(
        "Error exempting document:",
        error.response?.data || error.message
      );
      alert("Erro ao isentar o documento.");
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

  const getStatusClass = (status: string) => {
    if (status === "PENDENTE") return "text-yellow-500";
    if (status === "EM_ANALISE") return "text-blue-500";
    if (status === "APROVADO" || status === "APROVADO_IA")
      return "text-green-600";
    if (status === "REPROVADO" || status === "REPROVADO_IA")
      return "text-red-600";
    if (status === "ISENTO") return "text-blue-500";
    return "";
  };

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
              Documentos
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
              onClick={() => setViewOption("subcontractors")}
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

        <div className="bg-white rounded-md p-5 border border-neutral-400 shadow-md flex gap-10 h-[50vh]">
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

              <div className="grid grid-cols-[1fr_2fr_1fr_1fr_0.5fr] gap-4 text-sm font-semibold text-neutral-600 pb-2 border-b border-neutral-300 items-center">
                <div>Status</div>
                <div className="col-span-1">Documento</div>
                <div>Checagem</div>
                <div>Validade</div>
                <div className="text-center">Ações</div>
              </div>

              <div className="flex flex-col gap-4 overflow-y-auto max-h-[35vh] pr-2">
                {filteredDocuments.length > 0 ? (
                  filteredDocuments.map((doc: Document) => (
                    <div
                      className="grid grid-cols-[1fr_2fr_1fr_1fr_0.5fr] gap-4 items-center py-2 border-b border-neutral-200 last:border-b-0"
                      key={doc.id}
                    >
                      <div>
                        {doc.status === "EM_ANALISE" ? (
                          <div className="flex items-center gap-2">
                            <AlertCircle className="w-4 h-4 text-blue-500" />
                            <span className="text-sm font-semibold text-blue-500">
                              Em Análise
                            </span>
                          </div>
                        ) : (
                          <div className="flex items-center gap-2">
                            {doc.status === "REPROVADO" ||
                            doc.status === "REPROVADO_IA" ? (
                              <Ban className="w-4 h-4 text-red-500" />
                            ) : doc.status === "APROVADO" ||
                              doc.status === "APROVADO_IA" ? (
                              <CheckCircle className="w-4 h-4 text-green-600" />
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
                        {doc.status === "EM_ANALISE" && (
                          <div className="flex items-center gap-2">
                            <AlertCircle className="w-4 h-4 text-blue-500" />
                            <span className="text-xs font-semibold text-blue-500">
                              ⚠️ Necessita análise humana
                            </span>
                          </div>
                        )}
                      </div>
                      <div className="col-span-1">
                        <h3 className="text-[16px] font-medium">{doc.title}</h3>
                        <span className="text-[12px] text-neutral-600">
                          {doc.ownerName}
                        </span>
                        {doc.isUnique !== undefined && (
                            <span className="text-[12px] text-neutral-600">
                                {' - '}
                                {doc.isUnique ? 'Documento único para esse contrato' : 'Documento se espelha para outros contratos'}
                            </span>
                        )}
                      </div>
                      <div>
                        <span className="text-sm text-neutral-600">
                          {formatarData(doc.lastCheck)}
                        </span>
                      </div>
                      <div>
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
                            {doc.hasDoc && (
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
                                Reenviar
                              </button>

                              <button
                                type="button"
                                onClick={() => exemptDocument(doc.id, doc.title)}
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
          )}

          {viewOption === "collaborators" && (
            <div className="flex flex-col items-start gap-10">
              <div className="flex items-center gap-2 text-[#34495E]">
                <User />
                <h2 className="text-[20px]">Colaboradores</h2>
              </div>
              <div className="flex flex-col gap-8 overflow-y-auto max-h-[35vh] pr-2">
                {collaborators.length > 0 ? (
                  collaborators.map((employee: any) => (
                    <div key={employee.id} className="flex flex-col gap-5">
                      <div className="flex items-center gap-5">
                        <div className="bg-neutral-400 p-2 rounded-full">
                          <User />
                        </div>
                        <div>
                          <p className="text-[18px]">{employee.name}</p>
                          <span className="text-[12px] text-realizaBlue font-semibold underline">
                            {employee.cboTitle}
                          </span>
                        </div>
                      </div>
                      <div className="bg-neutral-400 h-[1px]" />
                    </div>
                  ))
                ) : (
                  <span className="text-neutral-400">
                    Nenhum colaborador encontrado
                  </span>
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
              <div className="flex flex-col gap-8 overflow-y-auto max-h-[35vh] pr-2">
                {subcontractors.length > 0 ? (
                  subcontractors.map((sub: any) => (
                    <div key={sub.id} className="flex flex-col gap-5">
                      <div className="flex items-center gap-5">
                        <div className="bg-neutral-400 p-2 rounded-full">
                          <User />
                        </div>
                        <div>
                          <p className="text-[18px]">{sub.corporateName}</p>
                          <span className="text-[12px] text-realizaBlue font-semibold underline">
                            {sub.cnpj}
                          </span>
                        </div>
                      </div>
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
      </div>
    </div>
  );
}