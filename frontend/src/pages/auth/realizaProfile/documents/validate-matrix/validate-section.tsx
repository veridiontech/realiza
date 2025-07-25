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
}

export function ValidateSection({
  idBranch,
  documentTypeName,
  isSelected,
}: ValidateSectionProps) {
  const [expirationList, setExpirationList] = useState<ExpirationItem[]>([]);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [amountEdit, setAmountEdit] = useState(0);
  const [unitEdit, setUnitEdit] = useState<"DAYS" | "WEEKS" | "MONTHS">("DAYS");
  const [doesBlockEdit, setBlockEdit] = useState(false);

  useEffect(() => {
    if (!idBranch || !documentTypeName) return;
    const token = localStorage.getItem("tokenClient");
    if (!token) {
      console.error("Token não encontrado.");
      return;
    }

    axios
      .get<ExpirationItem[]>(
        `${ip}/document/branch/document-matrix/expiration/${idBranch}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            documentTypeName,
            isSelected: true,
            replicate: false,
          },
        }
      )
      .then((res) => setExpirationList(res.data))
      .catch((err) =>
        console.error("Erro ao buscar validade dos documentos:", err)
      );
  }, [idBranch, documentTypeName, isSelected]);

  // const traduzUnidade = (unit: ExpirationItem["expirationDateUnit"]) => {
  //   switch (unit) {
  //     case "DAYS":
  //       return "dias";
  //     case "WEEKS":
  //       return "semanas";
  //     case "MONTHS":
  //       return "meses";
  //     default:
  //       return unit;
  //   }
  // };

  const handleEditClick = (doc: ExpirationItem) => {
    setEditingId(doc.idDocument);
    setAmountEdit(doc.expirationDateAmount);
    setUnitEdit(doc.expirationDateUnit);
    setBlockEdit(doc.doesBlock);
  };

  const handleSave = async (id: string) => {
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("Token não encontrado.");
        return;
      }

      if (amountEdit <= 0 || !["DAYS", "WEEKS", "MONTHS"].includes(unitEdit)) {
        console.warn("Valores inválidos para salvar:", amountEdit, unitEdit);
        return;
      }

      const payload = {
        expirationDateAmount: amountEdit,
        expirationDateUnit: unitEdit,
        doesBlock: doesBlockEdit,
      };

      console.log("🔁 Enviando para API:", payload);

      const response = await axios.post(
        `${ip}/document/branch/document-matrix/expiration/update/${id}`,
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          params: {
            replicate: false,
          },
        }
      );

      console.log("✅ Sucesso:", response.data);

      setExpirationList((prev) =>
        prev.map((doc) =>
          doc.idDocument === id
            ? {
                ...doc,
                expirationDateAmount: amountEdit,
                expirationDateUnit: unitEdit,
                doesBlock: doesBlockEdit,
              }
            : doc
        )
      );

      setEditingId(null);
    } catch (err: any) {
      console.error("❌ Erro ao salvar validade:", err);
      if (err.response) {
        console.error("📄 Detalhes do erro:", err.response.data);
      }
    }
  };

  if (expirationList.length === 0) return null;

  return (
    <table className="w-full text-sm border border-gray-300">
      <thead className="bg-gray-100">
        <tr>
          <th className="px-2 py-1 text-left">Título</th>
          <th className="px-2 py-1 text-left">Meses</th>
          {/* <th className="px-2 py-1 text-left">Unidade</th> */}
          <th className="px-2 py-1 text-left">Bloqueia</th>
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
                    min={1}
                    value={amountEdit}
                    onChange={(e) => {
                      const value = e.target.value;
                      setAmountEdit(value === "" ? 0 : parseInt(value, 10));
                    }}
                    className="w-20 border px-1 py-0.5"
                  />
                </td>
                {/* <td className="px-2 py-1">
                  <select
                    value={unitEdit}
                    onChange={(e) =>
                      setUnitEdit(
                        e.target.value as ExpirationItem["expirationDateUnit"]
                      )
                    }
                    className="border px-1 py-0.5"
                  >
                    <option value="DAYS">Dias</option>
                    <option value="WEEKS">Semanas</option>
                    <option value="MONTHS">Meses</option>
                  </select>
                </td> */}
                <td className="px-2 py-1 text-center">
                  <input
                    type="checkbox"
                    checked={doesBlockEdit}
                    onChange={() => setBlockEdit(!doesBlockEdit)}
                  />
                </td>
                <td className="px-2 py-1">
                  <button
                    onClick={() => handleSave(doc.idDocument)}
                    className="text-green-600 font-semibold text-sm"
                  >
                    Salvar
                  </button>
                </td>
              </>
            ) : (
              <>
                <td className="px-2 py-1 text-center">{doc.expirationDateAmount}</td>
                {/* <td className="px-2 py-1">
                  {traduzUnidade(doc.expirationDateUnit)}
                </td> */}
                <td className="px-2 py-1 text-center">
                  <input type="checkbox" checked={doc.doesBlock} disabled />
                </td>
                <td className="px-2 py-1">
                  <button
                    onClick={() => handleEditClick(doc)}
                    className="text-blue-600 text-sm"
                  >
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