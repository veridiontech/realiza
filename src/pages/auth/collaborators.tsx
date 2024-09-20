import { Search } from 'lucide-react'
import { Helmet } from 'react-helmet-async'

import CardCollaborator from '@/components/cardCollaborator/cardCollaborator'
import { Button } from '@/components/ui/button'

import collaboratorsIcon from '../../assets/collaborators.svg'
import plusRed from '../../assets/plusRed.svg'
import { Link } from 'react-router-dom'

export function Collaborators() {
  return (
    <>
      <Helmet title="collaborators" />
      <section className="mx-20">
        <div className="flex h-[244px] w-full flex-row justify-between p-[48px] shadow-custom-blue">
          <div className="flex flex-col gap-[60px]">
            <div className="flex flex-row">
              <h1>Meus colaboradores -</h1>
              <div className="flex flex-row gap-2">
                <img src={collaboratorsIcon} alt="Collaborators Icon" />
                <span>20</span>
              </div>
            </div>

            <div className="flex w-[460px] items-center gap-3 rounded-lg border border-sky-800 bg-zinc-100 px-4 py-2">
              <Search className="size-5 text-zinc-900" />
              <input
                className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
                placeholder="Pesquisar unidades, ações etc..."
              />
            </div>
          </div>
          <div className="flex flex-col gap-[50px]">
            <div className="flex flex-row gap-2">
              <img src={plusRed} alt="Add Contact Icon" />
              <span className="font-medium text-red-700">
                Adicionar contato
              </span>
            </div>
            <Link to="/monitoring">
              <Button variant="realiza">Ver tabela de monitoramento</Button>
            </Link>
          </div>
        </div>
        {/* When clicking to deactivate, update the database and deactivate the collaborator */}
        <div className="mt-[26px] flex w-full flex-col items-center justify-center gap-[80px] px-[53px] py-[76px] shadow-custom-blue">
          <div className="flex gap-[80px]">
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
          </div>
          <div className="flex gap-[80px]">
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
          </div>
          <div className="flex gap-[80px]">
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
            <CardCollaborator />
          </div>
        </div>
      </section>
    </>
  )
}
