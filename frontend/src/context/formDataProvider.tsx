import React, { createContext, useContext, useState } from "react";

interface EnterpriseFormData {
  fantasyName: string;
  socialReason: string;
  email: string;
  phone: string;
  company: string;
  role?: string;
  idCompany: string;
  idBranch?: string
}

interface UserFormData {
  name: string;
  surname: string;
  phone: string;
  cpf: string;
  email: string;
  position: string;
}

interface FormDataContextType {
  enterpriseData: EnterpriseFormData | null;
  setEnterpriseData: (data: EnterpriseFormData) => void;
  userData: UserFormData | null;
  setUserData: (data: UserFormData) => void;
}

const FormDataContext = createContext<FormDataContextType | undefined>(
  undefined,
);

export const FormDataProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [enterpriseData, setEnterpriseData] =
    useState<EnterpriseFormData | null>(null);
  const [userData, setUserData] = useState<UserFormData | null>(null);
  return (
    <FormDataContext.Provider
      value={{ enterpriseData, setEnterpriseData, userData, setUserData }}
    >
      {children}
    </FormDataContext.Provider>
  );
};

export const useFormDataContext = () => {
  const context = useContext(FormDataContext);
  if (!context) {
    throw new Error(
      "useFormDataContext must be used within a FormDataProvider",
    );
  }
  return context;
};
