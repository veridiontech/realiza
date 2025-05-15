import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";
import { Blocks } from "react-loader-spinner";

export function ActivitiesBox() {
  // const [checkedDocs, setCheckedDocs] = useState<string[]>([]); // Array para armazenar os documentos selecionados
  const [activitieSelected, setActivitieSelected] = useState<any>(null);
  const [activities, setActivities] = useState<any>([]);
  const [documentsByActivitie, setDocumentsByActivitie] = useState([]);
  const { selectedBranch } = useBranch();
  const [loadingDocumentId, setLoadingDocumentId] = useState<string | null>(
    null,
  );

  const getActivitie = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const resSelected = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        },
      );
      setActivities(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar atividades:", err);
    }
  };

  // // Função para buscar todos os documentos da filial
  // const getAllDocuments = async () => {
  //   setLoadingAllDocuments(true);
  //   try {
  //     const tokenFromStorage = localStorage.getItem("tokenClient");
  //     const res = await axios.get(`${ip}/document/branch/filtered-branch`, {
  //       params: {
  //         idSearch: selectedBranch?.idBranch,
  //         size: 1000,
  //         headers: { Authorization: `Bearer ${tokenFromStorage}` }
  //       },
  //     });
  //     console.log(res.data.content);
  //     setActivitiesAll(res.data.content);
  //   } catch (err) {
  //     console.log("Erro ao buscar todos documentos da filial:", err);
  //   } finally {
  //     setLoadingAllDocuments(false);
  //   }
  // };

  const getDocumentByActivitie = async (id: string) => {
    console.log("id selecionado", id);

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected?.idActivity}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        },
      );
      console.log(res.data.content);
      console.log(res.data);
      setDocumentsByActivitie(res.data);
    } catch (err) {
      console.log("Erro ao buscar documentos da atividade:", err);
    }
  };

  const removeDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
  ) => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.post(
        `${ip}/contract/activity/remove-document-from-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      console.log(res.data);
      toast.success("Sucesso ao remover documento");
    } catch (err: any) {
      console.log("Erro ao remover documento:", err);
      toast.error("Erro ao remover documento");
    }
  };

  const addDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
  ) => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.post(
        `${ip}/contract/activity/add-document-to-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );
      console.log(res.data);
      toast.success("Sucesso ao adicionar documento à atividade");
    } catch (err: any) {
      console.log("Erro ao adicionar documento:", err);
      toast.error("Erro ao adicionar documento à atividade");
    }
  };

const handleSelectDocument = async (document: any, idDocument: string) => {
  const idActivity = activitieSelected?.idActivity;
  if (!idActivity) return;

  setLoadingDocumentId(idDocument);

  try {
    if (document.selected === true) {
      await removeDocumentByActivitie(idActivity, idDocument);

      // Atualiza visualmente o documento removido
      setDocumentsByActivitie((prevDocs: any) =>
        prevDocs.map((doc: any) =>
          doc.idDocument === idDocument
            ? { ...doc, selected: false }
            : doc
        )
      );
    } else {
      await addDocumentByActivitie(idActivity, idDocument);

      setDocumentsByActivitie((prevDocs: any) =>
        prevDocs.map((doc: any) =>
          doc.idDocument === idDocument
            ? { ...doc, selected: true }
            : doc
        )
      );
    }

    // Você ainda pode manter a chamada real de atualização, se quiser garantir os dados
    // await getDocumentByActivitie(idActivity);
  } finally {
    setLoadingDocumentId(null);
  }
};

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getActivitie();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (activitieSelected?.idActivity) {
      getDocumentByActivitie(activitieSelected.idActivity);
    }
  }, [activitieSelected]);

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxActivities
          activities={activities}
          onSelectActivitie={(activitie: any) =>
            setActivitieSelected(activitie)
          }
        />
      </div>
      <div>
        <div className="w-[35vw] border p-5 shadow-md">
          <div className="flex items-center gap-2 rounded-md border p-2">
            <Search />
            <input className="outline-none" />
          </div>
          <ScrollArea className="h-[30vh]">
            {documentsByActivitie.map((document: any) => (
              <div
                key={document.idDocument}
                className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
              >
                {loadingDocumentId === document.idDocument ? (
                  <Blocks
                    height="50"
                    width="50"
                    color="#4fa94d"
                    ariaLabel="blocks-loading"
                    visible={true}
                  />
                ) : (
                  <input
                    type="checkbox"
                    checked={document.selected === true}
                    onChange={() =>
                      handleSelectDocument(document, document.idDocument)
                    }
                    className="cursor-pointer"
                  />
                )}

                <span>{document.documentTitle || "Documento"}</span>
              </div>
            ))}
          </ScrollArea>
        </div>
      </div>
    </div>
  );
}
