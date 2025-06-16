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
  isUltragaz: boolean;
}

export interface propsDocument {
  documentId?: string;
  idDocument: string;
  title: string;
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
  client: string
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

export interface propsBoard {
  idBoard: string,
  name: string,
  idClient: string,
}

export interface propsMarket {
  idMarket: string,
  name: string,
  idBoard: string,
}

export interface propsCenter {
  idCenter: string,
  name: string,
  idMarkete: string,
}

export interface propsBranchUltra {
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

export interface propsActivities {
  idActivity: string,
  title: string,
  risk: string,
}

export interface propsCompanyData {
  updated: string;
  taxId: string;
  alias: string | null;
  founded: string;
  head: boolean;
  company: {
    members: any[]; // pode ser ajustado se souber a estrutura dos membros
    id: number;
    name: string;
    equity: number;
    nature: {
      id: number;
      text: string;
    };
    size: {
      id: number;
      acronym: string;
      text: string;
    };
    simples: {
      optant: boolean;
      since: string;
    };
    simei: {
      optant: boolean;
      since: string;
    };
  };
  statusDate: string;
  status: {
    id: number;
    text: string;
  };
  address: {
    municipality: number;
    street: string;
    number: string;
    district: string;
    city: string;
    state: string;
    details: string | null;
    zip: string;
    country: {
      id: number;
      name: string;
    };
  };
  mainActivity: {
    id: number;
    text: string;
  };
  phones: {
    type: string;
    area: string;
    number: string;
  }[];
  emails: {
    ownership: string;
    address: string;
    domain: string;
  }[];
  sideActivities: {
    id: number;
    text: string;
  }[];
  registrations: any[]; 
  suframa: any[];       
}