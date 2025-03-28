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
  documentId?: string;
  idDocument: string;
  name: string;
  doesBlock?: boolean;
  idDocumentSubgroup?: string;
  typets?: string;
  idDocumentMatrix?: string;
}

export interface propsBranch {
  idBranch: string
  name: string;
  email: string;
  cnpj: string;
  address: string;
  telephone: string;
  cep: string;
  state: string;
  row: string;
  actions: string
}

export interface propsSupplier {
  idProvider: string;
  cnpj: string;
  tradeName: string;
  corporateName: string;
  logoId: string;
  logoData: string;
  email: string;
  cep: string;
  state: string;
  city: string;
  address: string;
  number: string;
  supplier: string;
  client: string;
}
