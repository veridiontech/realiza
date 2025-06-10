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
import { useEffect, useState } from "react";
import { useBranch } from "@/context/Branch-provider";
import { propsDocument } from "@/types/interfaces";

export function TrabalhistaBox() {
  const { setDocuments, documents, setNonSelected, nonSelected } =
    useDocument();
  const [notSelectedDocument, setNotSelectedDocument] = useState([]);
  const [selectedDocument, setSelectedDocument] = useState<any>([]);
  const { selectedBranch } = useBranch();
  const [isLoading, setIsLoading] = useState(false)

  const getDocument = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true)
    try {
      const resSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
          params: {
            documentTypeName: "TRABALHISTA",
            isSelected: true,
          },
        },
      );
      const resNonSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
          params: {
            documentTypeName: "TRABALHISTA",
            isSelected: false,
          },
        },
      );
      console.log("teste", resSelected.data);
      console.log("teste", resNonSelected.data);

      setNotSelectedDocument(resNonSelected.data);
      setSelectedDocument(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar documentos:", err);
    } finally {
      setIsLoading(true)
    }
  };

  const sendDocuments = async (isSelected: boolean, idDocumentation: string[]) => {
    // const 
    const tokenFromStorage = localStorage.getItem("tokenClient")
    try {
      console.log("selecionando documentos não selecionados:", idDocumentation);
      await axios.post(`${ip}/document/branch/document-matrix/update`, idDocumentation, {
        headers: {
          Authorization: `Bearer ${tokenFromStorage}`,
        },
        params: {
          isSelected,
        }
      })
      clearArray()
      pullDatas()
    } catch (err) {
      console.log("erro ao enviar documento", err);

    }
  }

  const filterIdDocuments = nonSelected
    .map((document: propsDocument) => document.idDocumentation)
  // .map((document) => document.idDocumentation);

  const filterIdDocumentsSelected = documents
    .map((document: propsDocument) => document.idDocumentation)

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getDocument();
    }
  }, [selectedBranch?.idBranch]);


  const clearArray = () => {
    setDocuments([])
    setNonSelected([])
    setNotSelectedDocument([])
    setSelectedDocument([])
  }

  const pullDatas = () => {
    getDocument()
  }

  //

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxNonSelected documents={notSelectedDocument} isLoading={isLoading} />
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
            <AlertDialogContent className="max-h-[400px] overflow-y-auto">
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {nonSelected.length > 0 ? (
                    nonSelected.map((doc: propsDocument) => (
                      <li key={doc.idDocumentation}>{doc.title}</li>
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
            <AlertDialogContent className="max-h-[400px] overflow-y-auto">
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {documents.length > 0 ? (
                    documents.map((doc) => (
                      <li key={doc.idDocumentation}>{doc.title}</li>
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
        <BoxSelected documents={selectedDocument} isLoading={isLoading} />
      </div>
    </div>
  );
}
