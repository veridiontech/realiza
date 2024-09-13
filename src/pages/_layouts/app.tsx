import { Outlet } from 'react-router-dom'

import { Header } from '@/components/header/header'

export function AppLayout() {
  return (
    <div>
      <Header />
      <div>
        <Outlet />
      </div>
    </div>
  )
}
