import { Outlet } from 'react-router-dom'

import { Header } from '@/components/header/header'

export function AppLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="flex-grow bg-muted-foreground">
        <Outlet />
      </div>
    </div>
  )
}
