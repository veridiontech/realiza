import { createBrowserRouter } from 'react-router-dom'

import { AppLayout } from './pages/_layouts/app'
import { AuthLayout } from './pages/_layouts/auth'
import { Dashboard } from './pages/app/dashboard'
import MonittoringBis from './pages/auth/bis'
import { Collaborators } from './pages/auth/collaborators'
import { MonittoringTable } from './pages/auth/monittoringTable'
import { ProfileUser } from './pages/auth/profileUser'
import { SignIn } from './pages/auth/sign-in'
import { EditProfile } from './pages/auth/profileEdit'

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
    children: [{ path: '/bis', element: <MonittoringBis /> }],
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/monittoring', element: <MonittoringTable /> }],
  },
  {
    path: '/',
    element: <AppLayout />,
    children: [{ path: '/editProfile', element: <EditProfile /> }],
  },
  // {
  // { path: '/', element: <Dashboard /> },
  // { path: '/sign-in', element: <SignIn /> },
])
