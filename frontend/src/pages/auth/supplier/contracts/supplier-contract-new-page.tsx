import { useSupplier } from "@/context/Supplier-context";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { CardContract } from "../../realizaProfile/contracts/card-contract";
import { ModalAddContract } from "@/components/realizaAddContract";

export function SupplierContractNewPage() {
  const { supplier } = useSupplier();
  const [contracts, setContracts] = useState([]);

  const getContracts = async () => {
    try {
      const res = await axios.get(
        `${ip}/contract/supplier/filtered-supplier?idSearch=${supplier?.idProvider}`,
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

  return (
    <div className="m-10 flex min-h-full justify-center">
          <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white p-10">
            <div className="m-8 flex items-center justify-between">
              <h1 className="text-2xl">Contratos</h1>
              <ModalAddContract /> 
            </div>
            <div className="flex flex-col gap-">
              <CardContract />
            </div>
          </div>
        </div>
  );
}
