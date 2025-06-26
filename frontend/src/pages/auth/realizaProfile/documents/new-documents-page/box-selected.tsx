import { ScrollArea } from "@/components/ui/scroll-area";
import { useDocument } from "@/context/Document-provider";
import { propsDocument } from "@/types/interfaces";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { Blocks } from "react-loader-spinner";

interface boxSelectedProps {
  documents: propsDocument[];
  isLoading: boolean;
}

export function BoxSelected({ documents, isLoading }: boxSelectedProps) {
  const { documents: selectedDocs, setDocuments } = useDocument();
  const [checkedDocs, setCheckedDocs] = useState<string[]>([]);

  useEffect(() => {
    setCheckedDocs(selectedDocs.map((doc: any) => doc.idDocument));
  }, [selectedDocs]);

  const toggleCheckbox = (id: string, document: propsDocument, isChecked: boolean) => {
    setCheckedDocs((prev) =>
      isChecked ? [...prev, id] : prev.filter((docId) => docId !== id)
    );

    setDocuments((prevDocuments) => {
      if (isChecked) {
        return [...prevDocuments, document];
      } else {
        return prevDocuments.filter((doc: any) => doc.idDocument !== id);
      }
    });
  };

  if (isLoading) {
    return (
      <div className="border p-5 shadow-md w-[35vw]">
        <div className="flex items-center gap-2 rounded-md border p-2">
          <Search />
          <input className="outline-none" />
        </div>
        <div className="h-[30vh] flex items-center justify-center">
          <Blocks height="80" width="80" color="#4fa94d" ariaLabel="blocks-loading" visible={true} />
        </div>
      </div>
    );
  }

  return (
    <div className="relative w-fit">
      <span className="absolute -top-5 left-2 text-sm text-gray-500">
          Documentos alocados
        </span>
    <div className="border p-5 shadow-md w-[35vw]">
      <div className="flex items-center gap-2 rounded-md border p-2">
        <Search />
        <input className="outline-none" />
      </div>
      <ScrollArea className="h-[30vh]">
        <div>
          {Array.isArray(documents) && documents.length > 0 ? (
            documents.map((document: any) => (
              <div
                key={document.idDocument}
                className="rounded-sm p-1 hover:bg-gray-200 flex items-center gap-2"
              >
                <input
                  type="checkbox"
                  checked={checkedDocs.includes(document.idDocument)}
                  onChange={(e) =>
                    toggleCheckbox(document.idDocument, document, e.target.checked)
                  }
                />
                <span>{document.title || "Documento"}</span>
              </div>
            ))
          ) : (
            <p className="text-sm text-gray-500">Nenhum documento encontrado.</p>
          )}
        </div>
        
      </ScrollArea>
    </div>
    </div>
  );
}
