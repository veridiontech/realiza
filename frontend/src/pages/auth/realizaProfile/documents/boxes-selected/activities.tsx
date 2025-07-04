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
  const [activitieSelected, setActivitieSelected] = useState<any>(null);
  const [activities, setActivities] = useState<any[]>([]);
  const [documentsByActivitie, setDocumentsByActivitie] = useState([]);
  const { selectedBranch } = useBranch();
  const [loadingDocumentId, setLoadingDocumentId] = useState<string | null>(
    null
  );
  const [isLoading, setIsLoading] = useState(false);
  const [showReplicateConfirmation, setShowReplicateConfirmation] = useState(false);
  const [pendingOperation, setPendingOperation] = useState<any>(null);

  const getActivitie = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true);
    try {
      const resSelected = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setActivities(resSelected.data);
    } catch (err) {
      console.log("erro ao buscar atividades:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const getDocumentByActivitie = async (id: string) => {
    console.log("id selecionado", id);
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected?.idActivity}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      console.log(res.data.content);
      console.log(res.data);
      setDocumentsByActivitie(res.data);
    } catch (err) {
      console.log("Erro ao buscar documentos da atividade:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const removeDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const res = await axios.post(
        `${ip}/contract/activity/remove-document-from-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
      console.log(res.data);
    } catch (err: any) {
      console.log("Erro ao remover documento:", err);
      throw err;
    }
  };

  const addDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const res = await axios.post(
        `${ip}/contract/activity/add-document-to-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
      console.log(res.data);
    } catch (err: any) {
      console.log("Erro ao adicionar documento:", err);
      throw err;
    }
  };

  const handleSelectDocument = async (_document: any, idDocument: string) => {
    const idActivity = activitieSelected?.idActivity;
    if (!idActivity) return;

    setLoadingDocumentId(idDocument);

    setPendingOperation({
      type: _document.selected === true ? "remove" : "add",
      idActivity: idActivity,
      idDocumentBranch: idDocument,
      document: _document,
    });
    
    setShowReplicateConfirmation(true);
  };

  const handleConfirmReplication = async (replicate: boolean) => {
    setShowReplicateConfirmation(false);
    if (!pendingOperation) return;

    const { type, idActivity, idDocumentBranch } = pendingOperation;

    try {
      if (type === "remove") {
        await removeDocumentByActivitie(idActivity, idDocumentBranch, replicate);
        setDocumentsByActivitie((prevDocs: any) =>
          prevDocs.map((doc: any) =>
            doc.idDocument === idDocumentBranch ? { ...doc, selected: false } : doc
          )
        );
      } else {
        await addDocumentByActivitie(idActivity, idDocumentBranch, replicate);
        setDocumentsByActivitie((prevDocs: any) =>
          prevDocs.map((doc: any) =>
            doc.idDocument === idDocumentBranch ? { ...doc, selected: true } : doc
          )
        );
      }
      toast.success("Operação realizada com sucesso!");
    } catch (err) {
      console.error("Erro na operação:", err);
      toast.error("Erro ao realizar a operação.");
    } finally {
      setLoadingDocumentId(null);
      setPendingOperation(null);
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
          isLoading={isLoading}
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
            {isLoading ? (
              <div className="flex items-center justify-center">
                <Blocks
                  height="80"
                  width="80"
                  color="#4fa94d"
                  ariaLabel="blocks-loading"
                  wrapperStyle={{}}
                  wrapperClass="blocks-wrapper"
                  visible={true}
                />
              </div>
            ) : (
              <div>
                {" "}
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
              </div>
            )}
          </ScrollArea>
        </div>
      </div>

      {showReplicateConfirmation && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg text-center">
            <h3 className="text-lg font-semibold mb-4">Replicar Alteração?</h3>
            <p className="mb-6">Deseja replicar esta alteração para todas as outras filiais?</p>
            <div className="flex justify-center gap-4">
              <button
                onClick={() => handleConfirmReplication(true)}
                className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
              >
                Sim
              </button>
              <button
                onClick={() => handleConfirmReplication(false)}
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
              >
                Não
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}