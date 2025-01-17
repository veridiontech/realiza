import { useState, useEffect } from "react";
import axios from "axios";

export interface Activity {
  idActivity: string;
  title: string;
}

export function useActivities() {
  const [activities, setActivities] = useState<Activity[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchActivities = async () => {
      try {
        const response = await axios.get(
          "https://realiza.onrender.com/contract/activity",
        );
        setActivities(response.data.content); // Adaptado para o formato de resposta da API
      } catch (err: any) {
        setError(err.message || "Erro ao buscar atividades.");
      } finally {
        setLoading(false);
      }
    };

    fetchActivities();
  }, []);

  return { activities, loading, error };
}
