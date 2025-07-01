import { useState } from "react";
import { FormCreateUserRealiza } from "./form-create-user-realiza";
import { FormCreateUserSupplier } from "./form-create-supplier";
import { FormCreateUserClient } from "./form-create-client";
import { User } from "lucide-react";

export function CreateUserRealiza() {
  const [selectForm, setSelectForm] = useState("REALIZA");

  return (
    <div className="dark:bg-primary relative bottom-[10vw] flex w-full flex-col gap-8 rounded-md bg-white p-6 shadow-md md:m-20 md:w-[90vw] lg:p-10">
      <div className="flex w-full flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-2 text-realizaBlue">
          <User size={20} />
          <h1 className="text-lg font-semibold md:text-xl">Crie um novo usuário</h1>
        </div>

        <select
          className="w-full max-w-xs rounded-md border border-gray-300 bg-gray-100 px-3 py-2 text-sm text-gray-700 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 md:w-[300px]"
          onChange={(e) => setSelectForm(e.target.value)}
          value={selectForm}
        >
          <option value="REALIZA">Realiza</option>
          <option value="CLIENT">Cliente</option>
          <option value="SUPPLIER">Fornecedor</option>
        </select>
      </div>


      {selectForm === "REALIZA" && <FormCreateUserRealiza />}
      {selectForm === "CLIENT" && <FormCreateUserClient />}
      {selectForm === "SUPPLIER" && <FormCreateUserSupplier />}
    </div>
  );
}
