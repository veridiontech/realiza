import { createBrowserRouter } from 'react-router-dom'

import { AppLayout } from './pages/_layouts/app'
import { AuthLayout } from './pages/_layouts/auth'
import { Dashboard } from './pages/app/dashboard'
import MonitoringTable from './pages/auth/bis'
import { Collaborators } from './pages/auth/collaborators'
import { ProfileUser } from './pages/auth/profileUser'
import { SignIn } from './pages/auth/sign-in'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/', element: <Dashboard /> }],
  },
  {
    path: '/',
    element: <AuthLayout />,
    children: [{ path: '/sign-in', element: <SignIn /> }],
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/profile', element: <ProfileUser /> }],
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/collaborators', element: <Collaborators /> }],
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/Bis', element: <MonitoringTable /> }],
  },
  // {
  // { path: '/', element: <Dashboard /> },
  // { path: '/sign-in', element: <SignIn /> },
])
