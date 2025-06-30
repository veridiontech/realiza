import { ScrollArea } from "@/components/ui/scroll-area";
import { ip } from "@/utils/ip";
import axios from "axios";
import {
  Eye,
  Notebook,
  NotebookText,
  Plus,
  Upload,
  User,
  MoreVertical,
  FileX2,
} from "lucide-react";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AddDocument } from "../employee/modals/addDocumentForSupplier";
import { DocumentViewer } from "../employee/modals/viewDocumentForSupplier";

export function ContarctsByProvider() {
  const id = useParams();
  const [contracts, setContracts] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [collaborators, setCollaborators] = useState([]);
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

  const token = localStorage.getItem("tokenClient");

  const getContractsByProvider = async () => {
    try {
      const res = await axios.get(`${ip}/contract/supplier/filtered-supplier`, {
        params: { idSearch: id.id },
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("contrato", id);
      setContracts(res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  const getProvider = async () => {
    const res = await axios.get(`${ip}/supplier/${id.id}`);
    console.log(res.data);
    setProvider(res.data);
  };

  useEffect(() => {
    if (id) {
      getProvider();
      getContractsByProvider();
    }
  }, [id]);

  const getAllDatas = async (idContract: string, serviceName: string) => {
    try {
      const res = await axios.get(`${ip}/document/contract/${idContract}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setDocuments(res.data.documentDtos || []);
      setCollaborators(res.data.employeeDtos || []);
      setSelectedContractName(serviceName);
      setSearchTerm("");
    } catch (err) {
      console.log(err);
    }
  };

  const filteredDocuments = documents.filter(
    (doc: any) =>
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
  };

  const handleOpenViewerModal = (documentId: string) => {
    setSelectedDocumentId(documentId);
    setIsViewerModalOpen(true);
  };

  const handleCloseViewerModal = () => {
    setIsViewerModalOpen(false);
    setSelectedDocumentId(null);
  };

  // const exemptDocument = async (documentId: string, documentTitle: string) => {
  // try {
  //   await axios.patch(
  //     `${ip}/document/exempt/${documentId}`,
  //     {},
  //     {
  //       headers: {
  //         Authorization: `Bearer ${token}`,
  //       },
  //     }
  //   );
     
  //   };
  //   catch (error) {
  //    console.error("Erro:", error);

  //   alert(`Documento "${documentTitle}" isento com sucesso!`)
  // }


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
        <div className="bg-white p-5 rounded-md shadow-md w-full">
          <span className="text-neutral-500 text-[14px]">
            Contrato selecionado:
          </span>
          <div>
            <h2 className="text-[#34495E] text-[20px] font-semibold underline">
              {selectedContractName || "Nenhum contrato selecionado"}
            </h2>
          </div>
        </div>

        <div className="bg-white rounded-md p-5 border border-neutral-400 shadow-md flex gap-10 h-[50vh]">
          <div className="border border-neutral-400 rounded-md shadow-md p-5 w-[40vw] flex flex-col gap-6">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-2 text-[#34495E]">
                <NotebookText />
                <h2 className="text-[20px]">Documentos vinculados:</h2>
              </div>
              <div className="bg-realizaBlue p-2 rounded-md text-white hover:bg-blue-700 transition duration-300 cursor-pointer">
                <Plus />
              </div>
            </div>

            <input
              type="text"
              placeholder="Buscar por título ou empresa..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="border border-neutral-300 rounded-md px-3 py-2 text-sm w-full"
            />

            <div className="flex flex-col gap-4 overflow-y-auto max-h-[35vh] pr-2 relative">
              {filteredDocuments.length > 0 ? (
                filteredDocuments.map((doc: any) => (
                  <div
                    className="flex items-center justify-between"
                    key={doc.id}
                  >
                    <div>
                      <h3 className="text-[16px] font-medium">{doc.title}</h3>
                      <span className="text-[12px] text-neutral-600">
                        {doc.ownerName}
                      </span>
                    </div>

                    <div className="flex gap-3">
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
                        <div className="absolute right-10  mt-2 w-15 bg-white border border-gray-200 rounded-md shadow-lg z-50">
                          <button
                            onClick={() => handleOpenViewerModal(doc.id)}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Eye className="w-5 h-5 text-base" />
                          </button>
                          <button
                            onClick={() =>
                              handleOpenUploadModal(doc.id, doc.title)
                            }
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <Upload className="w-5 h-5 text-base" />
                          </button>
                          <button
                            type="button"
                            // onClick={() => exemptDocument(doc.id, doc.title)}
                            className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                          >
                            <FileX2
                              className="w-5 h-5 text-base"
                              color="#b31933"
                            />
                          </button>
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
        </div>
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
        />
      )}
    </div>
  );
}
