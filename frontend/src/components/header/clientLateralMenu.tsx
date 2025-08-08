import {
  ChartPie,
  // File,
  Home,
  Users2,
  Building2,
  Factory,
  PersonStanding,
  Paperclip,
} from "lucide-react";
import { SheetContent, SheetTitle } from "../ui/sheet";
import { useUser } from "@/context/user-provider";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { ClientMenuItem } from "./clientMenuItem";
import { useState } from "react";

export function ClientLateralMenu({ onClose }: { onClose: () => void }) {
  const { user } = useUser();
  const getIdUser = user?.idUser;
  const [, setActiveMenuKey] = useState<string | null>(null);

  return (
    <SheetContent
      className="h-full overflow-auto dark:bg-white bg-right bg-cover"
      side="left"
      onInteractOutside={onClose}
      onEscapeKeyDown={onClose}
      style={{ backgroundImage: `url(${bgModalRealiza})` }}
    >
      <SheetTitle className="sr-only">Menu Cliente</SheetTitle>

      <ClientMenuItem
        to={`/cliente/home/${getIdUser}`}
        icon={<Home />}
        label="Início"
        menuKey="home"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">
        Prestadores de serviço
      </h3>
      <ClientMenuItem
        to={`/cliente/serviceProviders/${getIdUser}`}
        icon={<Users2 />}
        label="Ver contratos"
        menuKey="ver-contratos"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <ClientMenuItem
        to={`/cliente/dashboard-details/${getIdUser}`}
        icon={<ChartPie />}
        label="Ver BI’s"
        menuKey="ver-bis"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">
        Gestão de fornecedores
      </h3>
      <ClientMenuItem
        to={`/cliente/table-clientproviders/${getIdUser}`}
        icon={<Users2 />}
        label="Ver Fornecedores"
        menuKey="ver-fornecedores"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">Sobre a empresa</h3>
      <ClientMenuItem
        to={`/cliente/profile/${getIdUser}`}
        icon={<Building2/>}
        label="Empresa"
        menuKey="empresa"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <ClientMenuItem
        to={`/cliente/branch/${getIdUser}`}
        icon={<Factory />}
        label="Filiais"
        menuKey="filiais"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <ClientMenuItem
        to={`/cliente/employees/${getIdUser}`}
        icon={<PersonStanding />}
        label="Colaboradores"
        menuKey="colaboradores"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">Funcionalidades</h3>
      <ClientMenuItem
        to={`/cliente/documents/${getIdUser}`}
        icon={<Paperclip />}
        label="Painel do cliente"
        menuKey="painel-cliente"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
    </SheetContent>
  );
}
