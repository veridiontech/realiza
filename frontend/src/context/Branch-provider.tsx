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
  addBranch: (newBranch: propsBranch) => void;
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

  const getBranch = async (idClient: string, size: number = 1000) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${idClient}`, {
        params: { size },
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });
      setBranch(res.data.content || []);
    } catch (err) {
      console.error("Erro ao buscar filiais no context", err);
      setBranch([]);
    }
  };

  const addBranch = (newBranch: propsBranch) => {
    setBranch((prev) => [...prev, newBranch]);
  };

  return (
    <BranchContext.Provider
      value={{ branch, setBranch, selectedBranch, setSelectedBranch, addBranch }}
    >
      {children}
    </BranchContext.Provider>
  );
}
