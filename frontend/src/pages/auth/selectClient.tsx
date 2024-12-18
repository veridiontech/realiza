import { useState } from "react";
import selectClientImage from "@/assets/selectClientImage.png";

export function SelectClient() {
  const [searchTerm, setSearchTerm] = useState("");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [selectedClient, setSelectedClient] = useState("");

  const clients = [
    "Distribuidora de gás LTDA",
    "InovaTech Solutions",
    "UrbanWear Co.",
    "UrbanWear Co.",
    "UrbanWear Co.",
    "UrbanWear Co.",
    "UrbanWear Co.",
    "UrbanWear Co.",
    "UrbanWear Co.",
  ];

  const filteredClients = clients.filter((client) =>
    client.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="flex justify-center min-h-full m-10">
      <div className="flex justify-between bg-white w-[80rem] h-[30rem] rounded-lg">
        <div className="ml-10 mt-4">
          <h1 className="text-2xl font-semibold">Escolha seu ambiente</h1>

          <div className="w-[40rem] h-[23rem] my-10  outline outline-offset-2 outline-1 outline-slate-300 rounded-lg p-6">
            <h2 className="text-xl font-medium mb-4">Selecione um Cliente</h2>

            <div className="relative mb-4">
              <input
                type="text"
                placeholder="🔍 Procure por clientes cadastrados aqui..."
                className="w-full p-2 border border-gray-300 rounded-lg focus:outline-blue-400"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            <div className="relative">
              <button
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="w-full p-3 bg-blue-100 text-blue-600 font-medium rounded-lg flex justify-between items-center focus:outline-none"
              >
                {selectedClient || "Escolha o Cliente"}
                <span>{isDropdownOpen ? "▲" : "▼"}</span>
              </button>

              {isDropdownOpen && (
                <div
                  className="absolute w-full mt-2 bg-white border border-gray-300 rounded-lg shadow-lg max-h-40 overflow-y-auto z-10"
                >
                  {filteredClients.length > 0 ? (
                    filteredClients.map((client, index) => (
                      <div
                        key={index}
                        onClick={() => {
                          setSelectedClient(client);
                          setIsDropdownOpen(false);
                        }}
                        className="p-2 hover:bg-blue-100 cursor-pointer"
                      >
                        {client}
                      </div>
                    ))
                  ) : (
                    <div className="p-2 text-gray-500">Nenhum cliente encontrado</div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="mx-8 my-4 w-[30rem] h-[28rem] rounded-lg bg-blue-50">
          <img
            src={selectClientImage}
            alt="imagem de seleção de cliente"
            className="object-cover w-full h-full rounded-lg"
          />
        </div>
      </div>
    </div>
  );
}
