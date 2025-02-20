export interface propsUser {
  idUser: string;
  branch: string;
  branches: string[];
  cnpj: string;
  nameEnterprise: string;
  fantasyName: string;
  socialReason: string;
  email: string;
  phone: string;
  firstName: string;
  surname: string;
  profilePicture: string;
  cpf: string;
  position: string;
  role: string;
  password: string;
  company: string;
  idCompany: string;
  telephone: string;
  description: string;
  profilePictureData: string;
  supplier: string;
  idClient: string;
}

export interface propsClient {
  cnpj: string;
  corporateName: string;
  companyName: string;
  idCompany: string;
  email: string;
  idClient: string;
  telephone: string;
  tradeName: string;
}

export interface propsDocument {
  doesBlock: boolean;
  idDocument: string;
  idDocumentSubgroup: string;
  name: string;
  type: string;
}

export interface propsBranch {
  idBranch: string;
  name: string;
}
