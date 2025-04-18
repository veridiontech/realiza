import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { BoxNonSelected } from "../new-documents-page/box-non-selected";
// import { propsDocument } from "@/types/interfaces";
import { BoxSelected } from "../new-documents-page/box-selected";
import { useDocument } from "@/context/Document-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import { propsDocument } from "@/types/interfaces";
import { boolean } from "zod";

export function ThirdCompany() {
  const { documents, nonSelected } = useDocument();
  const [notSelectedDocument, setNotSelectedDocument] = useState([]);
  const [selectedDocument, setSelectedDocument] = useState<any>([]);
  const { selectedBranch } = useBranch();

  const mockDocumentsNonSelected: propsDocument[] = [
    { idDocument: "1", name: "Documento 1" },
    { idDocument: "2", name: "Documento 2" },
    { idDocument: "3", name: "Documento 3" },
    { idDocument: "4", name: "Documento 4" },
  ];

  const mockDocumentsSelected: propsDocument[] = [
    { idDocument: "1", name: "Documento 11231231" },
    { idDocument: "2", name: "Documento 21231231" },
    { idDocument: "3", name: "Documento 31231231" },
    { idDocument: "4", name: "Documento 4231321" },
  ];

  const getDocument = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const resSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: { documentGroupName: "Documento empresa", isSelected: true },
        },
      );
      const resNonSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: { documentGroupName: "Documento empresa", isSelected: false },
        },
      );
      console.log("teste", resSelected.data);
      console.log("teste", resNonSelected.data);

      setNotSelectedDocument(resNonSelected.data);
      setSelectedDocument(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar documentos:", err);
    }
  };
  const filterIdDocuments = nonSelected
  .map((document) => document.idDocument)
  // .map((document) => document.idDocument);

  const filterIdDocumentsSelected = documents
  .map((document) => document.idDocument)
  // .map((document) => document.idDocument);

console.log("ids dos documentos", filterIdDocuments);
console.log("ids dos documentos selecionados", filterIdDocumentsSelected);
  

// Envio de documentos nao selecionados
  const sendDocuments = async(isSelected: boolean, idDocumentation: string[]) => {
    // const 
    const token = localStorage.getItem("tokenClient")
    try {
      console.log("selecionando documentos não selecionados:", filterIdDocuments);
      await axios.post(`${ip}/document/branch/document-matrix/update`, idDocumentation, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          isSelected: {isSelected},
        }
      })

      
    }catch(err) {
      console.log("erro ao enviar documento", err );
      
    }
  }

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getDocument();
    }
  }, [selectedBranch?.idBranch]);

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxNonSelected documents={mockDocumentsNonSelected} />
      </div>
      <div className="flex flex-col gap-5">
        <div>
          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-4 transition-all duration-300 ${nonSelected.length === 0 ? "cursor-not-allowed bg-gray-300 text-gray-500" : "bg-realizaBlue text-white"}`}
              disabled={nonSelected.length === 0}
            >
              Confirmar Seleção
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {nonSelected.length > 0 ? (
                    nonSelected.map((doc) => (
                      <li key={doc.idDocument}>{doc.name}</li>
                    ))
                  ) : (
                    <p>Nenhum documento selecionado.</p>
                  )}
                </ul>
              </div>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={() => sendDocuments(true, filterIdDocuments)}>Confirmar</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
        <div>
          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-3 transition-all duration-300 ${documents.length === 0 ? "cursor-not-allowed bg-red-300 text-red-500" : "bg-red-600 text-white"}`}
              disabled={documents.length === 0}
            >
              Confirmar Remoção
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {documents.length > 0 ? (
                    documents.map((doc) => (
                      <li key={doc.idDocument}>{doc.name}</li>
                    ))
                  ) : (
                    <p>Nenhum documento selecionado.</p>
                  )}
                </ul>
              </div>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={() => sendDocuments(false, filterIdDocumentsSelected)}>Confirmar</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </div>
      <div>
        <BoxSelected documents={mockDocumentsSelected} />
      </div>
    </div>
  );
}
