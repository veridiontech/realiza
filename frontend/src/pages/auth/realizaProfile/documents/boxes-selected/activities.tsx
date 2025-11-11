import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";
import { Blocks, Oval } from "react-loader-spinner";
import { Button } from "@/components/ui/button";

interface DocumentData {
  idDocument: string;
  title: string;
  selected: boolean;
}

export function ActivitiesBox() {
  const [activitieSelected, setActivitieSelected] = useState<any>(null);
  const [activities, setActivities] = useState<any[]>([]);
  const [, setDocumentsByActivitie] = useState<DocumentData[]>([]);
  const [originalDocuments, setOriginalDocuments] = useState<DocumentData[]>([]);
  const [allDocumentsByBranch, setAllDocumentsByBranch] = useState<DocumentData[]>([]);
  const { selectedBranch, branch } = useBranch();
  const [isSaving, setIsSaving] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingDocuments, setIsLoadingDocuments] = useState(false);
  const [showReplicateConfirmation, setShowReplicateConfirmation] = useState(false);
  const [pendingOperation, setPendingOperation] = useState<{
    docsToAdd: DocumentData[];
    docsToRemove: DocumentData[];
  } | null>(null);
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
      console.error("Erro ao buscar atividades:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const getAllDocuments = async () => {
    if (!selectedBranch?.idBranch || !activitieSelected?.idActivity) {
      setAllDocumentsByBranch([]);
      setOriginalDocuments([]);
      return;
    }

    setIsLoadingDocuments(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const branchId = selectedBranch.idBranch;
    const activityId = activitieSelected.idActivity;

    let selectedDocs: DocumentData[] = [];
    try {
      const resSelected = await axios.get<DocumentData[]>(
        `${ip}/document/branch/document-matrix/all-and-selected-by-activity/${activityId}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      selectedDocs = resSelected.data.filter((doc) => doc.selected === true);
      setDocumentsByActivitie(selectedDocs);
    } catch (err) {
      console.error(
        "Erro ao buscar documentos selecionados da atividade:",
        err
      );
    }

    try {
      const params = { isSelected: true };
      const url = `${ip}/document/branch/document-matrix/${branchId}`;

      const resAll = await axios.get<DocumentData[]>(url, {
        params: params,
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });

      let allDocs: DocumentData[] = resAll.data || [];
      const selectedIds = new Set(selectedDocs.map((d) => d.idDocument));

      const mergedDocs = allDocs.map((doc) => ({
        ...doc,
        selected: selectedIds.has(doc.idDocument) || doc.selected === true,
      }));

      setAllDocumentsByBranch(mergedDocs);
      setOriginalDocuments(mergedDocs);
      setSearchTerm("");
    } catch (err) {
      console.error("Erro ao buscar todos os documentos da filial:", err);
      setAllDocumentsByBranch([]);
      setOriginalDocuments([]);
    } finally {
      setIsLoadingDocuments(false);
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
      console.error("Erro ao remover documento:", err);
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
      console.error("Erro ao adicionar documento:", err);
      throw err;
    }
  };

  const handleSelectDocument = (documentId: string) => {
    if (!activitieSelected?.idActivity) return;

    setAllDocumentsByBranch((prevDocs) =>
      prevDocs.map((doc) =>
        doc.idDocument === documentId
          ? { ...doc, selected: !doc.selected }
          : doc
      )
    );
  };

  const changedDocuments = useMemo(() => {
    if (!originalDocuments.length) {
      return { docsToAdd: [], docsToRemove: [], hasChanges: false };
    }

    const originalMap = new Map(
      originalDocuments.map((doc) => [doc.idDocument, doc.selected])
    );
    const docsToAdd: DocumentData[] = [];
    const docsToRemove: DocumentData[] = [];

    allDocumentsByBranch.forEach((currentDoc) => {
      const originalSelected = originalMap.get(currentDoc.idDocument);
      if (originalSelected === undefined) return;

      if (currentDoc.selected && !originalSelected) {
        docsToAdd.push(currentDoc);
      } else if (!currentDoc.selected && originalSelected) {
        docsToRemove.push(currentDoc);
      }
    });

    return {
      docsToAdd,
      docsToRemove,
      hasChanges: docsToAdd.length > 0 || docsToRemove.length > 0,
    };
  }, [allDocumentsByBranch, originalDocuments]);

  const handleOpenConfirmationModal = () => {
    const { docsToAdd, docsToRemove } = changedDocuments;
    if (docsToAdd.length === 0 && docsToRemove.length === 0) {
      toast.info("Nenhuma alteração foi feita.");
      return;
    }

    setPendingOperation({ docsToAdd, docsToRemove });
    setShowReplicateConfirmation(true);
  };

  const handleConfirmReplication = async (confirmed: boolean) => {
    setShowReplicateConfirmation(false);
    if (!pendingOperation || !activitieSelected?.idActivity) return;

    const { docsToAdd, docsToRemove } = pendingOperation;
    const idActivity = activitieSelected.idActivity;
    const branches = selectedBranches;
    const replicate = confirmed;

    setIsSaving(true);

    const addPromises = docsToAdd.map((doc) =>
      addDocumentByActivitie(
        idActivity,
        doc.idDocument,
        branches,
        replicate
      ).catch((err) => ({ error: err, docId: doc.idDocument, type: "add" }))
    );

    const removePromises = docsToRemove.map((doc) =>
      removeDocumentByActivitie(
        idActivity,
        doc.idDocument,
        branches,
        replicate
      ).catch((err) => ({ error: err, docId: doc.idDocument, type: "remove" }))
    );

    try {
      const results = await Promise.all([...addPromises, ...removePromises]);

      const errors = results.filter((res) => res && res.error);

      if (errors.length > 0) {
        toast.error(
          `Houveram ${errors.length} erros ao salvar. Verifique o console.`
        );
        console.error("Erros na operação em lote:", errors);
        getAllDocuments();
      } else {
        toast.success("Alterações salvas com sucesso!");
        setOriginalDocuments(allDocumentsByBranch);
      }
    } catch (err) {
      console.error("Erro catastrófico no Promise.all:", err);
      toast.error("Erro ao processar alterações.");
      getAllDocuments();
    } finally {
      setIsSaving(false);
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
      return allDocumentsByBranch;
    }
    const lowercasedSearchTerm = searchTerm.toLowerCase();
    return allDocumentsByBranch.filter((document) =>
      document.title?.toLowerCase().includes(lowercasedSearchTerm)
    );
  }, [allDocumentsByBranch, searchTerm]);

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getActivitie();
      setActivitieSelected(null);
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (activitieSelected?.idActivity) {
      getAllDocuments();
    } else {
      setAllDocumentsByBranch([]);
      setOriginalDocuments([]);
    }
  }, [activitieSelected]);

  const renderDocumentBoxContent = () => {
    if (!activitieSelected) {
      return (
        <div className="flex items-center justify-center h-[30vh]">
          <p className="p-4 text-center text-gray-500">
            Selecione uma atividade à esquerda para ver os documentos.
          </p>
        </div>
      );
    }

    if (isLoadingDocuments) {
      return (
        <div className="flex items-center justify-center py-10 h-[30vh]">
          <Blocks height="80" width="80" color="#4fa94d" visible={true} />
        </div>
      );
    }

    return (
      <ScrollArea className="h-[30vh]">
        {filteredDocuments.length > 0 ? (
          filteredDocuments.map((document) => (
            <div
              key={document.idDocument}
              className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
            >
              <input
                type="checkbox"
                checked={document.selected === true}
                onChange={() => handleSelectDocument(document.idDocument)}
                className="cursor-pointer"
                disabled={!activitieSelected || isSaving}
              />
              <span className={!activitieSelected ? "text-gray-400" : ""}>
                {document.title || "Documento sem título"}
              </span>
            </div>
          ))
        ) : (
          <p className="p-4 text-center text-gray-500">
            {searchTerm
              ? "Nenhum documento encontrado com este termo."
              : "Nenhum documento alocado ou disponível para esta atividade/filial."}
          </p>
        )}
      </ScrollArea>
    );
  };

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
              disabled={!activitieSelected || isLoadingDocuments}
            />
          </div>

          {renderDocumentBoxContent()}

          {activitieSelected && !isLoadingDocuments && (
            <Button
              className="w-full mt-4 bg-realizaBlue"
              onClick={handleOpenConfirmationModal}
              disabled={
                !activitieSelected || !changedDocuments.hasChanges || isSaving
              }
            >
              {isSaving ? (
                <Oval
                  visible={true}
                  height="20"
                  width="20"
                  color="#fff"
                  ariaLabel="oval-loading"
                />
              ) : (
                "Salvar Alterações"
              )}
            </Button>
          )}
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
                onClick={() => handleConfirmReplication(replicate)}
                className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
              >
                Confirmar
              </button>
              <button
                onClick={() => {
                  setShowReplicateConfirmation(false);
                  setPendingOperation(null);
                  setReplicate(false);
                  setSelectedBranches([]);
                }}
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