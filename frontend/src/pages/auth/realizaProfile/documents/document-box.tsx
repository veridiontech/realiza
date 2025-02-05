// DocumentBox.tsx

import { ScrollArea } from "@radix-ui/react-scroll-area";
import { useDocument } from "@/context/Document-provider"; // Importe o hook do contexto
import { Search } from "lucide-react";
import { Blocks } from "react-loader-spinner";

interface DocumentBoxProps {
  documents: { idDocumentMatrix: string; name: string }[];
  isLoading: boolean;
}

export function DocumentBox({ documents, isLoading }: DocumentBoxProps) {
  const { setDocument } = useDocument(); // Acessa a função do contexto para atualizar o documento

  const handleClickDocument = (doc: { idDocumentMatrix: string; name: string }) => {
    setDocument(doc); // Atualiza o documento no contexto
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
          <div className="flex items-center justify-center">
          <Blocks
              height="60"
              width="60"
              color="#4fa94d"
              ariaLabel="blocks-loading"
              wrapperStyle={{}}
              wrapperClass="blocks-wrapper"
              visible={true}
            />
          </div>
        </ScrollArea>
      </div>
    );
  }

  return (
    <div className="flex w-[30vw] flex-col gap-5 rounded-md border p-2 shadow-md">
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <Search className="text-gray-500" />
        <input
          className="w-full border-none focus:border-none focus:outline-none focus:ring-0"
          placeholder="Pesquisar Documento"
        />
      </div>
      <ScrollArea className="h-[25vh] w-full">
        <div className="flex flex-col gap-3">
          {documents.map((doc) => (
            <div key={doc.idDocumentMatrix} onClick={() => handleClickDocument(doc)} className="cursor-pointer">
              <h3>{doc.name}</h3>
            </div>
          ))}
        </div>
      </ScrollArea>
    </div>
  );
}
