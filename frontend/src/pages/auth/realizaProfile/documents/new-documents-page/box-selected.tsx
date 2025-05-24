import { ScrollArea } from "@/components/ui/scroll-area";
import { useDocument } from "@/context/Document-provider";
import { propsDocument } from "@/types/interfaces";
import { Search } from "lucide-react";
import { useState } from "react";

interface boxSelectedProps {
    documents: propsDocument[]
}

export function BoxSelected({documents}: boxSelectedProps) {
     const [checkedDocs, setCheckedDocs] = useState<string[]>([]); 
      const { setDocuments } = useDocument();
    
      const toggleCheckbox = (id: string, document: propsDocument) => {
        setCheckedDocs((prev) =>
          prev.includes(id) ? prev.filter((docId) => docId !== id) : [...prev, id]
        );
    
    
        setDocuments((prevDocuments) => {
          if (prevDocuments.some((doc) => doc.idDocumentation === id)) {
            return prevDocuments.filter((doc) => doc.idDocumentation !== id);
          } else {
            return [...prevDocuments, document];
          }
        });
      };

    return(
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
                  className="cursor-pointer rounded-sm p-1 hover:bg-gray-200 flex items-center gap-2"
                  onClick={() => toggleCheckbox(document.idDocument, document)} 
                >
                  <input
                    type="checkbox"
                    checked={checkedDocs.includes(document.idDocument)}
                    onChange={() => {}} 
                  />
                  <span>{document.title || "Documento"}</span>
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
    )
}