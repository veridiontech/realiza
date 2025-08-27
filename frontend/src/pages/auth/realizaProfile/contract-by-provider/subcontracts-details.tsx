import { ip } from "@/utils/ip";
import axios from "axios";
import {
  Eye,
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
import { AddDocument } from "../employee/modals/addDocumentForSupplier"; // Verifique o caminho
import { DocumentViewer } from "../employee/modals/viewDocumentForSupplier"; // Verifique o caminho
import { toast } from "sonner";

// Interface para o documento (mantida)
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

// Interface para as informações do subcontratado
interface SubcontractorInfo {
  nameSubcontractor: string;
  responsible: string;
  // Adicione outros campos que a API retornar
}

export function SubcontractorDetails() {
  const { id } = useParams<{ id: string }>(); // Pega o ID da URL
  const [subcontractorInfo, setSubcontractorInfo] =
    useState<SubcontractorInfo | null>(null);
  const [documents, setDocuments] = useState<Document[]>([]);
  const [collaborators, setCollaborators] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
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
  const [viewOption, setViewOption] = useState<"documents" | "collaborators">(
    "documents"
  );
  const [isExemptionModalOpen, setIsExemptionModalOpen] = useState(false);
  const [documentToExempt, setDocumentToExempt] = useState<{
    id: string;
    title: string;
  } | null>(null);
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  const token = localStorage.getItem("tokenClient");

  // Função única para buscar todos os dados do subcontratado
  const getSubcontractorDetails = useCallback(async () => {
    if (!id) return;
    setIsLoading(true);
    try {
      // ATENÇÃO: Verifique se este endpoint está correto para sua API
      const res = await axios.get(`${ip}/contract/subcontractor/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      // A API deve retornar um objeto com as infos, documentos e colaboradores
      setSubcontractorInfo(res.data);
      setDocuments(res.data.documentDtos || []);
      setCollaborators(res.data.employeeDtos || []);
    } catch (err) {
      console.error("Erro ao buscar detalhes do subcontratado:", err);
      toast.error("Não foi possível carregar os dados do subcontratado.");
    } finally {
      setIsLoading(false);
    }
  }, [id, token]);

  useEffect(() => {
    getSubcontractorDetails();
  }, [getSubcontractorDetails]);

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
    getSubcontractorDetails(); // Re-busca os dados após upload
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
    (newStatus: string) => {
      toast(
        `Status do documento atualizado para "${newStatus.replace(
          /_/g,
          " "
        )}".`
      );
      getSubcontractorDetails(); // Re-busca os dados após mudança de status
    },
    [getSubcontractorDetails]
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
      // ATENÇÃO: Verifique se o endpoint e os parâmetros para isenção estão corretos
      await axios.post(
        `${ip}/document/${documentToExempt.id}/exempt`,
        { motivo: description },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          // O `contractId` foi removido, verifique se a API precisa de outro parâmetro
          // como `subcontractorId: id`
        }
      );
      toast(`Documento "${documentToExempt.title}" isento com sucesso!`);
      handleCloseExemptionModal();
      getSubcontractorDetails(); // Re-busca os dados
    } catch (error: any) {
      console.error("Erro ao isentar documento:", error.response?.data || error.message);
      toast("Erro ao isentar o documento.");
    }
  };

  const formatarData = (dataString?: string) => {
    if (!dataString) return "-";
    const data = new Date(dataString);
    if (isNaN(data.getTime())) return "-";
    return data.toLocaleDateString("pt-BR", { timeZone: 'UTC' });
  };

  const getStatusClass = (status: string) => {
    if (status === "PENDENTE" || status === "PENDENTE_ISENCAO") return "text-yellow-500";
    if (status === "EM_ANALISE") return "text-blue-500";
    if (status === "APROVADO" || status === "APROVADO_IA") return "text-green-600";
    if (status === "REPROVADO" || status === "REPROVADO_IA") return "text-red-600";
    if (status === "VENCIDO") return "text-orange-500";
    if (status === "ISENTO") return "text-blue-500";
    return "";
  };
  
  if (isLoading) {
    return <div className="p-10">Carregando detalhes do subcontratado...</div>;
  }

  return (
    <div className="w-full flex flex-col gap-5 px-10 relative bottom-[4vw]">
      {/* Card de Informações e Abas */}
      <div className="bg-white p-5 rounded-md shadow-md w-full relative">
        <span className="text-neutral-500 text-[14px]">
          Detalhes do Subcontratado:
        </span>
        <div>
          <h2 className="text-[#34495E] text-[20px] font-semibold underline">
            {subcontractorInfo?.nameSubcontractor || "Não encontrado"}
          </h2>
          <p className="text-neutral-600">{subcontractorInfo?.responsible}</p>
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
        </div>
      </div>

      {/* Container de Conteúdo (Documentos ou Colaboradores) */}
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
                       {/* Coluna Status */}
                       <div>
                         <div className="flex items-center gap-2">
                           {doc.status === "REPROVADO" || doc.status === "REPROVADO_IA" ? <Ban className="w-4 h-4 text-red-500" />
                           : doc.status === "VENCIDO" ? <AlertCircle className="w-4 h-4 text-orange-500" />
                           : doc.status === "APROVADO" || doc.status === "APROVADO_IA" ? <CheckCircle className="w-4 h-4 text-green-600" />
                           : doc.status === "ISENTO" ? <FileX2 className="w-4 h-4 text-blue-500" />
                           : doc.status === "PENDENTE_ISENCAO" ? <AlertCircle className="w-4 h-4 text-yellow-500" />
                           : doc.status === "EM_ANALISE" ? <AlertCircle className="w-4 h-4 text-blue-500" />
                           : null}
                           <span className={`text-sm font-medium ${getStatusClass(doc.status)}`}>
                             {doc.status.replace(/_/g, " ")}
                           </span>
                         </div>
                       </div>
                       
                       {/* Coluna Documento */}
                       <div className="col-span-1">
                         <h3 className="text-[16px] font-medium">{doc.title}</h3>
                       </div>
 
                       {/* Coluna Bloqueia */}
                       <div className="flex justify-center items-center">
                         {doc.bloqueia ? <Lock className="w-4 h-4 text-red-500" /> : <Unlock className="w-4 h-4 text-green-600" />}
                       </div>
 
                       {/* Outras Colunas */}
                       <div className="text-center"><span className="text-sm text-neutral-600">{formatarData(doc.uploadDate)}</span></div>
                       <div className="text-center"><span className="text-sm text-neutral-600">{formatarData(doc.lastCheck)}</span></div>
                       <div className="text-center"><span className="text-sm text-neutral-600">{formatarData(doc.expirationDate)}</span></div>
                       
                       {/* Coluna Ações */}
                       <div className="flex justify-center relative">
                         <button
                           onClick={() => setOpenMenuDocumentId(openMenuDocumentId === doc.id ? null : doc.id)}
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
                                 <Eye className="w-5 h-5 text-base" /> Visualizar
                               </button>
                             )}
                             <button
                               onClick={() => handleOpenUploadModal(doc.id, doc.title)}
                               className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                             >
                               <Upload className="w-5 h-5 text-base" /> Enviar
                             </button>
                             <button
                               type="button"
                               onClick={() => handleOpenExemptionModal(doc.id, doc.title)}
                               className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 flex items-center gap-2"
                             >
                               <FileX2 className="w-5 h-5 text-base" color="#b31933" /> Isentar
                             </button>
                           </div>
                         )}
                       </div>
                     </div>
                   ))
                 ) : (
                   <span className="text-neutral-400">Nenhum documento encontrado</span>
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
                    to={`/sistema/detailsEmployees/${employee.id}`} // Rota para detalhes do colaborador
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
      </div>

      {/* Modals (sem alterações) */}
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
                <h2 className="text-xl font-semibold mb-4 text-[#34495E]">Justificativa para Isenção</h2>
                <p className="text-sm text-neutral-600 mb-4">
                    Você está isentando o documento: <span className="font-medium text-neutral-800">{documentToExempt.title}</span>
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
  );
}