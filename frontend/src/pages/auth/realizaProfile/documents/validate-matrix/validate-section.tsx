import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";

interface ExpirationItem {
  idDocument: string;
  title: string;
  expirationDateAmount: number;
  expirationDateUnit: "DAYS" | "WEEKS" | "MONTHS";
  doesBlock: boolean;
}

interface ValidateSectionProps {
  idBranch: string;
  documentTypeName: string;
  isSelected: boolean;
  refreshTrigger?: number;
}

export function ValidateSection({
  idBranch,
  documentTypeName,
  isSelected,
  refreshTrigger,
}: ValidateSectionProps) {
  const [expirationList, setExpirationList] = useState<ExpirationItem[]>([]);
  const [originalExpirationList, setOriginalExpirationList] = useState<
    ExpirationItem[]
  >([]);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const { branch } = useBranch();
  const [showReplicateConfirmation, setShowReplicateConfirmation] =
    useState(false);
  const [selectedBranches, setSelectedBranches] = useState<string[]>([]);
  const [replicate, setReplicate] = useState(false);

  const fetchExpirations = async () => {
    if (!idBranch || !documentTypeName) {
      return;
    }

    const token = localStorage.getItem("tokenClient");
    if (!token) {
      return;
    }

    const url = `${ip}/document/branch/document-matrix/expiration/${idBranch}`;
    const params = {
      documentTypeName,
      isSelected: true,
      replicate: false,
      _ts: Date.now(),
    };

    try {
      const { data } = await axios.get<ExpirationItem[]>(url, {
        headers: { Authorization: `Bearer ${token}` },
        params: params,
      });

      const normalized = (data ?? []).map((d) => ({
        ...d,
        expirationDateAmount: Number(d.expirationDateAmount ?? 0),
        expirationDateUnit: (d.expirationDateUnit as any) ?? "MONTHS",
        doesBlock: !!(d as any).doesBlock,
      }));

      setExpirationList(normalized);
      setOriginalExpirationList(normalized);
    } catch (err) {
      console.error(
        "fetchExpirations: Erro ao buscar validade dos documentos:",
        err
      );
    }
  };

  useEffect(() => {
    fetchExpirations();
  }, [idBranch, documentTypeName, isSelected, refreshTrigger]);

  const handleInputChange = (
    id: string,
    field: keyof ExpirationItem,
    value: string | number | boolean
  ) => {
    const updatedList = expirationList.map((item) => {
      if (item.idDocument === id) {
        return { ...item, [field]: value };
      }
      return item;
    });
    setExpirationList(updatedList);
  };

  const changedItems = useMemo(() => {
    if (originalExpirationList.length === 0) return [];
    const originalMap = new Map(
      originalExpirationList.map((item) => [item.idDocument, item])
    );
    return expirationList.filter((currentItem) => {
      const originalItem = originalMap.get(currentItem.idDocument);
      if (!originalItem) return false;
      return (
        currentItem.expirationDateAmount !== originalItem.expirationDateAmount ||
        currentItem.doesBlock !== originalItem.doesBlock ||
        currentItem.expirationDateUnit !== originalItem.expirationDateUnit
      );
    });
  }, [expirationList, originalExpirationList]);

  const hasChanges = changedItems.length > 0;

  const handleOpenModal = () => {
    if (!hasChanges) {
      toast.info("Nenhuma alteração foi feita.");
      return;
    }
    setShowReplicateConfirmation(true);
  };

  const handleConfirmReplication = async (confirmedReplicate: boolean) => {
    setIsSaving(true);
    setShowReplicateConfirmation(false);

    const token = localStorage.getItem("tokenClient");
    if (!token) {
      setIsSaving(false);
      return;
    }

    const savePromises = changedItems.map((itemToSave) => {
      const payload = {
        expirationDateAmount: itemToSave.expirationDateAmount,
        expirationDateUnit: itemToSave.expirationDateUnit,
        doesBlock: itemToSave.doesBlock,
        replicate: confirmedReplicate,
        branches: confirmedReplicate ? selectedBranches : [],
      };

      const url = `${ip}/document/branch/document-matrix/expiration/update/${itemToSave.idDocument}`;
      
      return axios.post(url, payload, {
        headers: { Authorization: `Bearer ${token}` },
      }).catch(err => ({ error: err, docId: itemToSave.idDocument }));
    });

    try {
      const results = await Promise.all(savePromises);
      const errors = results.filter(res => res && 'error' in res);

      if (errors.length > 0) {
        toast.error(`Houveram ${errors.length} erros ao salvar.`);
        console.error("Erros ao salvar em lote:", errors);
      } else {
        toast.success("Alterações salvas com sucesso!");
      }
      
      await fetchExpirations();
      setIsEditing(false);

    } catch (err: any) {
      console.error("handleSaveAll: Erro ao salvar validade:", err);
      toast.error("Erro ao salvar alterações.");
    } finally {
      setIsSaving(false);
      setReplicate(false);
      setSelectedBranches([]);
    }
  };

  const toggleSelectAll = () => {
    if (selectedBranches.length === (branch?.length ?? 0)) {
      setSelectedBranches([]);
    } else {
      setSelectedBranches(branch?.map((b: any) => b.idBranch) ?? []);
    }
  };

  if (expirationList.length === 0) return null;

  return (
    <div>
      <div className="flex justify-end mb-2">
        <button
          onClick={isEditing ? handleOpenModal : () => setIsEditing(true)}
          className={`font-semibold text-sm ${
            isEditing ? "text-green-600" : "text-blue-600"
          }`}
          disabled={isSaving || (isEditing && !hasChanges)}
        >
          {isSaving ? "Salvando..." : isEditing ? "Salvar Alterações" : "Editar"}
        </button>
      </div>
      <table className="w-full text-sm border border-gray-300">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-2 py-1 text-left">Título</th>
            <th className="px-2 py-1 text-left">Validade</th>
            <th className="px-2 py-1 text-left">Unidade</th>
            <th className="px-2 py-1 text-left">Bloqueia</th>
          </tr>
        </thead>
        <tbody>
          {expirationList.map((doc) => (
            <tr key={doc.idDocument} className="border-t">
              <td className="px-2 py-1 font-medium">{doc.title}</td>
              {isEditing ? (
                <>
                  <td className="px-2 py-1">
                    <input
                      type="number"
                      min={0}
                      value={doc.expirationDateAmount}
                      onChange={(e) => {
                        handleInputChange(
                          doc.idDocument,
                          "expirationDateAmount",
                          e.target.value === "" ? 0 : parseInt(e.target.value, 10)
                        );
                      }}
                      className="w-20 border px-1 py-0.5"
                      disabled={isSaving}
                    />
                  </td>
                  <td className="px-2 py-1">
                    <select
                      value={doc.expirationDateUnit}
                      onChange={(e) => {
                        handleInputChange(
                          doc.idDocument,
                          "expirationDateUnit",
                          e.target.value
                        );
                      }}
                      className="border px-1 py-0.5"
                      disabled={isSaving}
                    >
                      <option value="DAYS">Dias</option>
                      <option value="WEEKS">Semanas</option>
                      <option value="MONTHS">Meses</option>
                    </select>
                  </td>
                  <td className="px-2 py-1">
                    <input
                      type="checkbox"
                      checked={doc.doesBlock}
                      onChange={(e) => {
                        handleInputChange(
                          doc.idDocument,
                          "doesBlock",
                          e.target.checked
                        );
                      }}
                      disabled={isSaving}
                    />
                  </td>
                </>
              ) : (
                <>
                  <td className="px-2 py-1 text-center">
                    {doc.expirationDateAmount}
                  </td>
                  <td className="px-2 py-1 text-center">
                    {doc.expirationDateUnit === "DAYS" ? "Dias" :
                     doc.expirationDateUnit === "WEEKS" ? "Semanas" : "Meses"}
                  </td>
                  <td className="px-2 py-1">
                    <input
                      type="checkbox"
                      checked={doc.doesBlock}
                      readOnly
                      disabled
                    />
                  </td>
                </>
              )}
            </tr>
          ))}
        </tbody>
      </table>

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
                        checked={selectedBranches.length === (branch?.length ?? 0)}
                        onChange={toggleSelectAll}
                        className="h-4 w-4"
                      />
                      <label>Selecionar todas as filiais</label>
                    </div>

                    {branch?.map((b: any) => (
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