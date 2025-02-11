// DocumentSelectedBox.tsx

import { propsDocument } from "@/types/interfaces";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import { useState } from "react";
// import { useDocument } from "@/context/Document-provider";
// import { useState } from "react";

export function DocumentSelectedBox() {
  // const [isLoading, setIsLoading] = useState(false);
  const [document] = useState<propsDocument[]>([])

  // if (isLoading) {
  //   return (
  //     <div className="flex justify-center items-center h-full">
  //       {/* Aqui você pode usar um componente de loading */}
  //     </div>
  //   );
  // }

  return (
    <div className="w-[30vw] rounded-md border p-2 shadow-md">
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
          <option value="" disabled>
            Selecionados
          </option>
          {document.map((doc) => (
            <option key={doc.idDocumentMatrix} value={doc.idDocumentMatrix}>
              {doc.name}
            </option>
          ))}
        </select>
      </div>
      <ScrollArea className="h-[25vh] w-full">
        <div className="flex flex-col gap-3">
          {document.length > 0 ? (
            document.map((doc) => (
              <div key={doc.idDocumentMatrix}>
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
