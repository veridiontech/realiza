import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { BoxActivities } from "../new-documents-page/box-activitie";
import { Search } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";
import { Blocks } from "react-loader-spinner";

// Tipagem para os objetos para maior clareza e segurança
interface Document {
  idDocument: string;
  documentTitle: string;
  selected: boolean; // Indica se já está associado na API
}

interface Activity {
  idActivity: string;
  title: string; // Corrigido para corresponder à prop esperada por BoxActivities
  risk: string;  // Adicionado conforme o erro de tipagem
}

export function ActivitiesBox() {
  const [activitieSelected, setActivitieSelected] = useState<Activity | null>(null);
  const [activities, setActivities] = useState<Activity[]>([]);
  const [documentsByActivitie, setDocumentsByActivitie] = useState<Document[]>([]);
  
  // Novo estado para controlar os documentos selecionados na UI antes de salvar
  const [pendingSelectedDocs, setPendingSelectedDocs] = useState<Set<string>>(new Set());
  
  const { selectedBranch, branch } = useBranch();
  
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  
  const [selectedBranches, setSelectedBranches] = useState<string[]>([]);
  const [replicate, setReplicate] = useState(false);

  // Busca as atividades da filial selecionada
  const getActivities = async () => {
    if (!selectedBranch?.idBranch) return;
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true);
    try {
      const response = await axios.get(
        `${ip}/contract/activity/find-by-branch/${selectedBranch.idBranch}`,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
      setActivities(response.data);
    } catch (err) {
      console.error("Erro ao buscar atividades:", err);
      toast.error("Falha ao carregar as atividades.");
    } finally {
      setIsLoading(false);
    }
  };

  // Busca os documentos associados a uma atividade
  const getDocumentsByActivity = async () => {
    if (!activitieSelected?.idActivity) return;
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true);
    setDocumentsByActivitie([]); // Limpa a lista antes de carregar
    try {
      const response = await axios.get(
        `${ip}/contract/activity/find-document-by-activity/${activitieSelected.idActivity}`,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
      const docs = response.data;
      setDocumentsByActivitie(docs);
      
      // Inicializa os documentos pendentes com os que já vêm selecionados da API
      const alreadySelected = docs
        .filter((doc: Document) => doc.selected)
        .map((doc: Document) => doc.idDocument);
      setPendingSelectedDocs(new Set(alreadySelected));

    } catch (err) {
      console.error("Erro ao buscar documentos da atividade:", err);
      toast.error("Falha ao carregar os documentos da atividade.");
    } finally {
      setIsLoading(false);
    }
  };

  // NOVA FUNÇÃO: Adiciona múltiplos documentos de uma vez
  const addMultipleDocumentsToActivity = async (
    idActivity: string,
    documentBranchIds: string[],
    branchIds: string[],
    replicate: boolean
  ) => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    const requestBody = {
      documentBranchIds,
      replicate,
      branchIds,
    };
    try {
      await axios.post(
        `${ip}/contract/activity/add-multiple-documents-to-activity/${idActivity}`,
        requestBody,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
    } catch (err) {
      console.error("Erro ao adicionar múltiplos documentos:", err);
      throw err; // Propaga o erro para ser tratado no 'handleConfirm'
    }
  };

  // Controla a seleção dos checkboxes, atualizando o estado pendente
  const handleDocumentSelectionChange = (docId: string) => {
    setPendingSelectedDocs(prev => {
      const newSelection = new Set(prev);
      if (newSelection.has(docId)) {
        newSelection.delete(docId);
      } else {
        newSelection.add(docId);
      }
      return newSelection;
    });
  };

  // Abre o modal de confirmação
  const handleOpenConfirmModal = () => {
    if (pendingSelectedDocs.size === 0) {
      toast.info("Nenhum documento selecionado para salvar.");
      return;
    }
    setShowConfirmModal(true);
  };

  // Lógica de confirmação e envio para a API
  const handleConfirmAndSubmit = async () => {
    if (!activitieSelected) return;

    setIsSubmitting(true);
    try {
      const documentIds = Array.from(pendingSelectedDocs);
      await addMultipleDocumentsToActivity(
        activitieSelected.idActivity,
        documentIds,
        selectedBranches,
        replicate
      );
      toast.success("Documentos associados com sucesso!");
      setShowConfirmModal(false);
      await getDocumentsByActivity(); // Recarrega a lista para refletir o estado atual
    } catch (err) {
      toast.error("Ocorreu um erro ao salvar as alterações.");
    } finally {
      setIsSubmitting(false);
      // Reseta estados do modal
      setReplicate(false);
      setSelectedBranches([]);
    }
  };
  
  // Efeitos para carregar dados
  useEffect(() => {
    getActivities();
    setActivitieSelected(null);
    setDocumentsByActivitie([]);
    setPendingSelectedDocs(new Set());
  }, [selectedBranch]);

  useEffect(() => {
    if (activitieSelected) {
      getDocumentsByActivity();
    } else {
      setDocumentsByActivitie([]);
      setPendingSelectedDocs(new Set());
    }
  }, [activitieSelected]);

  // Filtra os documentos selecionados para exibir no modal
  const docsForModal = documentsByActivitie.filter(doc => pendingSelectedDocs.has(doc.idDocument));

  return (
    <div className="flex items-center justify-center gap-10 p-10">
      <BoxActivities
        activities={activities}
        isLoading={isLoading && activities.length === 0}
        onSelectActivitie={(activity: Activity) => setActivitieSelected(activity)}
      />

      <div className="w-[35vw] border p-5 shadow-md flex flex-col gap-4">
        <div className="flex items-center gap-2 rounded-md border p-2">
          <Search className="text-gray-400" />
          <input placeholder="Buscar documento..." className="outline-none w-full bg-transparent" />
        </div>
        <ScrollArea className="h-[30vh]">
          {isLoading ? (
            <div className="flex items-center justify-center h-full">
              <Blocks height="80" width="80" color="#4fa94d" visible={true} />
            </div>
          ) : (
            <div>
              {documentsByActivitie.length > 0 ? (
                documentsByActivitie.map((doc) => (
                  <label
                    key={doc.idDocument}
                    className="flex cursor-pointer items-center gap-3 rounded-sm p-2 hover:bg-gray-100"
                  >
                    <input
                      type="checkbox"
                      checked={pendingSelectedDocs.has(doc.idDocument)}
                      onChange={() => handleDocumentSelectionChange(doc.idDocument)}
                      className="cursor-pointer h-4 w-4"
                    />
                    <span>{doc.documentTitle || "Documento sem título"}</span>
                  </label>
                ))
              ) : (
                <div className="flex items-center justify-center h-full text-gray-500">
                  {activitieSelected ? "Nenhum documento encontrado." : "Selecione uma atividade."}
                </div>
              )}
            </div>
          )}
        </ScrollArea>
        <button
          onClick={handleOpenConfirmModal}
          disabled={!activitieSelected || isLoading}
          className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          Salvar Alterações
        </button>
      </div>

      {showConfirmModal && (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl max-w-lg w-full max-h-[90vh] flex flex-col gap-4">
            <h3 className="text-xl font-semibold">Confirmar Associação</h3>
            
            <div className="border rounded-md p-3 bg-gray-50">
                <p className="mb-2 text-sm font-medium">Os seguintes documentos serão associados à atividade "{activitieSelected?.title}":</p>
                <ScrollArea className="max-h-32">
                    <ul className="list-disc list-inside text-sm space-y-1">
                        {docsForModal.map(doc => (
                            <li key={doc.idDocument}>{doc.documentTitle}</li>
                        ))}
                    </ul>
                </ScrollArea>
            </div>
            
            <div className="flex items-center gap-2 justify-center border-t border-b py-4">
              <input
                type="checkbox" id="replicate-check"
                checked={replicate} onChange={() => setReplicate(prev => !prev)}
                className="h-4 w-4"
              />
              <label htmlFor="replicate-check">Replicar esta alteração para outras filiais?</label>
            </div>

            {replicate && (
              <div className="flex-grow overflow-y-auto">
                <ScrollArea className="h-full pr-2">
                    <div className="flex items-center gap-2 mb-2">
                        <input type="checkbox" id="select-all" 
                            checked={selectedBranches.length === branch.length}
                            onChange={() => setSelectedBranches(prev => prev.length === branch.length ? [] : branch.map((b: any) => b.idBranch))}
                        />
                        <label htmlFor="select-all">Selecionar Todas</label>
                    </div>
                    {branch.map((b: any) => (
                        <div key={b.idBranch} className="flex items-center gap-2">
                            <input type="checkbox" id={b.idBranch} value={b.idBranch}
                                checked={selectedBranches.includes(b.idBranch)}
                                onChange={(e) => {
                                    const { value, checked } = e.target;
                                    setSelectedBranches(prev => checked ? [...prev, value] : prev.filter(id => id !== value));
                                }}
                            />
                            <label htmlFor={b.idBranch}>{b.name}</label>
                        </div>
                    ))}
                </ScrollArea>
              </div>
            )}

            <div className="flex justify-end gap-4 mt-auto">
              <button onClick={() => setShowConfirmModal(false)} disabled={isSubmitting} className="bg-gray-200 hover:bg-gray-300 font-bold py-2 px-4 rounded">
                Cancelar
              </button>
              <button onClick={handleConfirmAndSubmit} disabled={isSubmitting} className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded w-32 flex justify-center">
                {isSubmitting ? <Blocks height="24" width="24" color="#FFF" visible={true} /> : 'Confirmar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

