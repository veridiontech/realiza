import { useDocument } from "@/context/Document-provider";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { propsDocument } from "@/types/interfaces";

export function ActivitiesBox() {
  const [checkedDocs, setCheckedDocs] = useState<string[]>([]); // Array para armazenar os documentos selecionados
  const { activitieSelected, setDocuments } = useDocument();
  // const [documentsByActivitie, setDocumentByActivitie] = useState([]);
  const [activities, setActivities] = useState<any>([]);
  const { selectedBranch } = useBranch();
  const [activitiesAll, setActivitiesAll] = useState([]);
  // const [loadingActivitie, setLoadingActivitie] = useState(false);
  const [loadingAllDocuments, setLoadingAllDocuments] = useState(false);
  // const [loadingDocumentsByActivity, setLoadingDocumentsByActivity] =
  //   useState(false);

  // Função para buscar todas as atividades
  const getActivitie = async () => {

    const token = localStorage.getItem("tokenClient");
    try {
      const resSelected = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      setActivities(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar atividades:", err);
    } 
  };

  // Função para buscar todos os documentos da filial
  const getAllDocuments = async () => {
    setLoadingAllDocuments(true);
    try {
      const res = await axios.get(`${ip}/document/branch/filtered-branch`, {
        params: {
          idSearch: selectedBranch?.idBranch,
          size: 1000,
        },
      });
      console.log(res.data.content);
      setActivitiesAll(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar todos documentos da filial:", err);
    } finally {
      setLoadingAllDocuments(false);
    }
  };

  // Função para buscar documentos relacionados à atividade selecionada
  const getDocumentByActivitie = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected?.idActivity}`,
      );
      console.log("Documentos da atividade:", res.data);

      // Resposta já é um array de IDs
      const selectedDocumentsIds = res.data; // Aqui, res.data é diretamente um array de IDs de documentos

      // Atualizar o estado de checkedDocs com os IDs dos documentos da atividade
      setCheckedDocs((prevCheckedDocs) => [
        ...new Set([...prevCheckedDocs, ...selectedDocumentsIds]), // Garantir que não haja duplicação
      ]);
    } catch (err) {
      console.log("Erro ao buscar documentos da atividade:", err);
    } 
  };

  // Atualizar os documentos ao selecionar uma atividade
  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getActivitie();
      getAllDocuments();
    }
  }, [selectedBranch?.idBranch]);

  // Atualizar os documentos ao selecionar uma atividade
  useEffect(() => {
    if (activitieSelected?.idActivity) {
      getDocumentByActivitie();
    }
  }, [activitieSelected?.idActivity]);

  // Função de alternância de seleção de documentos
  const toggleCheckbox = (id: string, document: propsDocument) => {
    setCheckedDocs((prev) => {
      // Se o documento já estiver selecionado, removemos da seleção
      if (prev.includes(id)) {
        return prev.filter((docId) => docId !== id);
      } else {
        // Caso contrário, adicionamos à seleção
        return [...prev, id];
      }
    });

    // Atualizando o estado global de documentos selecionados
    setDocuments((prevDocuments) => {
      if (prevDocuments.some((doc) => doc.idDocumentation === id)) {
        return prevDocuments.filter((doc) => doc.idDocumentation !== id);
      } else {
        return [...prevDocuments, document];
      }
    });
  };

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxActivities activities={activities} />
      </div>
      <div>
        <div className="w-[35vw] border p-5 shadow-md">
          <div className="flex items-center gap-2 rounded-md border p-2">
            <Search />
            <input className="outline-none" />
          </div>
          <ScrollArea className="h-[30vh]">
            {loadingAllDocuments ? (
              <p>Carregando documentos...</p>
            ) : Array.isArray(activitiesAll) && activitiesAll.length > 0 ? (
              activitiesAll.map((document: any) => (
                <div
                  key={document.idDocument}
                  className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
                  onClick={() => toggleCheckbox(document.idDocument, document)}
                >
                  <input
                    type="checkbox"
                    checked={checkedDocs.includes(document.idDocument)} // Marcar checkbox se idDocument estiver em checkedDocs
                    onChange={() => toggleCheckbox(document.idDocument, document)} // Adicionar onChange para atualizar o estado
                  />
                  <span>{document.title || "Documento"}</span>
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
