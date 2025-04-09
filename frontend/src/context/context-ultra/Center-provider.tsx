import { propsCenter } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { useMarket } from "./Market-provider";

interface BoardContextProps {
  center: propsCenter[];
  setCenter: React.Dispatch<React.SetStateAction<propsCenter[]>>;
  selectedCenter: propsCenter | null
  setSelectedCenter: React.Dispatch<React.SetStateAction<propsCenter | null>>;
}

const CenterContext = createContext<BoardContextProps | undefined>(undefined);

export function useCenter() {
  const context = useContext(CenterContext);
  if (!context) {
    throw new Error("O BoardProvider não está configurado corretamente");
  }
  return context;
}

export function CenterProvider({ children }: { children: React.ReactNode }) {
  const [center, setCenter] = useState<propsCenter[]>([]);
  const [selectedCenter, setSelectedCenter] = useState<propsCenter| null>(null)
  const {selectedMarket} = useMarket()

  useEffect(() => {
    if(selectedMarket?.idMarket) {
        getCenter(selectedMarket.idMarket)
    }
  },[selectedMarket?.idMarket])

  const getCenter = async (idMarket: string) => {
    try {
      const res =
        await axios.get(`${ip}/ultragaz/center/find-by-market?idMarket=${idMarket}
`);
      setCenter(res.data.content)
    } catch (err) {
        console.log("Erro ao buscar diretoria no CenterProvider", err);
        
    }
  };

  return (
    <CenterContext.Provider value={{ center, setCenter, selectedCenter, setSelectedCenter }}>
      {children}
    </CenterContext.Provider>
  );
}
