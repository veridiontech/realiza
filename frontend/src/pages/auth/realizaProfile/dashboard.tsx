import {
  Briefcase,
  ChevronRight,
  Clock,
  Files,
  LineChartIcon,
  MessageCircle,
  University,
  User,
  UsersRound,
} from "lucide-react";
import { Helmet } from "react-helmet-async";
import { NavLink } from "react-router-dom";

import { EnterpriseResume } from "@/components/home/enterpriseResume";
import { GraphicHomeLeft } from "@/components/home/graphicHomeLeft";
import { GraphicHomeRight } from "@/components/home/graphicHomeRight";
import { Button } from "@/components/ui/button";
import { MainCard } from "@/components/quickActions/mainCard";
import { ActionButton } from "@/components/quickActions/actionButton";

export function Dashboard() {
  return (
    <>
      <Helmet title="Dashboard" />
      <section className="dark:bg-primary-foreground bg-zinc-100 pb-10 pt-14">
        <div className="container mx-auto max-w-7xl">
          <EnterpriseResume />
          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
            <GraphicHomeLeft />
            <GraphicHomeRight />
          </div>
          <div className="mt-5 w-full text-right">
            <NavLink to="/BIs">
              <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
                Ver mais <ChevronRight />
              </Button>
            </NavLink>
          </div>

          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>

            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
              <MainCard
                title="Fornecedores"
                value={324}
                icon={<UsersRound size={28} />}
              />
              <MainCard
                title="Mensagens"
                value={12}
                icon={<MessageCircle size={28} />}
              />
              <MainCard
                title="Unidades"
                value={4}
                icon={<University size={28} />}
              />
              <MainCard
                title="Contratos"
                value={72}
                icon={<Files size={28} />}
              />
            </div>

            <div className="mt-8 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
              <ActionButton
                label="Adicionar fornecedor"
                icon={<ChevronRight />}
              />
              <ActionButton label="Enviar documento" icon={<ChevronRight />} />
              <ActionButton label="Criar contato" icon={<ChevronRight />} />
              <ActionButton label="Gerar relatório" icon={<ChevronRight />} />
              <ActionButton
                label="Atualizar documentos"
                icon={<ChevronRight />}
              />
              <ActionButton
                label="Consultar contratos"
                icon={<ChevronRight />}
              />
              <ActionButton
                label="Aprovar solicitações"
                icon={<ChevronRight />}
              />
              <ActionButton
                label="Editar colaboradores"
                icon={<ChevronRight />}
              />
            </div>
          </div>

          {/* OUTROS CARDS */}
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 md:grid-cols-4">
              <MainCard
                title="Solicitações de homologação"
                value={28}
                icon={<LineChartIcon />}
              />
              <MainCard
                title="Contratos em andamento"
                value={95}
                icon={<Briefcase />}
              />
              <MainCard
                title="Documentos pendentes"
                value={1022}
                icon={<Clock />}
              />
              <MainCard
                title="Número de fornecedores"
                value={22}
                icon={<User />}
              />
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
