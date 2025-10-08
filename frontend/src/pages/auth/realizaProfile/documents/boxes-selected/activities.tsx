import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";
import { Blocks } from "react-loader-spinner";

interface DocumentData {
  idDocument: string;
  title: string;
  selected: boolean;
}

export function ActivitiesBox() {
  const [activitieSelected, setActivitieSelected] = useState<any>(null);
  const [activities, setActivities] = useState<any[]>([]);
  const [documentsByActivitie, setDocumentsByActivitie] = useState<DocumentData[]>([]);
  const { selectedBranch, branch } = useBranch();
  const [loadingDocumentId, setLoadingDocumentId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [showReplicateConfirmation, setShowReplicateConfirmation] = useState(false);
  const [pendingOperation, setPendingOperation] = useState<any>(null);
  const [selectedBranches, setSelectedBranches] = useState<string[]>([]);
  const [replicate, setReplicate] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

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

  const getDocumentByActivitie = async () => {
    if (!activitieSelected?.idActivity) {
      return;
    }

    setIsLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const activityId = activitieSelected.idActivity;

    try {
      const res = await axios.get<DocumentData[]>(
        `${ip}/document/branch/document-matrix/all-and-selected-by-activity/${activityId}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      
      const selectedDocuments = res.data.filter(doc => doc.selected === true);
      
      setDocumentsByActivitie(selectedDocuments);
      setSearchTerm("");
    } catch (err) {
      console.error("Erro ao buscar documentos da atividade:", err);
      setDocumentsByActivitie([]);
    } finally {
      setIsLoading(false);
    }
  };

  const removeDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
    branches: string[],
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.post(
        `${ip}/contract/activity/remove-document-from-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        branches,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
    } catch (err: any) {
      console.log("Erro ao remover documento:", err);
      throw err;
    }
  };

  const addDocumentByActivitie = async (
    idActivity: string,
    idDocumentBranch: string,
    branches: string[],
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.post(
        `${ip}/contract/activity/add-document-to-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        branches,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
    } catch (err: any) {
      console.log("Erro ao adicionar documento:", err);
      throw err;
    }
  };

  const handleSelectDocument = async (document: DocumentData, idDocument: string) => {
    const idActivity = activitieSelected?.idActivity;
    if (!idActivity) return;

    setLoadingDocumentId(idDocument);

    setPendingOperation({
      type: document.selected === true ? "remove" : "add",
      idActivity: idActivity,
      idDocumentBranch: idDocument,
      document: document,
    });

    setShowReplicateConfirmation(true);
  };

  const handleConfirmReplication = async (confirmed: boolean) => {
    setShowReplicateConfirmation(false);
    if (!pendingOperation) return;

    const { type, idActivity, idDocumentBranch } = pendingOperation;

    try {
      if (type === "remove") {
        await removeDocumentByActivitie(
          idActivity,
          idDocumentBranch,
          selectedBranches,
          confirmed
        );
        setDocumentsByActivitie((prevDocs) =>
          prevDocs.filter((doc) => doc.idDocument !== idDocumentBranch)
        );
      } else {
        await addDocumentByActivitie(
          idActivity,
          idDocumentBranch,
          selectedBranches,
          confirmed
        );
      }
      toast.success("Operação realizada com sucesso!");
    } catch (err) {
      console.error("Erro na operação:", err);
      toast.error("Erro ao realizar a operação.");
    } finally {
      setLoadingDocumentId(null);
      setPendingOperation(null);
      setReplicate(false);
      setSelectedBranches([]);
    }
  };

  const toggleSelectAll = () => {
    if (selectedBranches.length === branch.length) {
      setSelectedBranches([]);
    } else {
      setSelectedBranches(branch.map((b) => b.idBranch));
    }
  };

  const filteredDocuments = useMemo(() => {
    if (!searchTerm) {
      return documentsByActivitie;
    }
    const lowercasedSearchTerm = searchTerm.toLowerCase();
    return documentsByActivitie.filter((document) =>
      document.title?.toLowerCase().includes(lowercasedSearchTerm)
    );
  }, [documentsByActivitie, searchTerm]);

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getActivitie();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (activitieSelected?.idActivity) {
      getDocumentByActivitie();
    } else {
      setDocumentsByActivitie([]);
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
            <input
              className="outline-none w-full"
              placeholder="Pesquisar documentos..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <ScrollArea className="h-[30vh]">
            {isLoading ? (
              <div className="flex items-center justify-center py-10">
                <Blocks
                  height="80"
                  width="80"
                  color="#4fa94d"
                  visible={true}
                />
              </div>
            ) : (
              <div>
                {filteredDocuments.length > 0 ? (
                  filteredDocuments.map((document) => (
                    <div
                      key={document.idDocument}
                      className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
                    >
                      {loadingDocumentId === document.idDocument ? (
                        <Blocks
                          height="50"
                          width="50"
                          color="#4fa94d"
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
                      <span>{document.title || "Documento sem título"}</span>
                    </div>
                  ))
                ) : (
                  <p className="p-4 text-center text-gray-500">
                    {searchTerm
                      ? "Nenhum documento encontrado com este termo."
                      : "Nenhum documento alocado para esta atividade."}
                  </p>
                )}
              </div>
            )}
          </ScrollArea>
        </div>
      </div>

      {showReplicateConfirmation && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full max-h-[80vh] overflow-hidden flex flex-col">
            <h3 className="text-lg font-semibold mb-4">
              Replicar Alteração?
            </h3>
            <p className="mb-4">
              Deseja replicar esta alteração para outras filiais?
            </p>

            <div className="flex items-center gap-2 justify-center mb-4">
              <input
                type="checkbox"
                checked={replicate}
                onChange={() => setReplicate(!replicate)}
                className="h-4 w-4"
              />
              <label>Habilitar replicação</label>
            </div>

            {replicate && (
              <div className="flex-1 overflow-y-auto">
                <ScrollArea className="h-full">
                  <div className="text-left">
                    <div className="flex items-center gap-2 mb-2">
                      <input
                        type="checkbox"
                        checked={selectedBranches.length === branch.length}
                        onChange={toggleSelectAll}
                        className="h-4 w-4"
                      />
                      <label>Selecionar todas as filiais</label>
                    </div>

                    {branch.map((b: any) => (
                      <div key={b.idBranch} className="flex items-center gap-2">
                        <input
                          type="checkbox"
                          value={b.idBranch}
                          checked={selectedBranches.includes(b.idBranch)}
                          onChange={(e) => {
                            const { value, checked } = e.target;
                            if (checked) {
                              setSelectedBranches([
                                ...selectedBranches,
                                value,
                              ]);
                            } else {
                              setSelectedBranches(
                                selectedBranches.filter((id) => id !== value)
                              );
                            }
                          }}
                          className="h-4 w-4"
                        />
                        <span>{b.name}</span>
                      </div>
                    ))}
                  </div>
                </ScrollArea>
              </div>
            )}

            <div className="flex justify-center gap-4 mt-4">
              <button
                onClick={() => handleConfirmReplication(true)}
                className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
              >
                Confirmar
              </button>
              <button
                onClick={() => setShowReplicateConfirmation(false)}
                className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded"
              >
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}