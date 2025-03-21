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
  const [contracts, setContracts] = useState<any>([]);
  const [supplierData, setSupplierData] = useState<any>(null)

  const getContract = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      console.log("contratos:", res.data.content);

      setContracts(res.data.content);
    } catch (err) {
      console.log("erro ao buscar documentos:", err);
    }
  };

  const getSupplier = async () => {
    try {
        const res = await axios.get(`${ip}/supplier/${contracts.providerSupplier}`)
        setSupplierData(res.data)
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getContract();
    }
    if(contracts.providerSupplier) {
        getSupplier() 
    }
  }, [selectedBranch?.idBranch]);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <div className="border-l-realizaBlue w-auto cursor-pointer rounded-lg border border-l-8 p-5 shadow-lg hover:bg-gray-100">
          {contracts.map((contract: any) => (
            <div
              className="flex flex-col items-start gap-5"
              key={contract.idContract}
            >
              <div className="flex gap-1">
                <h1>Nome do serviço: </h1>
                <p>{contract.serviceName}</p>
              </div>
              <div className="text-[12px]">
                <p>Data de início</p>
                <p>
                  {new Date(contract.dateStart).toLocaleDateString("pt-BR")}
                </p>
              </div>
            </div>
          ))}
        </div>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Detalhes do Contrato</DialogTitle>
          {contracts.map((contract: any) => (
            <div key={contract.idContract} className="flex flex-col gap-5">
              <div className="flex flex-col gap-1">
                <h1>{contract.serviceName}</h1>
                <div className="text-[12px]">
                  <p>{contract.description}</p>
                </div>
              </div>
              <div>
                <p>Prestador do serviço:</p>
                <div>
                  <li>{supplierData?.corporateName}</li>
                  <div>
                    <li>Duração do serviço: {contract.serviceDuration}</li>
                  </div>
                </div>
              </div>
              <div>
                <p>Data de início</p>
                <p>
                  {new Date(contract.startDate).toLocaleDateString("pt-BR")}
                </p>
              </div>
            </div>
          ))}
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
