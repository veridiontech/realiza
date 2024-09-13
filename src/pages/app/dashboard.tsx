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

import { EnterpriseResume } from '@/components/home/enterpriseResume'
import { GraphicHomeLeft } from '@/components/home/graphicHomeLeft'
import { GraphicHomeRight } from '@/components/home/graphicHomeRight'
import { Button } from '@/components/ui/button'

export function Dashboard() {
  return (
    <>
      <Helmet title="Dashboard" />
      <section className="bg-zinc-100 pb-10 pt-14">
        <div className="container mx-auto max-w-7xl">
          <EnterpriseResume />
          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
            <GraphicHomeLeft />
            <GraphicHomeRight />
          </div>
          <div className="mt-5 w-full text-right">
            <Button className="bg-blue-600 hover:bg-blue-500">
              Ver mais <ChevronRight />
            </Button>
          </div>
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm">Fornecedores</h3>
                  <span className="text-2xl">324</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <UsersRound size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm">Mensagens</h3>
                  <span className="text-2xl">12</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <MessageCircle size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm">Unidades</h3>
                  <span className="text-2xl">4</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <University size={28} />
                </div>
              </Button>
              <Button className="group h-full w-full max-w-none items-center justify-between border-zinc-200 bg-white p-5 hover:bg-blue-500">
                <div className="flex flex-col items-start text-zinc-900 group-hover:text-white">
                  <h3 className="text-sm">Contratos</h3>
                  <span className="text-2xl">72</span>
                </div>
                <div className="flex size-11 items-center justify-center rounded bg-blue-600/40 text-blue-600 group-hover:text-white">
                  <Files size={28} />
                </div>
              </Button>
            </div>
          </div>
          <div className="mt-8 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Adicionar fornecedor
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Enviar documento
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Criar contato
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Gerar relatório
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Atualizar documentos
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Consultar contratos
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Aprovar solicitações
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
            <Button className="flex h-full items-center justify-between bg-white px-4 py-3 text-zinc-900 hover:bg-blue-600/25">
              Editar colaboradores
              <div className="flex size-8 items-center justify-center rounded bg-blue-600 text-white">
                <ChevronRight />
              </div>
            </Button>
          </div>
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 md:grid-cols-4">
              <div className="w-full rounded bg-white p-4">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <LineChartIcon />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600">
                  Solicitações de homologação
                </h3>
                <p className="pt-1 text-3xl font-medium">28</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingUp className="size-4 text-green-500" />
                  <p>Aumento em 10% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <Briefcase />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600">
                  Contratos em andamento
                </h3>
                <p className="pt-1 text-3xl font-medium">95</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingDown className="size-4 text-red-500" />
                  <p>Diminuição em 15% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <Clock />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600">
                  Documentos pendentes
                </h3>
                <p className="pt-1 text-3xl font-medium">1022</p>
                <div className="flex items-center gap-1 pt-1 text-[10px]">
                  <TrendingUp className="size-4 text-green-500" />
                  <p>Aumento em 12% comparado ao último mês</p>
                </div>
              </div>
              <div className="w-full rounded bg-white p-4">
                <div className="flex size-10 items-center justify-center rounded bg-blue-600/40 text-blue-600">
                  <User />
                </div>
                <h3 className="pt-4 text-sm text-zinc-600">
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
