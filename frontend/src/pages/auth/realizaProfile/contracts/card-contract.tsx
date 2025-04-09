import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";

export function CardContract() {
  const { selectedBranch } = useBranch();
  const [contracts, setContracts] = useState<any[]>([]);
  const [selectedContract, setSelectedContract] = useState<any>(null);
  const [supplierData, setSupplierData] = useState<any>(null);

  const getContract = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      setContracts(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar contratos:", err);
    }
  };

  const getSupplier = async (supplierId: string) => {
    try {
      const res = await axios.get(`${ip}/supplier/${supplierId}`);
      setSupplierData(res.data);
    } catch (err) {
      console.log("Erro ao buscar fornecedor:", err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getContract();
    }
  }, [selectedBranch?.idBranch]);

  return (
    <div className="grid grid-cols-2 gap-x-1 gap-y-8">
      {contracts.map((contract) => (
        <Dialog key={contract.idContract}>
          <DialogTrigger asChild>
            <div
              onClick={() => {
                setSelectedContract(contract);
                getSupplier(contract.providerSupplier);
              }}
              className="border-l-realizaBlue flex w-[33vw] cursor-pointer flex-col items-start gap-5 rounded-lg border border-l-8 p-5 shadow-lg hover:bg-gray-100"
            >
              <div>
                <div className="flex gap-1">
                  <strong>Nome do serviço: </strong>
                  <p>{contract.serviceName}</p>
                </div>
                <div>
                  <h2 className="text-[12px]">
                    <strong>Referência de contrato:</strong>{" "}
                    {contract.contractReference}
                  </h2>
                </div>
              </div>
              <div className="text-[12px]">
                <strong>Data de início</strong>
                <p>
                  {new Date(contract.dateStart).toLocaleDateString("pt-BR")}
                </p>
              </div>
            </div>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader className="flex flex-col gap-5">
              <DialogTitle>Detalhes do Contrato</DialogTitle>
              {selectedContract && (
                <>
                  <div className="flex flex-col gap-1">
                    <h1>Nome do serviço: {selectedContract.serviceName}</h1>
                    <div className="text-[12px]">
                      <p>{selectedContract.description}</p>
                    </div>
                  </div>
                  <div>
                    <p>Prestador do serviço:</p>
                    <ul>
                      <li>{supplierData?.corporateName}</li>
                      <li>
                        Duração do serviço: {selectedContract.serviceDuration}
                      </li>
                    </ul>
                  </div>
                  <div>
                    <p>Data de início</p>
                    <p>
                      {new Date(selectedContract.dateStart).toLocaleDateString(
                        "pt-BR",
                      )}
                    </p>
                  </div>
                </>
              )}
            </DialogHeader>
          </DialogContent>
        </Dialog>
      ))}
    </div>
  );
}
