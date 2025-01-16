import axios from "axios";
import { useState } from "react";
import { ip } from "@/utils/ip";

export interface ServiceProviderProps {
  idProvider: string;
  cnpj: string;
  client: string;
}

const API_URL = `${ip}/supplier`; // Altere para a URL real da API, se necessário

export function useFetchServiceProviders() {
  const [serviceProviders, setServiceProviders] = useState<
    ServiceProviderProps[]
  >([]);
  const [totalPages, setTotalPages] = useState(0); // Total de páginas para paginação
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  async function fetchServiceProviders(limit = 5, page = 0) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        `${API_URL}?_limit=${limit}&_page=${page}`,
      );
      setServiceProviders(response.data.content); // Ajuste para pegar o array `content`
      setTotalPages(response.data.totalPages); // Ajusta o total de páginas
    } catch (err: any) {
      setError(err.message || "Erro ao buscar prestadores de serviço.");
    } finally {
      setLoading(false);
    }
  }

  return {
    serviceProviders,
    totalPages,
    error,
    loading,
    fetchServiceProviders,
  };
}
