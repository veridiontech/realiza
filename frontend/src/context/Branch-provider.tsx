import { propsBranch } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";

interface BranchContextProps {
  branch: propsBranch | null;
  setBranch: React.Dispatch<React.SetStateAction<propsBranch | null>>;
}

const BranchContext = createContext<BranchContextProps | undefined>(undefined);

export function userBranch() {
  const context = useContext(BranchContext);
  if (!context) {
    throw new Error("O BranchProvider não está configurado corretamente");
  }
  return context;
}

export function BranchProvider({ children }: { children: React.ReactNode }) {
  const [branch, setBranch] = useState<propsBranch | null>(null);

  const getBranch = async (idBranch: string) => {
    try {
      const res = await axios.get(`${ip}/branch/${idBranch}`);
      if (res.data) {
        setBranch(res.data);
      } else {
        console.log("cliente não encontrado");
        setBranch(null);
      }
    } catch (err) {
      console.log("erro ao buscar filiais no context", err);
    }
  };

  useEffect(() => {
    const idBranch = localStorage.getItem("idBranch");
    if (idBranch) {
      getBranch(idBranch);
    }
  });
  return (
    <BranchContext.Provider
      value={{
        branch,
        setBranch,
      }}
    >
      {children}
    </BranchContext.Provider>
  );
}
