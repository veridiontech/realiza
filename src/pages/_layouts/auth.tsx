import { Outlet } from 'react-router-dom'

export function AuthLayout() {
  return (
    <div className='flex min-h-screen'>
      <div className='w-6/12 bg-white'>
        <Outlet />
      </div>
      <div className='w-6/12 bg-black'>

      </div>
    </div>
  )
}
