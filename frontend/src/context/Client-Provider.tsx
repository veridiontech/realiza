import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";

interface ClientContextProps {
  client: propsClient | null;
  setClient: React.Dispatch<React.SetStateAction<propsClient | null>>;
}

const ClientContext = createContext<ClientContextProps | undefined>(undefined);

export function useClient() {
  const context = useContext(ClientContext);
  if (!context) {
    throw new Error("O UserProvider não está configurado corretamente.");
  }
  return context;
}

export function ClientProvider({ children }: { children: React.ReactNode }) {
  const [client, setClient] = useState<propsClient | null>(null);

  useEffect(() => {
    const idClient = localStorage.getItem("idClient");
    if (idClient) {
      getUser(idClient);
    }
  }, []);

  const getUser = async (idClient: string) => {
    try {
      const res = await axios.get(`${ip}/client/${idClient}`);   
      if (res.data) {
        setClient(res.data);
      } else {
        console.error("Cliente não encontrado.");
        setClient(null);
      }
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      setClient(null);
    }
  };

  return (
    <ClientContext.Provider
      value={{
        client,
        setClient,
      }}
    >
      {children}
    </ClientContext.Provider>
  );
}
