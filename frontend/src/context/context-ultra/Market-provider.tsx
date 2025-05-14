import { propsMarket } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { useBoard } from "./Board-provider";

interface BoardContextProps {
  markets: propsMarket[];
  setMarkets: React.Dispatch<React.SetStateAction<propsMarket[]>>;
  selectedMarket: propsMarket | null
  setSelectedMarket: React.Dispatch<React.SetStateAction<propsMarket | null>>;
}

const MarketContext = createContext<BoardContextProps | undefined>(undefined);

export function useMarket() {
  const context = useContext(MarketContext);
  if (!context) {
    throw new Error("O BoardProvider não está configurado corretamente");
  }
  return context;
}

export function MarketProvider({ children }: { children: React.ReactNode }) {
  const [markets, setMarkets] = useState<propsMarket[]>([]);
  const [selectedMarket, setSelectedMarket] = useState<propsMarket | null>(null)
  const { selectedBoard } = useBoard()

  useEffect(() => {
    if (selectedBoard?.idBoard) {
      getBoard(selectedBoard.idBoard)
    }
  }, [selectedBoard?.idBoard])

  const getBoard = async (idBoard: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res =
        await axios.get(`${ip}/ultragaz/market/find-by-board?idBoard=${idBoard}
`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
        );
      setMarkets(res.data.content)
    } catch (err) {
      console.log("Erro ao buscar diretoria no boardProvider", err);

    }
  };

  return (
    <MarketContext.Provider value={{ markets, setMarkets, selectedMarket, setSelectedMarket }}>
      {children}
    </MarketContext.Provider>
  );
}
