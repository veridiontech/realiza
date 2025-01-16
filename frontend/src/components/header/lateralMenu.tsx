import {
  ChartPie,
  CheckSquare,
  File,
  FileSearch,
  FileStack,
  FileX2,
  Flag,
  Home,
  Info,
  Layers,
  MessageSquare,
  Users2,
  UserSquare,
  Building2,
  Factory,
  PersonStanding,
} from "lucide-react";

import { Button } from "../ui/button";
import { SheetContent } from "../ui/sheet";
import { Link } from "react-router-dom";

export function LateralMenu() {
  return (
    <SheetContent className="h-full overflow-auto dark:bg-white" side={"left"}>
      <Button
        variant={"ghost"}
        className="w-full justify-start bg-zinc-100 px-4 py-2"
      >
        <Home className="size-4 text-blue-600" />
        <span className="ml-2 text-sm font-medium text-zinc-900">Início</span>
      </Button>
      <h3 className="pl-4 pt-6 text-xs text-zinc-800">
        Prestadores de serviço
      </h3>
      <Link to={"/serviceProviders"}>
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
      <Link to={"/dashboard"}>
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
      <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <Layers className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Ver filiais
        </span>
      </Button>
      <h3 className="pl-4 pt-6 text-xs text-zinc-800">
        Colaboradores e contratos
      </h3>
      <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <UserSquare className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Meus colaboradores
        </span>
      </Button>
      <Link to={"/contracts"}>
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
      <Link to={"/enterprise"}>
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
      <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <Factory className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">Filiais</span>
      </Button>
      <Link to={"/employees"}>
        <Button
          variant={"ghost"}
          className="mt-2 w-full justify-start px-4 py-2"
        >
          <PersonStanding className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">
            Funcionários
          </span>
        </Button>
      </Link>
      <Button variant={"ghost"} className="mt-2 w-full justify-start px-4 py-2">
        <MessageSquare className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">
          Mensagens
        </span>
      </Button>
      <Button variant={"ghost"} className="mt-1 w-full justify-start px-4 py-2">
        <Info className="size-4 text-zinc-800" />
        <span className="ml-2 text-sm font-medium text-zinc-900">Suporte</span>
      </Button>
      <h3 className="pl-4 pt-6 text-xs text-zinc-800">Documentos</h3>
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
      </Button>
    </SheetContent>
  );
}
