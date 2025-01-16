import { useState, useEffect } from "react";
import { Dropdown } from "@/components/ui/dropdown";
import { useFetchClients } from "@/hooks/gets/useClients";
import selectClientImage from "@/assets/selectClientImage.png";
import { Dialog } from "@/components/ui/dialog";
import { ModalSendEmail } from "@/components/modal-send-email";

export function SelectClient() {
  const { clients, loading, error, fetchClients } = useFetchClients(); // Hook customizado
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedClient, setSelectedClient] = useState<number | null>(null); // Ajustado para ser number | null

  useEffect(() => {
    fetchClients(); // Busca os clientes ao carregar o componente
  }, []);

  // Verifica se os clientes estão definidos e não são nulos
  const filteredClients = (clients || []).filter(
    (client) =>
      client &&
      client.name &&
      client.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary border-realizaBlue flex h-[30rem] w-[80rem] justify-between rounded-lg border bg-white shadow-md dark:border-white">
        <div className="ml-10 mt-4">
          <h1 className="text-2xl font-semibold">Escolha seu ambiente</h1>

          <div className="dark:bg-primary-foreground my-10 h-[23rem] w-[40rem] rounded-lg p-6 outline outline-1 outline-offset-2 outline-slate-300">
            <div className="flex items-start justify-between">
              <h2 className="mb-4 text-xl font-medium">Selecione um Cliente</h2>
              <ModalSendEmail />
            </div>
            <div className="relative mb-4">
              <input
                type="text"
                placeholder="🔍 Procure por clientes cadastrados aqui..."
                className="w-full rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {loading ? (
              <div className="flex items-center justify-center p-4">
                <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-blue-600"></div>
                <span className="ml-2 text-gray-500">Carregando...</span>
              </div>
            ) : error ? (
              <div className="p-2 text-red-500">{error}</div>
            ) : (
              <Dropdown
                options={filteredClients.map((client) => ({
                  id: Number(client.id), // Converte id para number
                  name: client.name,
                }))}
                selectedOption={selectedClient}
                onSelect={(option) => setSelectedClient(option.id)} // Atualiza o ID do cliente selecionado
                placeholder="Escolha o Cliente"
              />
            )}
          </div>
        </div>

        <div className="mx-8 my-4 h-[28rem] w-[30rem] rounded-lg bg-blue-50">
          <img
            src={selectClientImage}
            alt="imagem de seleção de cliente"
            className="h-full w-full rounded-lg object-cover"
          />
        </div>
      </div>
    </div>
  );
}
