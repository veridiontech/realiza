import { propsBranch } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { useClient } from "./Client-Provider";

interface BranchContextProps {
  branch: propsBranch[]; 
  setBranch: React.Dispatch<React.SetStateAction<propsBranch[]>>;
  selectedBranch: propsBranch | null;
  setSelectedBranch: React.Dispatch<React.SetStateAction<propsBranch | null>>;
}

const BranchContext = createContext<BranchContextProps | undefined>(undefined);

export function useBranch() {
  const context = useContext(BranchContext);
  if (!context) {
    throw new Error("O BranchProvider não está configurado corretamente");
  }
  return context;
}

export function BranchProvider({ children }: { children: React.ReactNode }) {
  const [branch, setBranch] = useState<propsBranch[]>([]);
  const [selectedBranch, setSelectedBranch] = useState<propsBranch | null>(null);
  const { client } = useClient();

  useEffect(() => {
    if (client?.idClient) {
      setSelectedBranch(null); 
      getBranch(client.idClient);
    }
  }, [client?.idClient]);

  const getBranch = async (idClient: string) => {
    try {
      const res = await axios.get(`${ip}/branch/filtered-client?idSearch=${idClient}`);
      setBranch(res.data.content || []);
    } catch (err) {
      console.error("Erro ao buscar filiais no context", err);
      setBranch([]);
    }
  };

  return (
    <BranchContext.Provider value={{ branch, setBranch, selectedBranch, setSelectedBranch }}>
      {children}
    </BranchContext.Provider>
  );
}
