import { useBranch } from "@/context/Branch-provider";
import { propsDocument } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import axios from "axios";
import { Search } from "lucide-react";
import { Blocks } from "react-loader-spinner";
import { toast } from "sonner";

interface DocumentBoxProps {
  isLoading: boolean;
  documents: propsDocument[]
  onSelectDocument: (documentId: string) => void; 
}

export function DocumentBox({
  isLoading,
  documents = [],
  onSelectDocument,
}: DocumentBoxProps) {
  const { selectedBranch } = useBranch();

  const selectDocument = async (documentId: string) => {
    try {
      console.log("selecionando documento");
      await axios.post(
        `${ip}/document/branch/${selectedBranch?.idBranch}/document-matrix?documentId=${documentId}`,
      );
      console.log("documento selecionado com sucesso");
      toast.success("Documento selecionado enviado com sucesso");


      onSelectDocument(documentId);
    } catch (err) {
      console.log("erro ao selecionar documento", err); 
      toast.error("Erro ao selecionar documento");
    }
  };

  if (isLoading) {
    return (
      <div className="flex w-[30vw] flex-col gap-5 rounded-md border p-2 shadow-md">
        <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
          <Search className="text-gray-500" />
          <input
            className="w-full border-none focus:border-none focus:outline-none focus:ring-0"
            placeholder="Pesquisar Documento"
          />
        </div>
        <ScrollArea className="flex h-[25vh] w-full items-center justify-center">
          <Blocks height="60" width="60" color="#4fa94d" visible />
        </ScrollArea>
      </div>
    );
  }

  return (
    <div className="mr-2 flex h-[45vh] w-[30vw] flex-col gap-5 rounded-md border p-2 shadow-md">
      <h2 className="text-realizaBlue text-lg">Matriz:</h2>
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <Search className="text-gray-500" />
        <input
          className="w-full border-none focus:border-none focus:outline-none focus:ring-0"
          placeholder="Pesquisar Documento"
        />
      </div>
      <ScrollArea className="h-[25vh] w-full overflow-auto">
        <div className="flex flex-col gap-3 p-2">
          {Array.isArray(documents) && documents.length > 0 ? (
            documents.map((document: any) => (
              <div
                key={document.idDocument}
                onClick={() => {
                  if(document.idDocumentMatrix) {
                    selectDocument(document.idDocumentMatrix);
                  }
                }}
                className="cursor-pointer rounded-sm p-1 hover:bg-gray-200"
              >
                <span>{document.name}</span>
              </div>
            ))
          ) : (
            <p className="text-sm text-gray-500">
              Nenhum documento encontrado.
            </p>
          )}
        </div>
      </ScrollArea>
    </div>
  );
}
