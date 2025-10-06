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
  // NOVO: Propriedade para forçar a atualização da lista
  refreshTrigger?: number; 
}

export function ValidateSection({
  idBranch,
  documentTypeName,
  isSelected,
  refreshTrigger, // NOVO: Receber a nova prop
}: ValidateSectionProps) {
  const [expirationList, setExpirationList] = useState<ExpirationItem[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [amountEdit, setAmountEdit] = useState(0);
  const [doesBlockEdit, setBlockEdit] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const fetchExpirations = async () => {
    if (!idBranch || !documentTypeName) return;

    const token = localStorage.getItem("tokenClient");
    if (!token) {
      console.error("Token não encontrado.");
      return;
    }

    try {
      const { data } = await axios.get<ExpirationItem[]>(
        `${ip}/document/branch/document-matrix/expiration/${idBranch}`,
        {
          headers: { Authorization: `Bearer ${token}` },
          // Adicionamos um _ts para garantir que o navegador não use cache,
          // embora a dependência do useEffect já ajude nisso.
          params: { documentTypeName, isSelected: true, replicate: false, _ts: Date.now() }, 
        }
      );

      const normalized = (data ?? []).map((d) => ({
        ...d,
        expirationDateAmount: Number(d.expirationDateAmount ?? 0),
        expirationDateUnit: (d.expirationDateUnit as any) ?? "MONTHS",
        doesBlock: !!(d as any).doesBlock,
      }));

      setExpirationList(normalized);
    } catch (err) {
      console.error("Erro ao buscar validade dos documentos:", err);
    }
  };

  // 🚨 ATUALIZADO: Adicionando 'refreshTrigger' nas dependências 🚨
  useEffect(() => {
    fetchExpirations();
  }, [idBranch, documentTypeName, isSelected, refreshTrigger]); // Agora a busca é refeita sempre que refreshTrigger mudar

  const handleEditClick = (doc: ExpirationItem) => {
    setEditingId(doc.idDocument);
    setAmountEdit(Number(doc.expirationDateAmount ?? 0));
    setBlockEdit(!!doc.doesBlock); 
  };

  const handleSave = async (id: string) => {
    setIsSaving(true);
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("Token não encontrado.");
        return;
      }

      const payload = {
        expirationDateAmount: amountEdit,
        expirationDateUnit: "MONTHS",
        doesBlock: doesBlockEdit,
      };

      await axios.post(
        `${ip}/document/branch/document-matrix/expiration/update/${id}`,
        payload,
        {
          headers: { Authorization: `Bearer ${token}` },
          params: { replicate: false },
        }
      );

      // A lista será atualizada aqui também, garantindo que o estado local reflita a mudança
      await fetchExpirations(); 
      setEditingId(null);
    } catch (err: any) {
      console.error("Erro ao salvar validade:", err);
      if (err.response) console.error("Detalhes do erro:", err.response.data);
    } finally {
      setIsSaving(false);
    }
  };

  if (expirationList.length === 0) return null;

  return (
    <table className="w-full text-sm border border-gray-300">
      <thead className="bg-gray-100">
        <tr>
          <th className="px-2 py-1 text-left">Título</th>
          <th className="px-2 py-1 text-left">Validade (meses)</th>
          <th className="px-2 py-1 text-left">
            Bloqueia 
          </th>
          <th className="px-2 py-1 text-left">Ações</th>
        </tr>
      </thead>
      <tbody>
        {expirationList.map((doc) => (
          <tr key={doc.idDocument} className="border-t">
            <td className="px-2 py-1 font-medium">{doc.title}</td>

            {editingId === doc.idDocument ? (
              <>
                <td className="px-2 py-1 text-center">
                  <input
                    type="number"
                    min={0}
                    value={amountEdit}
                    onChange={(e) =>
                      setAmountEdit(e.target.value === "" ? 0 : parseInt(e.target.value, 10))
                    }
                    className="w-20 border px-1 py-0.5"
                    disabled={isSaving}
                  />
                </td>
                <td className="px-2 py-1 text-center">
                  <input
                    type="checkbox"
                    checked={!doesBlockEdit} 
                    onChange={() => setBlockEdit(!doesBlockEdit)} 
                    disabled={isSaving}
                  />
                </td>
                <td className="px-2 py-1">
                  <button
                    onClick={() => handleSave(doc.idDocument)}
                    className="text-green-600 font-semibold text-sm"
                    disabled={isSaving}
                  >
                    {isSaving ? "Salvando..." : "Salvar"}
                  </button>
                </td>
              </>
            ) : (
              <>
                <td className="px-2 py-1 text-center">{doc.expirationDateAmount}</td>
                <td className="px-2 py-1 text-center">
                  <input type="checkbox" checked={!doc.doesBlock} readOnly disabled />
                </td>
                <td className="px-2 py-1">
                  <button onClick={() => handleEditClick(doc)} className="text-blue-600 text-sm">
                    Editar
                  </button>
                </td>
              </>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  );
}