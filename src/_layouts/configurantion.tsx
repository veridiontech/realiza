import { Outlet } from 'react-router-dom'
import { Header } from '@/components/header/header'
import { LateralMenu } from '@/components/asideConfiguration'


export function ConfigurationLayout() {
  return (
    <div className="flex min-h-screen flex-col bg-white">
      <Header />
      <div className='bg-gray-200'>
          <div className='flex h-46 w-1/5 bg-white mx-20 my-10 align-center'>
            <LateralMenu />
          </div>
          <div className="flex bg-white">
            <Outlet />
          </div>
      </div>
    </div>
  )
}
