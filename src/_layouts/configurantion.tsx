import { Outlet } from 'react-router-dom'
import { Header } from '@/components/header/header'
import { LateralMenu } from '@/components/asideConfiguration'


export function ConfigurationLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-white">
      <Header />
      <div className='flex bg-gray-200'>
          <div className='flex h-46 w-1/5 bg-white mx-12 my-10 '>
            <LateralMenu />
          </div>
          <div className="flex h-screen w-2/3 mx-12 my-10 bg-white">
            <Outlet />
          </div>
      </div>
    </div>
  )
}
