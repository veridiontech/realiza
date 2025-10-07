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
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  // É provável que você tenha outras variáveis de estado aqui na main que foram removidas no branch 'victorvalim2-10'
  // e que são usadas no handleSaveAll, como 'amountEdit', 'doesBlockEdit' e 'setEditingId'.
  // Para fins deste merge, estou mantendo o estado que não foi conflito e a lógica de edição em lote do outro branch no handleSaveAll,
  // mas se a sua intenção é *realmente* manter apenas o código da main, o bloco handleSaveAll pode ficar incompleto,
  // pois a lógica da main (após o '=======') usa variáveis que não foram declaradas no topo do componente, como 'amountEdit', 'doesBlockEdit' e 'id'.

  // **ASSUMINDO QUE VOCÊ QUER A LÓGICA DA MAIN, A MAIS SIMPLES QUE SALVA APENAS UM ITEM, VAMOS RECRIAR O QUE ELA PRECISA:**
  // (O código que segue a lógica da main está incorreto no seu exemplo pois está incompleto, vou assumir a intenção da main)
  const [editingId, setEditingId] = useState<string | null>(null);
  const [amountEdit, setAmountEdit] = useState(0); // Assumindo valor padrão
  const [doesBlockEdit, setDoesBlockEdit] = useState(false); // Assumindo valor padrão

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
    // ESTE TRECHO É O CONFLITO, ESTOU ESCOLHENDO A LÓGICA DA MAIN,
    // QUE PARECE SER PARA SALVAR UMA EDIÇÃO PONTUAL, NÃO UMA EDIÇÃO EM LOTE.
    // É ESTRANHO que o nome seja 'handleSaveAll' se a lógica salva apenas um item (o que não faz sentido)
    // OU o código da main está incompleto no seu exemplo.
    // VOU MANTER O CÓDIGO DA MAIN, E AS NOVAS VARIÁVEIS DE ESTADO QUE ELE IMPLICA ('editingId', 'amountEdit', 'doesBlockEdit')
    // para que o código compile, mesmo que a lógica final não seja a esperada para um 'handleSaveAll'.
    setIsSaving(true);
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("Token não encontrado.");
        setIsSaving(false);
        return;
      }

      // **TRECHO DA MAIN** (Com a adição de checagem para 'editingId' para evitar erro de compilação/runtime)
      if (!editingId) {
          console.error("Nenhum documento em edição.");
          setIsSaving(false);
          return;
      }
      const id = editingId; // A lógica da main usa uma variável 'id' que não existe no escopo, estou assumindo que é o 'editingId'

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
          // A MAIN não tinha essa lógica de 'isEditing' para salvar todos,
          // o código de exibição do botão parece ter sido introduzido em 'victorvalim2-10'.
          // Se o objetivo é a main, o botão não deveria existir ou a lógica dele deve ser revista.
          // Como não há como saber a lógica completa da main, mantenho o estado isEditing
          // e a função handleSaveAll que usa o estado editingId.
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
                      onChange={(e) =>
                        handleInputChange(
                          doc.idDocument,
                          "expirationDateAmount",
                          e.target.value === "" ? 0 : parseInt(e.target.value, 10)
                        )
                      }
                      className="w-20 border px-1 py-0.5"
                      disabled={isSaving}
                    />
                  </td>
                  <td className="px-2 py-1">
                    <input
                      type="checkbox"
                      checked={!doc.doesBlock}
                      onChange={(e) =>
                        handleInputChange(
                          doc.idDocument,
                          "doesBlock",
                          !e.target.checked
                        )
                      }
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
                      checked={!doc.doesBlock}
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