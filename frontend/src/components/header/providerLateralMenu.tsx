import {
  ChartPie,
  File,
  Home,
  Users2,
  Building2,
  PersonStanding,
} from "lucide-react";

import { Button } from "../ui/button";
import { SheetContent } from "../ui/sheet";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import bgModalRealiza from "@/assets/modalBG.jpeg";

export function ProviderLateralMenu({ onClose }: { onClose: () => void }) {
  const { user } = useUser();
  const getIdUser = user?.idUser;

  if (user?.role === "ROLE_SUBCONTRACTOR_RESPONSIBLE") {
    return (
      <SheetContent
        className="h-full overflow-auto"
        // onMouseLeave={onClose}
        onInteractOutside={onClose}
        onEscapeKeyDown={onClose}
        side={"left"}
        style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <Link to={`/fornecedor/home/${getIdUser}`}>
          <Button
            variant={"ghost"}
            className="w-full justify-start bg-zinc-100 px-4 py-2"
          >
            <Home className="text-realizaBlue size-4" />
            <span className="ml-2 text-sm font-medium text-zinc-900">Contratos</span>
          </Button>
        </Link>
        <h3 className="pl-4 pt-6 text-xs text-zinc-800">
          Prestadores de serviço
        </h3>
        <Link to={`contracts/${user?.idUser}`}>
          <Button
            variant={"ghost"}
            className="mt-2 w-full justify-start px-4 py-2"
          >
            <Users2 className="size-4 text-zinc-800" />
            <span className="ml-2 text-sm font-medium text-zinc-900">
              Ver prestadores
            </span>
          </Button>
        </Link>
        <Link to={`/fornecedor/dashboard/${getIdUser}`}>
          <Button
            variant={"ghost"}
            className="mt-1 w-full justify-start px-4 py-2"
          >
            <ChartPie className="size-4 text-zinc-800" />
            <span className="ml-2 text-sm font-medium text-zinc-900">
              Ver BI’s
            </span>
          </Button>
        </Link>
        <h3 className="pl-4 pt-6 text-xs text-zinc-800">
          Colaboradores e contratos
        </h3>
        <Link to={`/fornecedor/home/${user?.idUser}`}>
          <Button
            variant={"ghost"}
            className="mt-1 w-full justify-start px-4 py-2"
          >
            <File className="size-4 text-zinc-800" />
            <span className="ml-2 text-sm font-medium text-zinc-900">
              Meus contratos
            </span>
          </Button>
        </Link>
        <h3 className="pl-4 pt-6 text-xs text-zinc-800">Sobre a empresa</h3>
        <Link to={`/fornecedor/profile/${getIdUser}`}>
          <Button
            variant={"ghost"}
            className="mt-2 w-full justify-start px-4 py-2"
          >
            <Building2 className="size-4 text-zinc-800" />
            <span className="ml-2 text-sm font-medium text-zinc-900">
              Empresa
            </span>
          </Button>
        </Link>

        <Link to={`/fornecedor/employees/${getIdUser}`}>
          <Button
            variant={"ghost"}
            className="mt-2 w-full justify-start px-4 py-2"
          >
            <PersonStanding className="size-4 text-zinc-800" />
            <span className="ml-2 text-sm font-medium text-zinc-900">
              Colaboradores
            </span>
          </Button>
        </Link>
      </SheetContent>
    )
  }

  return (
    <SheetContent
      className="h-full overflow-auto dark:bg-white bg-right bg-cover"
      side={"left"}
      // onMouseLeave={onClose}
      onInteractOutside={onClose}
      onEscapeKeyDown={onClose}
      style={{ backgroundImage: `url(${bgModalRealiza})` }}>
      <Link to={`/fornecedor/home/${getIdUser}`}>
        <Button
          variant={"ghost"}
          className="w-full justify-start hover:bg-neutral-500 px-4 py-2"
        >
          <Home className="size-4 text-white" />
          <span className="ml-2 text-sm font-medium text-white ">Contratos</span>
        </Button>
      </Link>
      <h3 className="pl-4 pt-6 text-xs text-white">
        Prestadores de serviço
      </h3>
      <Link to={`/fornecedor/quartered/${getIdUser}`}>
        <Button
          variant={"ghost"}
          className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500"
        >
          <Users2 className="size-4 text-white" />
          <span className="ml-2 text-sm font-medium text-white">
            Meus Subcontratados
          </span>
        </Button>
      </Link>
      <Link to={`/fornecedor/employees/${getIdUser}`}>
        <Button
          variant={"ghost"}
          className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500"
        >
          <PersonStanding className="size-4 text-white" />
          <span className="ml-2 text-sm font-medium text-white">
            Colaboradores
          </span>
        </Button>
      </Link> 
      {/* <h3 className="pl-4 pt-6 text-xs text-white">Sobre a empresa</h3>
      <Link to={`/fornecedor/profile/${getIdUser}`}>
        <Button
          variant={"ghost"}
          className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500"
        >
          <Building2 className="size-4 text-white" />
          <span className="ml-2 text-sm font-medium text-white">
            Empresa
          </span>
        </Button>
      </Link> */}
      {/* <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <MessageSquare className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Mensagens
        </span>
      </Button> */}
      {/* <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <Info className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">Suporte</span>
      </Button> */}
      {/* <h3 className="pl-4 pt-6 text-xs text-zinc-800">Documentos</h3>
      <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <FileStack className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Gerenciar documentos
        </span>
      </Button>
      <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <FileSearch className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Documentos pendentes
        </span>
      </Button>
      <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <FileX2 className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Documento vencidos
        </span>
      </Button>
      <h3 className="pl-4 pt-6 text-xs text-zinc-800">Relatórios</h3>
      <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <Flag className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Relatórios gerais
        </span>
      </Button>
      <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <CheckSquare className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Relatório de conformidade
        </span>
      </Button> */}
    </SheetContent>
  );
}
