import axios from 'axios';

export async function fetchCompanyByCNPJ(cnpj: string) {
  const sanitizedCnpj = cnpj.replace(/\D/g, ""); // Remove qualquer caractere não numérico

  try {
    const response = await axios.get(`https://open.cnpja.com/office/${sanitizedCnpj}`);

    const data = response.data;

    return {
      razaoSocial: data.company.name,
      nomeFantasia: data.company.name,
      cep: String(data.address.zip), // Certifique-se de que é uma string
      state: data.address.state,
      email: data.emails[0].address,
      city: data.address.city,
      address: data.address.street,
      number: data.address.number,
      telefone: data.phones[0].number, // Caso esteja disponível
    };
  } catch (error) {
    throw new Error("Erro ao consultar CNPJ. Verifique o número inserido.");
  }
}
