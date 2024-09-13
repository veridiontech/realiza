import { useState } from 'react'

import menExample from '../../assets/menExample.svg'

const CardCollaborator = () => {
  const [active, setActive] = useState('ativo')

  const toggleActive = () => {
    setActive(active === 'ativo' ? 'desativado' : 'ativo')
  }

  return (
    <>
      <div className="flex flex-col">
        <header
          onClick={toggleActive}
          className={`flex h-[25px] w-[220px] rounded-t-xl px-2 ${
            active === 'ativo' ? 'bg-sky-700' : 'bg-red-500'
          } cursor-pointer`}
        >
          <div className="flex flex-row items-center gap-2">
            <div
              className={`h-[10px] w-[10px] rounded-full ${
                active === 'ativo' ? 'bg-white' : 'bg-gray-400'
              }`}
            ></div>
            <span>{active}</span>
          </div>
        </header>
        <div
          className={`flex h-[241px] w-[220px] items-center rounded-b-xl border border-sky-700 justify-center${
            active === 'ativo'
              ? 'border border-sky-700'
              : 'border border-red-500'
          }`}
        >
          <div className="flex items-center gap-2">
            <img src={menExample} alt="menExemple"></img>
            <span>Raphael da silva santos</span>
          </div>
        </div>
      </div>
    </>
  )
}

export default CardCollaborator
