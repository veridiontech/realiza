import { propsActivities, propsDocument } from "@/types/interfaces";
import { createContext, useContext, useState } from "react";

interface DocumentContextProps {
  document: propsDocument | null;
  setDocument: React.Dispatch<React.SetStateAction<propsDocument | null>>;
  documents: propsDocument[];
  setDocuments: React.Dispatch<React.SetStateAction<propsDocument[]>>;
  nonSelected: propsDocument[];
  setNonSelected: React.Dispatch<React.SetStateAction<propsDocument[]>>;
  activitieSelected: propsActivities | null;  // Alterando para armazenar uma atividade ou null
  setActivitiesSelected: React.Dispatch<React.SetStateAction<propsActivities | null>>;  // Alterando para aceitar uma atividade ou null
}

const DocumentContext = createContext<DocumentContextProps | undefined>(
  undefined,
);

export function useDocument() {
  const context = useContext(DocumentContext);
  if (!context) {
    throw new Error("erro no provider de documentos");
  }
  return context;
}

export function DocumentProvider({ children }: { children: React.ReactNode }) {
  const [document, setDocument] = useState<propsDocument | null>(null);
  const [documents, setDocuments] = useState<propsDocument[]>([]);
  const [nonSelected, setNonSelected] = useState<propsDocument[]>([]);
  const [activitieSelected, setActivitiesSelected] = useState<propsActivities | null>(null)

  return (
    <DocumentContext.Provider
      value={{
        document,
        setDocument,
        documents,
        setDocuments,
        nonSelected,
        setNonSelected,
        activitieSelected,
        setActivitiesSelected
      }}
    >
      {children}
    </DocumentContext.Provider>
  );
}
