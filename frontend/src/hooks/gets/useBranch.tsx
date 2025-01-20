import axios from "axios";
import { useState } from "react";
import { BranchType } from "@/types/branch"; // Define your `Branch` type in this path
import { ip } from "@/utils/ip";

const API_URL = `${ip}/branch`;

export function useBranches() {
  const [branches, setBranches] = useState<BranchType[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function fetchBranches(
    limit = 10,
    page = 0,
    sort = "idBranch",
    direction = "ASC",
  ) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(API_URL, {
        params: { size: limit, page, sort, direction },
      });

      const { content, totalPages } = response.data;
      setBranches(
        content.map((branch: any) => ({
          id: branch.idBranch,
          name: branch.name,
          client: branch.client,
          status: branch.isActive ? "Ativo" : "Inativo",
        })),
      );
      setTotalPages(totalPages);
    } catch (err: any) {
      setError(err.message || "Erro ao carregar filiais.");
    } finally {
      setLoading(false);
    }
  }

  async function createBranch(branchData: Partial<BranchType>) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.post(API_URL, branchData);
      return response.data; // Return created branch data if needed
    } catch (err: any) {
      setError(err.message || "Erro ao criar filial.");
      throw err;
    } finally {
      setLoading(false);
    }
  }

  async function updateBranch(branchData: Partial<BranchType>) {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.put(
        `${API_URL}/${branchData.id}`,
        branchData,
      );
      return response.data; // Return updated branch data if needed
    } catch (err: any) {
      setError(err.message || "Erro ao atualizar filial.");
      throw err;
    } finally {
      setLoading(false);
    }
  }

  async function deleteBranch(branchId: string) {
    setLoading(true);
    setError(null);
    try {
      await axios.delete(`${API_URL}/${branchId}`);
    } catch (err: any) {
      setError(err.message || "Erro ao excluir filial.");
      throw err;
    } finally {
      setLoading(false);
    }
  }

  return {
    branches,
    totalPages,
    loading,
    error,
    fetchBranches,
    createBranch,
    updateBranch,
    deleteBranch,
  };
}
