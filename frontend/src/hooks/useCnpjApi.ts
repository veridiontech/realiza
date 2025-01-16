export async function fetchCompanyByCNPJ(cnpj: string) {
  const sanitizedCnpj = cnpj.replace(/\D/g, "");

  const response = await fetch(
    `https://brasilapi.com.br/api/cnpj/v1/${sanitizedCnpj}`,
  );
  if (!response.ok) {
    throw new Error("Erro ao consultar CNPJ. Verifique o número inserido.");
  }

  const data = await response.json();
  return {
    razaoSocial: data.razao_social,
    nomeFantasia: data.nome_fantasia,
    email: data.email,
    telefone: data.telefone,
  };
}
