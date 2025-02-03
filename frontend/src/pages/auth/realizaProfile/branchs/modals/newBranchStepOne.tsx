import { Modal } from "@/components/modal";
import { useState } from "react";
import { Search } from "lucide-react";
import { fetchCompanyByCNPJ } from "@/hooks/gets/useCnpjApi";

interface SearchCnpjModalProps {
  onClose: () => void;
  onProceed: (cnpj: string) => void;
}

export function SearchCnpjModal({ onClose, onProceed }: SearchCnpjModalProps) {
  const [cnpj, setCnpj] = useState("");
  const [providerData, setProviderData] = useState<{
    nome?: string;
    cnpj?: string;
  } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSearchCNPJ = async () => {
    setError(null);
    setLoading(true);

    try {
      const cleanedCNPJ = cnpj.replace(/\D/g, "");
      if (!cleanedCNPJ || cleanedCNPJ.length !== 14) {
        throw new Error("CNPJ inválido. Por favor, verifique o formato.");
      }

      const data = await fetchCompanyByCNPJ(cleanedCNPJ);
      setProviderData({
        nome: data.razaoSocial || data.nomeFantasia,
        cnpj: cleanedCNPJ,
      });
    } catch (err: any) {
      setError(err.message || "Erro desconhecido.");
      setProviderData(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Buscar Filial"
      onClose={onClose}
      onSubmit={() => {
        if (providerData?.cnpj) {
          onProceed(providerData.cnpj);
        }
      }}
      fields={[
        {
          name: "cnpj",
          label: "CNPJ",
          type: "custom",
          render: ({ value, onChange }) => (
            <div className="flex flex-col gap-2">
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  value={value}
                  onChange={(e) => {
                    setCnpj(e.target.value);
                    onChange(e.target.value);
                  }}
                  placeholder="00.000.000/0001-01"
                  required
                  className="flex-1 rounded-md border border-gray-300 px-3 py-2 text-black shadow-sm focus:border-blue-500 focus:ring-blue-500"
                />
                <button
                  type="button"
                  onClick={handleSearchCNPJ}
                  className="rounded-md bg-blue-600 px-3 py-2 text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
                  disabled={loading}
                >
                  {loading ? "Buscando..." : <Search className="h-5 w-5" />}
                </button>
              </div>
              {providerData?.nome && (
                <span className="text-sm text-white">
                  Nome: {providerData.nome}
                </span>
              )}
              {error && <p className="text-sm text-red-500">{error}</p>}
            </div>
          ),
        },
      ]}
    />
  );
}
