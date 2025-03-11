import { ModalAddContract } from "@/components/realizaAddContract";
import { useBranch } from "@/context/Branch-provider";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

export default function ContractsTable() {
  const { selectedBranch } = useBranch();
  const [contracts, setContracts] = useState([]);

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

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getContract();
    }
  }, [selectedBranch?.idBranch]);

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white p-10">
        {/* Cabeçalho com o título e botão para adicionar contrato */}
        <div className="m-8 flex items-center justify-between">
          <h1 className="text-2xl">Contratos</h1>
          <ModalAddContract /> {/* Botão Novo Contrato */}
        </div>
        <Dialog>
          <DialogTrigger asChild>
            <div className="border-l-realizaBlue w-auto cursor-pointer rounded-lg border border-l-8 p-5 shadow-lg hover:bg-gray-100">
              {contracts.map((contract: any) => (
                <div
                  className="flex flex-col items-start gap-5"
                  key={contract.idContract}
                >
                  <div className="flex flex-col gap-1">
                    <h1>Nome do serviço: </h1>
                    <p>{contract.serviceName}</p>
                  </div>
                  <div className="text-[12px]">
                    <p>Data de início</p>
                    <p>
                      {new Date(contract.startDate).toLocaleDateString("pt-BR")}
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
                      <li>{contract.providerSupplierName}</li>
                      <div>
                        <li>Duração do serviço: {contract.serviceDuration}</li>
                      </div>
                    </div>
                  </div>
                  <div>
                    <p>Data de início</p>
                    <p>{new Date(contract.startDate).toLocaleDateString("pt-BR")}</p>
                  </div>
                </div>
              ))}
            </DialogHeader>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
}
