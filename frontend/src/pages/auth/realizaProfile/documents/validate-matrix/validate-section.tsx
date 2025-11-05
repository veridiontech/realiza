import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

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
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  // Removendo estados não utilizados ou substituindo por busca na lista ao salvar
  // const [amountEdit, ] = useState(0);
  // const [doesBlockEdit, ] = useState(false);

  const fetchExpirations = async () => {
    console.log("Iniciando fetchExpirations...");
    if (!idBranch || !documentTypeName) {
      console.warn("fetchExpirations: idBranch ou documentTypeName ausentes. Abortando.");
      return;
    }

    const token = localStorage.getItem("tokenClient");
    if (!token) {
      console.error("fetchExpirations: Token não encontrado. Abortando.");
      return;
    }

    const url = `${ip}/document/branch/document-matrix/expiration/${idBranch}`;
    const params = { documentTypeName, isSelected: true, replicate: false, _ts: Date.now() };

    console.log(`fetchExpirations: Chamando GET ${url}`);
    console.log("fetchExpirations: Parâmetros:", params);

    try {
      const { data } = await axios.get<ExpirationItem[]>(url, {
        headers: { Authorization: `Bearer ${token}` },
        params: params,
      });

      console.log("fetchExpirations: Requisição concluída com sucesso.");

      const normalized = (data ?? []).map((d) => ({
        ...d,
        // Garantindo que os campos sejam do tipo esperado
        expirationDateAmount: Number(d.expirationDateAmount ?? 0),
        expirationDateUnit: (d.expirationDateUnit as any) ?? "MONTHS",
        doesBlock: !!(d as any).doesBlock,
      }));

      console.log("fetchExpirations: Dados normalizados e definidos na lista.");
      setExpirationList(normalized);
    } catch (err) {
      console.error("fetchExpirations: Erro ao buscar validade dos documentos:", err);
      if (axios.isAxiosError(err) && err.response) {
        console.error("fetchExpirations: Detalhes do erro da resposta:", err.response.data);
      }
    }
    console.log("fetchExpirations: Processo finalizado.");
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

  const handleSaveAll = async () => {
    setIsSaving(true);
    console.log("Iniciando handleSaveAll...");
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("handleSaveAll: Token não encontrado. Abortando.");
        setIsSaving(false);
        return;
      }

      if (!editingId) {
        console.error("handleSaveAll: Nenhum documento em edição (editingId está null). Abortando.");
        setIsSaving(false);
        return;
      }
      const id = editingId;

      // Encontrando o item na lista com as alterações locais
      const itemToSave = expirationList.find(item => item.idDocument === id);

      if (!itemToSave) {
          console.error(`handleSaveAll: Item com id ${id} não encontrado na lista. Abortando.`);
          setIsSaving(false);
          return;
      }

      const payload = {
        // Usando o valor atualizado do item na lista
        expirationDateAmount: itemToSave.expirationDateAmount, 
        expirationDateUnit: itemToSave.expirationDateUnit, // Usando a unidade do item
        doesBlock: itemToSave.doesBlock, // Usando o valor atualizado do item
      };

      const url = `${ip}/document/branch/document-matrix/expiration/update/${id}`;
      const params = { replicate: false };
      
      console.log(`handleSaveAll: Chamando POST ${url}`);
      console.log("handleSaveAll: Parâmetros:", params);
      console.log("handleSaveAll: Payload de envio:", payload);
      
      await axios.post(url, payload, {
        headers: { Authorization: `Bearer ${token}` },
        params: params,
      });
      
      console.log("handleSaveAll: Requisição POST concluída com sucesso.");
      
      // Revalida a lista após o salvamento
      await fetchExpirations(); 
      
      setEditingId(null);
      setIsEditing(false);
      console.log("handleSaveAll: Edição finalizada.");

    } catch (err: any) {
      console.error("handleSaveAll: Erro ao salvar a validade do documento:", err);
      if (err.response) {
        console.error("handleSaveAll: Detalhes do erro da resposta:", err.response.data);
      }
    } finally {
      setIsSaving(false);
      console.log("handleSaveAll: Processo de salvamento finalizado.");
    }
  };

  if (expirationList.length === 0) return null;

  return (
    <div>
      <div className="flex justify-end mb-2">
        <button
          onClick={isEditing ? handleSaveAll : () => setIsEditing(true)}
          className={`font-semibold text-sm ${
            isEditing ? "text-green-600" : "text-blue-600"
          }`}
          disabled={isSaving}
        >
          {isSaving ? "Salvando..." : isEditing ? "Salvar" : "Editar"}
        </button>
      </div>
      <table className="w-full text-sm border border-gray-300">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-2 py-1 text-left">Título</th>
            <th className="px-2 py-1 text-left">Validade (meses)</th>
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
                        setEditingId(doc.idDocument);
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
                    <input
                      type="checkbox"
                      checked={doc.doesBlock}
                      onChange={(e) => {
                        setEditingId(doc.idDocument);
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
    </div>
  );
}