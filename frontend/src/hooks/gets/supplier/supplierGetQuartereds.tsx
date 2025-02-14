// Arquivo: /hooks/gets/supplier/supplierGetQuartereds.ts
import { useState } from "react";
import axios from "axios";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";

// Interface representando os dados de um subcontratado retornados pela API
export interface QuarteredProps {
  idProvider: string;
  cnpj: string;
  tradeName: string;
  corporateName: string;
  email: string;
  cep: string;
  state: string;
  city: string;
  address: string;
  number: string;
  supplier: string;
  logoData?: string;
}

const API_URL = `${ip}/subcontractor/filtered-supplier`;

export function useSupplierQuartereds() {
  const { user } = useUser();
  const [quartereds, setQuartereds] = useState<QuarteredProps[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchQuartereds(
    limit: number,
    page: number,
    supplierId?: string,
  ) {
    if (!user) {
      setError("Usuário não autenticado");
      return;
    }
    setLoading(true);
    setError(null);

    try {
      const response = await axios.get(API_URL, {
        params: {
          _limit: limit,
          _page: page,
          idSearch: supplierId, // Filtra pelo ID do supplier
        },
      });

      const mappedSubcontractors = response.data.content.map((sub: any) => ({
        idProvider: sub.idProvider,
        cnpj: sub.cnpj,
        tradeName: sub.tradeName,
        corporateName: sub.corporateName,
        email: sub.email,
        cep: sub.cep,
        state: sub.state,
        city: sub.city,
        address: sub.address,
        number: sub.number,
        supplier: sub.supplier,
        logoData: sub.logoData,
      }));
      setQuartereds(mappedSubcontractors);
      setTotalPages(response.data.totalPages);
    } catch (err: any) {
      setError(err.message || "Erro ao buscar subcontratados.");
    } finally {
      setLoading(false);
    }
  }

  return { quartereds, totalPages, error, loading, fetchQuartereds };
}
