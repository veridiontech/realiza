import { propsBoard } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import { createContext, useContext, useEffect, useState } from "react";
import { useClient } from "../Client-Provider";

interface BoardContextProps {
  boards: propsBoard[];
  setBoards: React.Dispatch<React.SetStateAction<propsBoard[]>>;
  selectedBoard: propsBoard | null
  setSelectedBoard: React.Dispatch<React.SetStateAction<propsBoard | null>>;
}

const BoardContext = createContext<BoardContextProps | undefined>(undefined);

export function useBoard() {
  const context = useContext(BoardContext);
  if (!context) {
    throw new Error("O BoardProvider não está configurado corretamente");
  }
  return context;
}

export function BoardProvider({ children }: { children: React.ReactNode }) {
  const [boards, setBoards] = useState<propsBoard[]>([]);
  const [selectedBoard, setSelectedBoard] = useState<propsBoard | null>(null)
  const { client } = useClient()

  useEffect(() => {
    if (client?.idClient) {
      getBoard(client.idClient)
    }
  }, [client?.idClient])

  const getBoard = async (idClient: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res =
        await axios.get(`${ip}/ultragaz/board/find-by-client?idClient=${idClient}
`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        });
      setBoards(res.data.content)
    } catch (err) {
      console.log("Erro ao buscar diretoria no boardProvider", err);
    }
  };

  return (
    <BoardContext.Provider value={{ boards, setBoards, selectedBoard, setSelectedBoard }}>
      {children}
    </BoardContext.Provider>
  );
}
