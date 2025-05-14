import { useSupplier } from "@/context/Supplier-context";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
// import { CardContract } from "./card/card-contract";
import { ModalContractDetails } from "./card/modal-contract-details";

// Modal para exibir detalhes do contrato

export function SupplierContractNewPage() {
  const { supplier } = useSupplier();
  const [contracts, setContracts] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedContract, setSelectedContract] = useState(null);

  const getContracts = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-supplier?idSearch=${supplier?.idProvider}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      console.log("contratos:", res.data.content);
      setContracts(res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (supplier?.idProvider) {
      getContracts();
    }
  }, [supplier?.idProvider]);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value.toLowerCase());
  };

  const filteredContracts = contracts.filter((contract: any) =>
    contract.serviceName.toLowerCase().includes(searchQuery)
  );

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg shadow-md bg-white p-10">
        <div className="m-8 flex items-center justify-between">
          <h1 className="text-2xl">Contratos</h1>
          <input
            type="text"
            className="p-2 border rounded-md"
            placeholder="Pesquisar contrato..."
            onChange={handleSearch}
            value={searchQuery}
          />
        </div>
        <div className="flex flex-wrap gap-6">
          {filteredContracts.length === 0 ? (
            <p className="text-center text-gray-500">Nenhum contrato encontrado.</p>
          ) : (
            filteredContracts.map((contract: any) => (
              <div
                key={contract.idContract}
                className="border-l-realizaBlue border-l-8 w-[300px] bg-white p-4 rounded-lg border shadow-lg hover:shadow-xl transition-all"
              >
                <h3 className="text-lg font-semibold">{contract.serviceName}</h3>
                <p className="text-sm text-gray-500">Início: {contract.dateStart}</p>
                <p className="text-sm mt-2">{contract.description}</p>
                <div className="mt-4 flex justify-between">
                  <button
                    className="text-realizaBlue hover:text-blue-700"
                    onClick={() => setSelectedContract(contract)}
                  >
                    Ver Detalhes
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
      {selectedContract && (
        <ModalContractDetails
          contract={selectedContract}
          closeModal={() => setSelectedContract(null)}
        />
      )}
    </div>
  );
}
