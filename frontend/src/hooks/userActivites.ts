import { useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

export function useActivities() {
  const [activities, setActivities] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const getActivities = async (idBranch: string) => {
    const token = localStorage.getItem("tokenClient");
    setIsLoading(true);
    try {
      const res = await axios.get(
        `${ip}/contract/activity/find-by-branch/${idBranch}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setActivities(res.data);
    } catch (err) {
      console.error("Erro ao buscar atividades:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return { activities, isLoading, getActivities };
}
