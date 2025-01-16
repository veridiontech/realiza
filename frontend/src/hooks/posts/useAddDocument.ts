import { useMutation } from "@tanstack/react-query";
import axios from "axios";

export interface AddDocumentData {
  documentType: string;
  description?: string;
}

export function useAddDocument() {
  return useMutation({
    mutationFn: async (data: AddDocumentData) => {
      const response = await axios.post(
        "http://localhost:3001/Documents",
        data,
      );
      return response.data;
    },
    onError: (error) => {
      console.error("Erro ao adicionar documento:", error);
    },
    onSuccess: (data) => {
      console.log("Documento adicionado com sucesso:", data);
    },
  });
}
