import {
  ChartPie,
  // File,
  Home,
  Users2,
  Building2,
  Factory,
  PersonStanding,
} from "lucide-react";
import { Button } from "../ui/button";
import { SheetContent } from "../ui/sheet";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";

export function LateralMenu({ onClose }: { onClose: () => void }) {
  const { user } = useUser();
  const getIdUser = user?.idUser;

  return (
    <SheetContent
      className="h-full overflow-auto dark:bg-white"
      side="left"
      onInteractOutside={onClose} // Fecha se clicar fora
      onEscapeKeyDown={onClose} // Fecha se pressionar "Esc"
    >
      <Link to={`/sistema/select-client/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="w-full justify-start bg-zinc-100 px-4 py-2">
          <Home className="text-realizaBlue size-4" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Início</span>
        </Button>
      </Link>

      <h3 className="pl-4 pt-6 text-xs text-zinc-800">Prestadores de serviço</h3>
      <Link to={`/sistema/serviceProviders/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <Users2 className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Ver prestadores</span>
        </Button>
      </Link>

      <Link to={`/sistema/dashboard/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-1 w-full justify-start px-4 py-2">
          <ChartPie className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Ver BI’s</span>
        </Button>
      </Link>

      {/* <h3 className="pl-4 pt-6 text-xs text-zinc-800">Colaboradores e contratos</h3>
      <Link to={`/sistema/contracts/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-1 w-full justify-start px-4 py-2">
          <File className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Meus contratos</span>
        </Button>
      </Link> */}

      <h3 className="pl-4 pt-6 text-xs text-zinc-800">Sobre a empresa</h3>
      <Link to={`/sistema/profile/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <Building2 className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Empresa</span>
        </Button>
      </Link>

      <Link to={`/sistema/branch/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <Factory className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Filiais</span>
        </Button>
      </Link>

      <Link to={`/sistema/employees/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <PersonStanding className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Usuários</span>
        </Button>
      </Link>
    </SheetContent>
  );
}
