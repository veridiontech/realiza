import { ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "../ui/button";
import { useClient } from "@/context/Client-Provider";
import { ClientProfilePhoto } from "../profile-photos/client-profile-photo";
import { ModalCreateCliente } from "../modal-create-client";
import { useBranch } from "@/context/Branch-provider";

export function EnterpriseResume() {
  const { client } = useClient();
  const { selectedBranch } = useBranch();
  console.log("CLIENTE:", client);
  console.log("FILIAL:", selectedBranch);
  

  return (
    <div className="dark:bg-primary flex w-full items-start justify-between rounded bg-white p-4 shadow md:flex-row">
      <div className="flex items-center gap-3">
        <ClientProfilePhoto />
        <div className="flex flex-col gap-1">
          {client ? (
            <h3 className="text-lg font-medium">{client.corporateName}</h3>
          ) : (
            <h3 className="text-lg font-medium">Cliente não selecionado</h3>
          )}
          <div className="flex items-center gap-2">
            <strong className="md:font-md font-medium">Empresa:</strong>
            {client ? (
              <h3>{client.corporateName}</h3>
            ) : (
              <Skeleton className="h-[8px] w-[100px] rounded-full bg-gray-200" />
            )}
          </div>
          <div className="flex items-center gap-2">
            <strong className="md:font-md font-medium">Filial:</strong>
            {selectedBranch ? (
              <h3>{selectedBranch.name}</h3>
            ) : (
              <Skeleton className="h-[8px] w-[100px] rounded-full bg-gray-200" />
            )}
          </div>
          <div className="flex items-center gap-2">
            <strong className="font-medium">CNPJ:</strong>
            {client ? (
              <p>{client.cnpj}</p>
            ) : (
              <Skeleton className="h-[8px] w-[80px] rounded-full bg-gray-200" />
            )}
          </div>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <ModalCreateCliente />
        <Link to={`/sistema/profile/${client?.idClient}`}>
          <Button
            variant={"ghost"}
            className="dark:bg-primary-foreground rounded-full bg-neutral-200 w-[2vw] p-2 text-zinc-600 hover:bg-neutral-300"
          >
            <ChevronRight size={24} className="dark:text-white" />
          </Button>
        </Link>
      </div>
    </div>
  );
}
