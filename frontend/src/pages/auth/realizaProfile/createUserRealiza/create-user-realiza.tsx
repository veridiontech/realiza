import { useState } from "react";
import { FormCreateUserRealiza } from "./form-create-user-realiza";
import { FormCreateUserSupplier } from "./form-create-supplier";
import { FormCreateUserClient } from "./form-create-client";

export function CreateUserRealiza() {
  const [selectForm, setSelectForm] = useState("REALIZA");

  return (
    <div className="dark:bg-primary relative bottom-[10vw] flex w-[100vw] flex-col gap-8 rounded-md bg-white p-6 shadow-md md:m-20 md:w-[90vw] lg:p-10">
      <div className="flex items-center gap-1">
        <h1 className="text-xl font-bold lg:text-2xl">Crie um novo usuário para</h1>
        <select
          className="ml-2 rounded border px-2 py-1"
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
