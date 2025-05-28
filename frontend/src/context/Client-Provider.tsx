import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { propsBranch, propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";

interface ClientContextProps {
  client: propsClient | null;
  setClient: React.Dispatch<React.SetStateAction<propsClient | null>>;
  refreshClient: (id: string) => Promise<void>;
  refreshClients: () => Promise<void>;
  branches: propsBranch | null;
  setBranches: React.Dispatch<React.SetStateAction<propsBranch | null>>;
  clients: propsClient[];
  setClients: React.Dispatch<React.SetStateAction<propsClient[]>>;
  addClient: (newClient: propsClient) => void;
}

const ClientContext = createContext<ClientContextProps | undefined>(undefined);

export function useClient() {
  const context = useContext(ClientContext);
  if (!context) {
    throw new Error("O ClientProvider não está configurado corretamente.");
  }
  return context;
}

export function ClientProvider({ children }: { children: React.ReactNode }) {
  const [client, setClient] = useState<propsClient | null>(null);
  const [branches, setBranches] = useState<propsBranch | null>(null);
  const [clients, setClients] = useState<propsClient[]>([]);

  useEffect(() => {
    const idClient = localStorage.getItem("idClient");
    if (idClient) {
      refreshClient(idClient);
    }
  }, []);

  useEffect(() => {
    const idClient = localStorage.getItem("idClient");
    if (client && idClient) {
      getBranches(idClient);
    }
  }, [client]);

  const refreshClient = async (idClient: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/client/${idClient}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setClient(res.data);
    } catch (error) {
      console.error("Erro ao buscar cliente:", error);
      setClient(null);
    }
  };

  const refreshClients = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const firstRes = await axios.get(`${ip}/client`, {
        params: { page: 0, size: 1000 },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      const totalPages = firstRes.data.totalPages;
      const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
        axios.get(`${ip}/client`, {
          params: { page: i + 1, size: 1000 },
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        })
      );

      const responses = await Promise.all(requests);
      const allClients = [
        firstRes.data.content,
        ...responses.map((res) => res.data.content),
      ].flat();

      setClients(allClients);
    } catch (err) {
      console.error("Erro ao atualizar lista de clientes:", err);
    }
  };

  const getBranches = async (idClient: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setBranches(res.data.content);
    } catch (err) {
      console.log("Erro ao puxar filiais:", err);
    }
  };

  const addClient = (newClient: propsClient) => {
    setClients((prev) => [...prev, newClient]);
  };

  return (
    <ClientContext.Provider
      value={{
        client,
        setClient,
        refreshClient,
        refreshClients,
        branches,
        setBranches,
        clients,
        setClients,
        addClient,
      }}
    >
      {children}
    </ClientContext.Provider>
  );
}
