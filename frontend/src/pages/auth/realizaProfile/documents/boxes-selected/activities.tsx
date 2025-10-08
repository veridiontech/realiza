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
  // Mantido para compatibilidade e controle interno dos selecionados
  const [, setDocumentsByActivitie] = useState<DocumentData[]>([]);
  // NOVO ESTADO: Armazena todos os documentos da filial (selecionados ou não pela atividade)
  const [allDocumentsByBranch, setAllDocumentsByBranch] = useState<DocumentData[]>([]); 
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
      console.log(`LOG - Buscando atividades para a filial: ${selectedBranch?.idBranch}`);
      const resSelected = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setActivities(resSelected.data);
      console.log("LOG - Atividades buscadas com sucesso.");
    } catch (err) {
      console.error("LOG - Erro ao buscar atividades:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const getAllDocuments = async () => {
    if (!selectedBranch?.idBranch) {
      setAllDocumentsByBranch([]);
      return;
    }

    setIsLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const branchId = selectedBranch.idBranch;
    const activityId = activitieSelected?.idActivity;

    // 1. Busca os documentos *selecionados* pela atividade (para manter a flag 'selected' precisa)
    let selectedDocs: DocumentData[] = [];
    if (activityId) {
      try {
        console.log(
          `LOG - Buscando documentos SELECIONADOS para a atividade: ${activityId}`
        );
        const resSelected = await axios.get<DocumentData[]>(
          `${ip}/document/branch/document-matrix/all-and-selected-by-activity/${activityId}`,
          {
            headers: { Authorization: `Bearer ${tokenFromStorage}` },
          }
        );
        // Apenas documentos que vieram como 'selected: true'
        selectedDocs = resSelected.data.filter(doc => doc.selected === true);
        setDocumentsByActivitie(selectedDocs); 
      } catch (err) {
        console.error("LOG - Erro ao buscar documentos selecionados da atividade:", err);
      }
    } else {
        setDocumentsByActivitie([]);
    }

    // 2. Busca *todos* os documentos da filial (endpoint da Imagem 1)
    try {
      const params = {
        // documentTypeName: undefined, // Omitido conforme solicitado
        isSelected: true // Parâmetro isSelected=true conforme solicitado
      };
      const url = `${ip}/document/branch/document-matrix/${branchId}`;

      console.log(
        `LOG - Buscando TODOS os documentos da filial: ${branchId}. URL: ${url}, Parâmetros:`, 
        params
      );

      const resAll = await axios.get<DocumentData[]>(
        url, 
        {
          params: params,
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      
      let allDocs: DocumentData[] = resAll.data || [];
      
      // Cria um Set de IDs dos documentos selecionados pela atividade para comparação rápida
      const selectedIds = new Set(selectedDocs.map(d => d.idDocument));
      
      // Mescla as duas listas: usa a lista de todos e sobrescreve a flag 'selected' 
      // se o documento estiver na lista de documentos selecionados pela atividade.
      const mergedDocs = allDocs.map(doc => ({
        ...doc,
        // Garante que o documento tenha 'selected: true' se estiver alocado à atividade
        selected: selectedIds.has(doc.idDocument) || doc.selected === true
      }));
      
      setAllDocumentsByBranch(mergedDocs);
      setSearchTerm("");
      console.log(`LOG - ${mergedDocs.length} documentos da filial mesclados e prontos para exibição.`);
      
    } catch (err) {
      console.error("LOG - Erro ao buscar todos os documentos da filial:", err);
      setAllDocumentsByBranch([]);
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
      console.log(`LOG - Removendo documento ${idDocumentBranch} da atividade ${idActivity}. Replicar: ${replicate}. Filiais: ${branches.join(", ")}`);
      await axios.post(
        `${ip}/contract/activity/remove-document-from-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        branches,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
      console.log("LOG - Remoção concluída com sucesso.");
    } catch (err: any) {
      console.error("LOG - Erro ao remover documento:", err);
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
      console.log(`LOG - Adicionando documento ${idDocumentBranch} na atividade ${idActivity}. Replicar: ${replicate}. Filiais: ${branches.join(", ")}`);
      await axios.post(
        `${ip}/contract/activity/add-document-to-activity/${idActivity}?idDocumentBranch=${idDocumentBranch}&replicate=${replicate}`,
        branches,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );
      console.log("LOG - Adição concluída com sucesso.");
    } catch (err: any) {
      console.error("LOG - Erro ao adicionar documento:", err);
      throw err;
    }
  };

  const handleSelectDocument = async (document: DocumentData, idDocument: string) => {
    const idActivity = activitieSelected?.idActivity;
    if (!idActivity) return;

    setLoadingDocumentId(idDocument);

    const operationType = document.selected === true ? "remove" : "add";

    setPendingOperation({
      type: operationType,
      idActivity: idActivity,
      idDocumentBranch: idDocument,
      document: document,
    });

    setShowReplicateConfirmation(true);
    console.log(`LOG - Tentativa de ${operationType} documento: ${idDocument}. Aguardando confirmação de replicação.`);
  };

  const handleConfirmReplication = async (confirmed: boolean) => {
    setShowReplicateConfirmation(false);
    if (!pendingOperation) return;

    const { type, idActivity, idDocumentBranch, document } = pendingOperation;

    console.log(`LOG - Confirmação: ${type} para o documento: ${idDocumentBranch}. Replicar: ${confirmed}.`);

    try {
      if (type === "remove") {
        await removeDocumentByActivitie(
          idActivity,
          idDocumentBranch,
          selectedBranches,
          confirmed
        );
        // Atualiza o estado visual removendo a seleção na lista COMPLETA
        setAllDocumentsByBranch((prevDocs) =>
          prevDocs.map((doc) =>
            doc.idDocument === idDocumentBranch ? { ...doc, selected: false } : doc
          )
        );
        // Atualiza o estado de documentos selecionados
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
        // Atualiza o estado visual adicionando a seleção na lista COMPLETA
        setAllDocumentsByBranch((prevDocs) =>
          prevDocs.map((doc) =>
            doc.idDocument === idDocumentBranch ? { ...doc, selected: true } : doc
          )
        );
        // Atualiza o estado de documentos selecionados
        setDocumentsByActivitie((prevDocs) => [
            ...prevDocs,
            { ...document, selected: true } 
        ]);
      }
      toast.success("Operação realizada com sucesso!");
    } catch (err) {
      console.error("LOG - Erro na operação:", err);
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

  // useMemo agora usa a lista completa de documentos
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
    }
  }, [selectedBranch?.idBranch]);

  // Chama a nova função de busca de todos os documentos
  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getAllDocuments();
    } else {
      setAllDocumentsByBranch([]);
    }
  }, [selectedBranch, activitieSelected]); // Recarrega sempre que a filial ou a atividade mudar

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
            {isLoading && !allDocumentsByBranch.length ? (
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
                      : activitieSelected 
                        ? "Nenhum documento alocado ou disponível para esta atividade/filial."
                        : "Selecione uma atividade para ver os documentos."}
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