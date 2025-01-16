import axios from "axios";

export async function fetchCompanyByCNPJ(cnpj: string) {
  const sanitizedCnpj = cnpj.replace(/\D/g, "");

  try {
    const response = await axios.get(
      `https://brasilapi.com.br/api/cnpj/v1/${sanitizedCnpj}`,
    );
    const data = response.data;

    return {
      razaoSocial: data.razao_social,
      nomeFantasia: data.nome_fantasia,
      email: data.email,
      telefone: data.telefone,
    };
  } catch (error) {
    throw new Error("Erro ao consultar CNPJ. Verifique o número inserido.");
  }
}
