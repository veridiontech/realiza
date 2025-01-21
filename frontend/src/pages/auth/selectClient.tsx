import { useState, useEffect } from "react";
// import { Dropdown } from "@/components/ui/dropdown";
// import { useFetchClients } from "@/hooks/gets/useClients";
import selectClientImage from "@/assets/selectClientImage.png";
// import { Dialog } from "@/components/ui/dialog";
import { ModalSendEmail } from "@/components/modal-send-email";
import { toast } from "sonner";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useUser } from "@/context/user-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Puff } from "react-loader-spinner";

export function SelectClient() {
  const [loading, setIsLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");

  const [getClients, setGetClients] = useState([]);
  const navigate = useNavigate();
  const { user } = useUser();

  const getClient = async () => {
    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/client`);
      setGetClients(res.data.content);
    } catch (err) {
      console.log("erro ao buscar clientes", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getClient();
    if (user?.idUser) {
      toast("Você está na versão 1.0.0 do sistema realiza", {
        action: (
          <Button
            className="bg-realizaBlue"
            onClick={() => navigate(`/sistema/new-features/${user.idUser}`)}
          >
            Visualizar novas funções
          </Button>
        ),
      });
    }
  }, []);

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
              <div className="flex items-center justify-start border p-2 rounded-md w-[20vw]">
                <Puff
                  visible={true}
                  height="30"
                  width="30"
                  color="#34495D"
                  ariaLabel="puff-loading"
                />
              </div>
            ) : getClients.length === 0 ? (
              <p className="text-gray-500">Nenhum cliente encontrado.</p>
            ) : (
              <select className="h-[5vh] w-[20vw] rounded-md border">
                {getClients.map((client: any) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.companyName}
                  </option>
                ))}
              </select>
            )}

            {/* 
            {loading ? (
              <div className="flex items-center justify-center p-4">
                <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-blue-600"></div>
                <span className="ml-2 text-gray-500">Carregando...</span>
              </div>
            ) : error ? (
              <div className="p-2 text-red-500">{error}</div>
            ) : (
              // <Dropdown
              //   options={filteredClients.map((client) => ({
              //     id: Number(client.id), // Converte id para number
              //     name: client.name,
              //   }))}
              //   selectedOption={selectedClient}
              //   onSelect={(option) => setSelectedClient(option.id)} // Atualiza o ID do cliente selecionado
              //   placeholder="Escolha o Cliente"
              // />
              
            )} */}
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
