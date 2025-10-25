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
  const [amountEdit, ] = useState(0);
  const [doesBlockEdit, ] = useState(false);

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
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("Token não encontrado.");
        setIsSaving(false);
        return;
      }

      if (!editingId) {
          console.error("Nenhum documento em edição.");
          setIsSaving(false);
          return;
      }
      const id = editingId;

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

      await fetchExpirations();
      setEditingId(null);
      setIsEditing(false);
    } catch (err: any) {
      console.error("Erro ao salvar todas as validades:", err);
      if (err.response) console.error("Detalhes do erro:", err.response.data);
    } finally {
      setIsSaving(false);
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