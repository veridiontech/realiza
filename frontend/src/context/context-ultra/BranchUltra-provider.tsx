import { propsBranchUltra } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { useCenter } from "./Center-provider";

interface BoardContextProps {
  branchUltra: propsBranchUltra[];
  setBranchUltra: React.Dispatch<React.SetStateAction<propsBranchUltra[]>>;
  selectedBranchUltra: propsBranchUltra | null
  setSelectedBranchUltra: React.Dispatch<React.SetStateAction<propsBranchUltra | null>>;
}

const BranchUltraContext = createContext<BoardContextProps | undefined>(undefined);

export function useBranchUltra() {
  const context = useContext(BranchUltraContext);
  if (!context) {
    throw new Error("O BoardProvider não está configurado corretamente");
  }
  return context;
}

export function BranchUltraProvider({ children }: { children: React.ReactNode }) {
  const [branchUltra, setBranchUltra] = useState<propsBranchUltra[]>([]);
  const [selectedBranchUltra, setSelectedBranchUltra] = useState<propsBranchUltra | null>(null)
  const { selectedCenter } = useCenter()

  useEffect(() => {
    if (selectedCenter?.idCenter) {
      getBranchUltra(selectedCenter.idCenter)
    }
  }, [selectedCenter?.idCenter])

  const getBranchUltra = async (idCenter: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res =
        await axios.get(`${ip}/branch/find-by-center?idCenter=${idCenter}
`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
        );
      setBranchUltra(res.data.content)
    } catch (err) {
      console.log("Erro ao buscar diretoria no BranchUltraProvider", err);

    }
  };

  return (
    <BranchUltraContext.Provider value={{ branchUltra, setBranchUltra, selectedBranchUltra, setSelectedBranchUltra }}>
      {children}
    </BranchUltraContext.Provider>
  );
}
