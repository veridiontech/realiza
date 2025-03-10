import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { propsBranch, propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";

interface ClientContextProps {
  client: propsClient | null;
  setClient: React.Dispatch<React.SetStateAction<propsClient | null>>;
  branches: propsBranch | null;
  setBranches: React.Dispatch<React.SetStateAction<propsBranch | null>>;
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
  const [branches, setBranches] = useState<propsBranch | null>(null);

  useEffect(() => {
    const idClient = localStorage.getItem("idClient");
    if (idClient) {
      getUser(idClient);
      if (client) {
        getBranches(idClient);
      }
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

  const getBranches = async (idClient: string) => {
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${idClient}`,
      );
      setBranches(res.data.content);
    } catch (err) {
      console.log("erro ao puxar filiais:", err);
    }
  };

  return (
    <ClientContext.Provider
      value={{
        client,
        setClient,
        branches,
        setBranches,
      }}
    >
      {children}
    </ClientContext.Provider>
  );
}
