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
import { BoxSelected } from "../new-documents-page/box-selected";
import { useDocument } from "@/context/Document-provider";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { propsDocument } from "@/types/interfaces";
import { ValidateSection } from "../validate-matrix/validate-section";

export function ThirdCollaborators() {
  const { setDocuments, documents, setNonSelected, nonSelected } = useDocument();
  const [notSelectedDocument, setNotSelectedDocument] = useState([]);
  const [selectedDocument, setSelectedDocument] = useState<any>([]);
  const { selectedBranch } = useBranch();
  const [isLoading, setIsLoading] = useState(false);
  const [replicate, setReplicate] = useState(false);

  const getDocument = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true);
    try {
      const resSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
          params: { documentTypeName: "SAUDE", isSelected: true },
        }
      );
      const resNonSelected = await axios.get(
        `${ip}/document/branch/document-matrix/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
          params: { documentTypeName: "SAUDE", isSelected: false },
        }
      );

      setNotSelectedDocument(resNonSelected.data);
      setSelectedDocument(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar documentos:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const filterIdDocuments = nonSelected.map((document: propsDocument) => document.idDocument);
  const filterIdDocumentsSelected = documents.map((document: propsDocument) => document.idDocument);

  const sendDocuments = async (
    isSelected: boolean,
    idDocumentation: string[],
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.post(
        `${ip}/document/branch/document-matrix/update`,
        idDocumentation,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
          params: { isSelected, replicate },
        }
      );
      clearArray();
      pullDatas();
    } catch (err) {
      console.log("erro ao enviar documento", err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getDocument();
    }
  }, [selectedBranch?.idBranch]);

  const clearArray = () => {
    setDocuments([]);
    setNonSelected([]);
    setNotSelectedDocument([]);
    setSelectedDocument([]);
  };

  const pullDatas = () => {
    getDocument();
  };

  return (
    <>
      <div className="flex items-center justify-center gap-10 p-10">
        <div>
          <BoxNonSelected documents={notSelectedDocument} isLoading={isLoading} />
        </div>
        <div className="flex flex-col gap-5">
          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-4 transition-all duration-300 ${
                nonSelected.length === 0
                  ? "cursor-not-allowed bg-gray-300 text-gray-500"
                  : "bg-realizaBlue text-white"
              }`}
              disabled={nonSelected.length === 0}
            >
              Alocar novos documentos
            </AlertDialogTrigger>
            <AlertDialogContent className="max-h-[400px] overflow-y-auto">
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {nonSelected.length > 0 ? (
                    nonSelected.map((doc: propsDocument) => (
                      <li key={doc.idDocument}>{doc.title}</li>
                    ))
                  ) : (
                    <p>Nenhum documento selecionado.</p>
                  )}
                </ul>
              </div>
              <div className="mt-4">
                <label className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={replicate}
                    onChange={() => setReplicate(!replicate)}
                    className="h-4 w-4"
                  />
                  Replicar alteração para outras filiais
                </label>
              </div>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={() => sendDocuments(true, filterIdDocuments, replicate)}>
                  Confirmar
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>

          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-3 transition-all duration-300 ${
                documents.length === 0
                  ? "cursor-not-allowed bg-red-300 text-red-500"
                  : "bg-red-600 text-white"
              }`}
              disabled={documents.length === 0}
            >
              Desalocar documentos
            </AlertDialogTrigger>
            <AlertDialogContent className="max-h-[400px] overflow-y-auto">
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
              </AlertDialogHeader>
              <div>
                <ul>
                  {documents.length > 0 ? (
                    documents.map((doc) => (
                      <li key={doc.idDocument}>{doc.title}</li>
                    ))
                  ) : (
                    <p>Nenhum documento selecionado.</p>
                  )}
                </ul>
              </div>
              <div className="mt-4">
                <label className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={replicate}
                    onChange={() => setReplicate(!replicate)}
                    className="h-4 w-4"
                  />
                  Replicar alteração para outras filiais
                </label>
              </div>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction onClick={() => sendDocuments(false, filterIdDocumentsSelected, replicate)}>
                  Confirmar
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
        <div>
          <BoxSelected documents={selectedDocument} isLoading={isLoading} />
        </div>
      </div>

      <div className="flex justify-center w-full px-10 pb-10">
        <div className="max-w-4xl w-full">
          <ValidateSection
            idBranch={selectedBranch?.idBranch!}
            documentTypeName="Saude"
            isSelected={true}
          />
        </div>
      </div>
    </>
  );
}
