import { propsSupplier } from "@/types/interfaces";
// import { ip } from "@/utils/ip";
// import axios from "axios";
import React, { createContext, useContext, useState } from "react";

interface SupplierContextPros {
  supplier: propsSupplier | null;
  setSupplier: React.Dispatch<React.SetStateAction<propsSupplier | null>>;
}

const SupplierContext = createContext<SupplierContextPros | undefined>(
  undefined,
);

export function useSupplier() {
  const context = useContext(SupplierContext);
  if (!context) {
    throw new Error("SupplierProvider não está configurado corretamente");
  }
  return context;
}

export function SupplierProvider({ children }: { children: React.ReactNode }) {
  const [supplier, setSupplier] = useState<propsSupplier | null>(null);

  return (
    <SupplierContext.Provider value={{ supplier, setSupplier }}>
      {children}
    </SupplierContext.Provider>
  );
}
