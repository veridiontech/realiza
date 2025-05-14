import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";

export function ActivitiesBox() {
  // const [checkedDocs, setCheckedDocs] = useState<string[]>([]); // Array para armazenar os documentos selecionados
  const [activitieSelected, setActivitieSelected] = useState<any>(null);
  const [activities, setActivities] = useState<any>([]);
  const [documentsByActivitie, setDocumentsByActivitie] = useState([]);
  const { selectedBranch } = useBranch();

  const getActivitie = async () => {
<<<<<<< HEAD

    const tokenFromStorage = localStorage.getItem("tokenClient");
=======
    const token = localStorage.getItem("tokenClient");
>>>>>>> d182f36b144dc13a8a11a2a31cba6fa4171f1e00
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
<<<<<<< HEAD
    }
  };

  // Função para buscar todos os documentos da filial
  const getAllDocuments = async () => {
    setLoadingAllDocuments(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/document/branch/filtered-branch`, {
        params: {
          idSearch: selectedBranch?.idBranch,
          size: 1000,
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        },
      });
      console.log(res.data.content);
      setActivitiesAll(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar todos documentos da filial:", err);
    } finally {
      setLoadingAllDocuments(false);
=======
>>>>>>> d182f36b144dc13a8a11a2a31cba6fa4171f1e00
    }
  };

  const getDocumentByActivitie = async (id: string) => {
    console.log("id selecionado", id);

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
<<<<<<< HEAD
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected?.idActivity}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
=======
        `${ip}/contract/activity/find-document-by-activity/${id}`,
>>>>>>> d182f36b144dc13a8a11a2a31cba6fa4171f1e00
      );
      console.log(res.data.content);
      console.log(res.data);
      setDocumentsByActivitie(res.data);
    } catch (err) {
      console.log("Erro ao buscar documentos da atividade:", err);
    }
  };

  // const removeDocumentByActivitie = async(idActivity: string) => {
  //   const token = localStorage.getItem("tokenClient")
  //   try{
  //     await axios.post(`${ip}/contract/activity/remove-document-from-activity/${idActivity}`, {
  //       headers: {
  //         Authorization: `Bearer ${token}`
  //       }
  //     })
  //   }catch(err: any){
  //     console.log("Erro ao remover documento:", err);
      
  //   }
  // }

  //   const addDocumentByActivitie = async(idActivity: string) => {
  //   const token = localStorage.getItem("tokenClient")
  //   try{
  //     await axios.post(`${ip}/contract/activity/remove-document-from-activity/${idActivity}`)
  //   }catch(err: any){
  //     console.log("Erro ao remover documento:", err);
      
  //   }
  // }

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
                <input
                  type="checkbox"
                  checked={document.selected === true}
                  onChange={() => document.idDocument} // o certo é aqui
                />
                <span>{document.documentTitle || "Documento"}</span>
              </div>
            ))}
          </ScrollArea>
        </div>
      </div>
    </div>
  );
}
