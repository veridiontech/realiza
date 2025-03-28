import React, { createContext, useContext, useState } from "react";


interface DataSendEmailProvider {
  datasSender: any;
  setDatasSender: (data: any) => void;  
}

const DataSendEmailContext = createContext<DataSendEmailProvider | undefined>(undefined);

export const DataSendEmailProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [datasSender, setDatasSender] = useState(null);

  return (
    <DataSendEmailContext.Provider value={{ datasSender, setDatasSender }}>
      {children}
    </DataSendEmailContext.Provider>
  );
};

export const useDataSendEmailContext = () => {
  const context = useContext(DataSendEmailContext);
  if (!context) {
    throw new Error("useDataSendEmailContext must be used within a DataSendEmailProvider");
  }
  return context;
};
