import { ChevronRight, Pencil } from "lucide-react";
import { Link } from "react-router-dom";
import { Skeleton } from "@/components/ui/skeleton"
import { Button } from "../ui/button";
import { useClient } from "@/context/Client-Provider";
import { ClientProfilePhoto } from "../profile-photos/client-profile-photo";

export function EnterpriseResume() {
  const { client } = useClient();

  return (
    <Link to={`/sistema/profile/${client?.idClient}`}>
      <div className="dark:bg-primary flex w-full flex-col justify-between rounded bg-white p-4 shadow md:flex-row">
        <div className="flex gap-3 items-center">
          <ClientProfilePhoto />
          <div className="flex flex-col gap-1">
            {client ? (
              <h3 className="text-lg font-medium">{client.tradeName}</h3>
            ) : (
              <h3 className="text-lg font-medium">Cliente não selecionado</h3>
            )}
            <div className="flex items-center gap-2">
              <strong className="font-medium md:font-md">Empresa:</strong> 
              {client ? (<h3 >{client.corporateName}</h3>):(<Skeleton className="w-[100px] h-[8px] bg-gray-200 rounded-full" />)}
            </div>
            <div className="flex items-center gap-2">
              <strong className="font-medium">CNPJ:</strong> 
              {client ? (<p>{client.cnpj}</p>):(<Skeleton className="w-[80px] h-[8px] bg-gray-200 rounded-full" />)}
            </div>
          </div>
        </div>
        <div className="flex gap-4 pt-4 md:pt-0">
          <Button
            variant={"ghost"}
            className="dark:bg-primary-foreground rounded-full bg-zinc-100 p-2 text-zinc-600"
          >
            <Pencil size={24} className="dark:text-white" />
          </Button>
          <Button
            variant={"ghost"}
            className="dark:bg-primary-foreground rounded-full bg-zinc-100 p-2 text-zinc-600"
          >
            <ChevronRight size={24} className="dark:text-white" />
          </Button>
        </div>
      </div>
    </Link>
  );
}
