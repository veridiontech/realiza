import { userBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { Blocks } from "react-loader-spinner";

interface DocumentBoxProps {
  isLoading: boolean;
}

export function DocumentBox({ isLoading }: DocumentBoxProps) {
  const { branch } = userBranch();
  const [documents, setDocuments] = useState<{ idDocument: string; title: string }[]>([]);

  const getDocuments = async () => {
    if (!branch?.idBranch) return;

    try {
      const res = await axios.get(`${ip}/document/branch/${branch.idBranch}/document-matrix`);
      setDocuments(res.data || []);
      console.log("documentos da filial", res.data);
    } catch (err) {
      console.log("erro ao filtrar documentos:", err);
    }
  };
  
  useEffect(() => {
    if (branch?.idBranch) {
      getDocuments();
    }
  }, [branch?.idBranch]);
  

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
          {documents.length > 0 ? (
            documents.map((document) => (
              <div key={document.idDocument}>{document.title}</div>
            ))
          ) : (
            <p className="text-gray-500">Nenhum documento encontrado.</p>
          )}
        </div>
      </ScrollArea>
    </div>
  );
}
