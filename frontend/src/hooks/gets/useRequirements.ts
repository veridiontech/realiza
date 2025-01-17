import { useState, useEffect } from "react";
import axios from "axios";

export interface Requirement {
  idRequirement: string;
  title: string;
}

export function useRequirements() {
  const [requirements, setRequirements] = useState<Requirement[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRequirements = async () => {
      try {
        const response = await axios.get(
          "https://realiza.onrender.com/contract/requirement",
        );
        setRequirements(response.data.content); // Adaptado para o formato de resposta da API
      } catch (err: any) {
        setError(err.message || "Erro ao buscar requisitos.");
      } finally {
        setLoading(false);
      }
    };

    fetchRequirements();
  }, []);

  return { requirements, loading, error };
}
