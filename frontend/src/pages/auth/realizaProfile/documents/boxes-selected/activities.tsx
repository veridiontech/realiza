// import {
//   AlertDialog,
//   AlertDialogAction,
//   AlertDialogCancel,
//   AlertDialogContent,
//   AlertDialogFooter,
//   AlertDialogHeader,
//   AlertDialogTitle,
//   AlertDialogTrigger,
// } from "@/components/ui/alert-dialog";
// import { BoxNonSelected } from "../new-documents-page/box-non-selected";
import { useDocument } from "@/context/Document-provider";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
// import { BoxSelected } from "../new-documents-page/box-selected";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { propsDocument } from "@/types/interfaces";
import { ScrollArea } from "@/components/ui/scroll-area";

export function ActivitiesBox() {
  // const [activitiesCheck, setCheckedActivities] = useState<string | null>(null);
  const [checkedDocs, setCheckedDocs] = useState<string[]>([]);
  const { documents, nonSelected, activitieSelected, setDocuments } =
    useDocument();
  const [documentsByActivitie, setDocumentByActivitie] = useState([]);
  const [activities, setActivities] = useState<any>([]);
  const { selectedBranch } = useBranch();
  // const [activitiesAll, setActivitiesAll] = useState([])

  const getDocument = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const resSelected = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      console.log("atividades", resSelected.data);
      setActivities(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar documentos:", err);
    }
  };

  const getDocumentByActivitie = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected?.idActivity}`,
      );
      console.log("documento das atividades:", res.data);

      setDocumentByActivitie(res.data);
    } catch (err) {
      console.log("erro ao buscar por documentos:", err);
    }
  };

  const filterIdDocuments = nonSelected.map(
    (document) => document.idDocumentation,
  );

  const filterIdDocumentsSelected = documents.map(
    (document) => document.idDocumentation,
  );

  console.log("ids dos documentos", filterIdDocuments);
  console.log("ids dos documentos selecionados", filterIdDocumentsSelected);

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getDocument();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (activitieSelected?.idActivity) {
      getDocumentByActivitie();
    }
  }, [activitieSelected?.idActivity]);

  console.log("testando atividade selecionada:", activitieSelected);

  const toggleCheckbox = (id: string, document: propsDocument) => {
    setCheckedDocs((prev) =>
      prev.includes(id) ? prev.filter((docId) => docId !== id) : [...prev, id],
    );

    setDocuments((prevDocuments) => {
      if (prevDocuments.some((doc) => doc.idDocumentation === id)) {
        return prevDocuments.filter((doc) => doc.idDocumentation !== id);
      } else {
        return [...prevDocuments, document];
      }
    });
  };

  // const getActivitiesAll = async() => {
  //   try{
  //       const res = await axios.get(`${ip}/contract/activity`)
  //       setActivitiesAll(res.data)
  //   }catch(err) {
  //       console.log("erro ao buscar todas as atividades:", err);
  //   }
  // }

  // useEffect(() => {
  //   getActivitiesAll()
  // }, [])

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxActivities activities={activities} />
      </div>
      {/* <div className="flex flex-col gap-5">
        <div>
          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-4 transition-all duration-300 ${nonSelected.length === 0 ? "cursor-not-allowed bg-gray-300 text-gray-500" : "bg-realizaBlue text-white"}`}
              disabled={documents.length === 0}
            >
              Confirmar Seleção
            </AlertDialogTrigger>
            <AlertDialogContent>
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
                    <p>Nenhuma atividade encontrada.</p>
                  )}
                </ul>
              </div>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancelar</AlertDialogCancel>
                <AlertDialogAction
                  onClick={() =>
                    sendDocuments(false, filterIdDocumentsSelected)
                  }
                >
                  Confirmar
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </div>
      </div> */}
      <div>
        <div className="w-[35vw] border p-5 shadow-md">
          <div className="flex items-center gap-2 rounded-md border p-2">
            <Search />
            <input className="outline-none" />
          </div>
          <ScrollArea className="h-[30vh]">
            {Array.isArray(documentsByActivitie) &&
            documentsByActivitie.length > 0 ? (
              documentsByActivitie.map((document: any) => (
                <div
                  key={document.idDocument}
                  className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
                  onClick={() => toggleCheckbox(document.idDocument, document)}
                >
                  <input
                    type="checkbox"
                    checked={checkedDocs.includes(document.idDocument)}
                    onChange={() => {}}
                  />
                  <span>{document.documentTitle || "Documento"}</span>
                </div>
              ))
            ) : (
              <p className="text-sm text-gray-500">
                Nenhum documento encontrado.
              </p>
            )}
          </ScrollArea>
        </div>
      </div>
    </div>
  );
}
