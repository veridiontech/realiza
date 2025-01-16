import axios from "axios";
import { useState } from "react";

interface Client {
  id: string;
  name: string;
}

interface CreateClientData {
  name: string;
}

const API_URL = "https://realiza.onrender.com/client";

// Hook para buscar todos os clientes
export function useFetchClients() {
  const [clients, setClients] = useState<Client[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function fetchClients() {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(API_URL);
      setClients(response.data.content); // Supondo que a API retorna paginado
    } catch (err: any) {
      setError(err.message || "Erro ao buscar clientes.");
    } finally {
      setLoading(false);
    }
  }

  return { clients, error, loading, fetchClients };
}

// Hook para criar um cliente
export function useCreateClient() {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function createClient(newClient: CreateClientData) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.post<Client>(API_URL, newClient);
      return response.data; // Retorna o cliente criado
    } catch (err: any) {
      setError(err.message || "Erro ao criar cliente.");
    } finally {
      setLoading(false);
    }
  }

  return { createClient, error, loading };
}

// Hook para deletar um cliente
export function useDeleteClient() {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function deleteClient(id: string) {
    setLoading(true);
    setError(null);
    try {
      await axios.delete(`${API_URL}/${id}`);
    } catch (err: any) {
      setError(err.message || "Erro ao deletar cliente.");
    } finally {
      setLoading(false);
    }
  }

  return { deleteClient, error, loading };
}
