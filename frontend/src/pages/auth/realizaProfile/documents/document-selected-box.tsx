import { useState, useEffect } from "react";
import { propsDocument } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import axios from "axios";
import { Blocks } from "react-loader-spinner";

interface DocumentSelectedBoxProps {
  selectedDocuments: propsDocument[];
  isLoading: boolean;
}

export function DocumentSelectedBox({
  selectedDocuments: initialDocuments,
  isLoading,
}: DocumentSelectedBoxProps) {
  // Cria um estado local para os documentos
  const [documents, setDocuments] = useState<propsDocument[]>(initialDocuments);

  // Atualiza o estado local caso a prop mude
  useEffect(() => {
    setDocuments(initialDocuments);
  }, [initialDocuments]);

  const deleteMoveDocument = async (documentId: string) => {
    console.log("Tentando deletar o documento com o ID:", documentId);
    setDocuments((prevDocs) =>
      prevDocs.filter((doc) => doc.documentId !== documentId),
    );

    try {
      const req = await axios.delete(
        `${ip}/document/branch/document-matrix?documentId=${documentId}`,
      );
      toast.success("Documento movido com sucesso");
      console.log("Resposta da API:", req.data);
    } catch (err) {
      toast.error("Erro ao mover documento");
      console.error("Erro ao mover documento:", err);
    }
  };

  if (isLoading) {
    return (
      <div className="w-[30vw] rounded-md border p-2 shadow-md">
        <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
          <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
            <option value="" disabled>
              Selecionados
            </option>
            {documents.map((doc) => (
              <option key={doc.idDocument} value={doc.idDocument}>
                {doc.name}
              </option>
            ))}
          </select>
        </div>
        <ScrollArea className="flex h-[25vh] w-full items-center justify-center">
          <Blocks height="60" width="60" color="#4fa94d" visible />
        </ScrollArea>
      </div>
    );
  }

  return (
    <div className="h-[45vh] w-[30vw] rounded-md border p-2 shadow-md">
      <h2 className="text-realizaBlue m-1 pb-2 text-lg">Acompanhando:</h2>
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
          <option value="" disabled>
            Selecionados
          </option>
          {documents.map((doc) => (
            <option key={doc.idDocument} value={doc.idDocument}>
              {doc.name}
            </option>
          ))}
        </select>
      </div>
      <ScrollArea className="h-[25vh] w-full overflow-auto">
        <div className="flex flex-col gap-3 p-5">
          {documents.length > 0 ? (
            documents.map((doc) => (
              <div
                className="cursor-pointer rounded-md p-2 hover:bg-gray-200"
                key={doc.idDocument}
                onClick={() => {

                    deleteMoveDocument(doc.documentId);
                  
                }}
              >
                <h3>{doc.name}</h3>
              </div>
            ))
          ) : (
            <p>Nenhum documento selecionado.</p>
          )}
        </div>
      </ScrollArea>
    </div>
  );
}
