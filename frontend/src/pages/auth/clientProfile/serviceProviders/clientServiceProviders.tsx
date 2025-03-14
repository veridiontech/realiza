// import { Pagination } from "@/components/ui/pagination";
import { ModalTesteSendSupplier } from "@/components/client-add-supplier";
// import { useClient } from "@/context/Client-Provider";
import { TableClientServiceProvider } from "./tableClientProviders";

export function ClientServiceProvider() {
  // const { client } = useClient();

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8">Prestadores de Serviço</h1>

        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar fornecedor..."
              className="focus:outline-realizaBlue w-[34rem] rounded-lg border border-gray-300 p-2"
              onChange={() => {}}
            />
          </div>
          <ModalTesteSendSupplier />
        </div>

        <TableClientServiceProvider />
      </div>
    </div>
  );
}
