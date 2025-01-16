import {
  Briefcase,
  ChevronRight,
  Clock,
  Files,
  LineChartIcon,
  MessageCircle,
  TrendingDown,
  TrendingUp,
  University,
  User,
  UsersRound,
} from 'lucide-react'
import { Helmet } from 'react-helmet-async'
import { NavLink } from 'react-router-dom'

import { EnterpriseResume } from '@/components/home/enterpriseResume'
import { GraphicHomeLeft } from '@/components/home/graphicHomeLeft'
import { GraphicHomeRight } from '@/components/home/graphicHomeRight'
import { Button } from '@/components/ui/button'

export function Dashboard() {
  return (
    <>
      <Helmet title="Dashboard" />
      <section className=" pb-10 pt-14">
        <div className="container mx-auto max-w-7xl">
          <EnterpriseResume />
          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
            <GraphicHomeLeft />
            <GraphicHomeRight />
          </div>
          <div className="mt-5 w-full text-right">
            <NavLink to="/BIs">
              <Button className="bg-blue-600 dark:bg-primary dark:text-white hover:bg-blue-500">
                Ver mais <ChevronRight />
              </Button>
            </NavLink>
          </div>
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500 dark:bg-primary ">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm dark:text-white">Fornecedores</h3>
                  <span className="text-2xl dark:text-white">324</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <UsersRound size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500 dark:bg-primary">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm dark:text-white">Mensagens</h3>
                  <span className="text-2xl dark:text-white">12</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <MessageCircle size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500 dark:bg-primary">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm dark:text-white">Unidades</h3>
                  <span className="text-2xl dark:text-white">4</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <University size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500 dark:bg-primary">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm dark:text-white">Contratos</h3>
                  <span className="text-2xl dark:text-white">72</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <Files size={28} />
                </div>
              </Button>
            </div>
          </div>
          <div className="mt-8 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Adicionar fornecedor
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Enviar documento
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Criar contato
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Gerar relatório
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Atualizar documentos
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Consultar contratos
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Aprovar solicitações
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25 dark:bg-primary dark:text-white">
              Editar colaboradores
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
          </div>
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 md:grid-cols-4">
              <div className="w-full rounded bg-white p-4 dark:bg-primary">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <LineChartIcon />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600 dark:text-white">
                  Solicitações de homologação
                </h3>
                <p className="pt-1 text-3xl font-medium">28</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingUp className="size-4 text-green-500" />
                  <p>Aumento em 10% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4 dark:bg-primary">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <Briefcase />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600 dark:text-white">
                  Contratos em andamento
                </h3>
                <p className="pt-1 text-3xl font-medium">95</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingDown className="size-4 text-red-500" />
                  <p>Diminuição em 15% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4 dark:bg-primary">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <Clock />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600 dark:text-white">
                  Documentos pendentes
                </h3>
                <p className="pt-1 text-3xl font-medium">1022</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingUp className="size-4 text-green-500" />
                  <p>Aumento em 12% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4 dark:bg-primary">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <User />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600 dark:text-white">
                  Número de fornecedores
                </h3>
                <p className="pt-1 text-3xl font-medium">22</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingUp className="size-4 text-green-500" />
                  <p>Aumento em 16% comparado ao último mês</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}
