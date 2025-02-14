import { propsDocument } from "@/types/interfaces";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import { Blocks } from "react-loader-spinner";

interface DocumentSelectedBoxProps {
  selectedDocuments: propsDocument[];
  isLoading: boolean;
}

export function DocumentSelectedBox({
  selectedDocuments,
  isLoading,
}: DocumentSelectedBoxProps) {
  if (isLoading) {
    return (
      <div className="w-[30vw] rounded-md border p-2 shadow-md">
        <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
          <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
            <option value="" disabled>
              Selecionados
            </option>
            {selectedDocuments.map((doc) => (
              <option key={doc.idDocumentMatrix} value={doc.idDocumentMatrix}>
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
    <div className="w-[30vw] rounded-md border p-2 shadow-md">
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
          <option value="" disabled>
            Selecionados
          </option>
          {selectedDocuments.map((doc) => (
            <option key={doc.idDocumentMatrix} value={doc.idDocumentMatrix}>
              {doc.name}
            </option>
          ))}
        </select>
      </div>
      <ScrollArea className="h-[25vh] w-full overflow-auto">
        <div className="flex flex-col gap-3 p-5">
          {selectedDocuments.length > 0 ? (
            selectedDocuments.map((doc) => (
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
