import { ChevronRight, Pencil } from 'lucide-react'
import { Link } from 'react-router-dom'

import { Button } from '../ui/button'

export function EnterpriseResume() {
  return (
    <Link to="/profile">
      <div className="flex w-full flex-col justify-between rounded bg-white dark:bg-primary p-4 shadow md:flex-row">
        <div className="flex gap-3">
          <div className="flex aspect-square size-24 items-center justify-center rounded bg-red-600 uppercase text-white">
            LOGO
          </div>
          <div className="flex flex-col gap-1">
            <h3 className="text-lg font-medium">DISTRIBUIDORA DE GAS LTDA</h3>
            <p>
              <strong className="font-medium">Empresa:</strong> Cia
              Distribuidora
            </p>
            <p>
              <strong className="font-medium">CEP:</strong> 00001-000
            </p>
          </div>
        </div>
        <div className="flex gap-4 pt-4 md:pt-0">
          <Button
            variant={'ghost'}
            className="rounded-full bg-zinc-100 p-2 dark:bg-muted-foreground text-zinc-600"
          >
            <Pencil size={24} className='dark:text-white'/>
          </Button>
          <Button
            variant={'ghost'}
            className="rounded-full bg-zinc-100 dark:bg-muted-foreground p-2 text-zinc-600"
          >
            <ChevronRight size={24} className='dark:text-white'/>
          </Button>
        </div>
      </div>
    </Link>
  )
}
