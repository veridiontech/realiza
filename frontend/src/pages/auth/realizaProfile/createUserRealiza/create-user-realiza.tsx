import { FormCreateUserRealiza } from "./form-create-user-realiza";

export function CreateUserRealiza() {


  return (
    <div className="dark:bg-primary relative bottom-[10vw] flex w-[100vw] flex-col gap-8 rounded-md bg-white p-6 shadow-md md:m-20 md:w-[90vw] lg:p-10">
      <div className="flex items-center gap-1">
        <h1 className="text-xl font-bold lg:text-2xl">
          Crie um novo usuário para
        </h1>
        <select>
          <option value="">Realiza</option>
          <option value="">cliente{}</option>
          <option value=""></option>
        </select>
      </div>
      <FormCreateUserRealiza />
    </div>
  );
}
