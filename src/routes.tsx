import { createBrowserRouter } from 'react-router-dom'

import { AppLayout } from './pages/_layouts/app'
import { AuthLayout } from './pages/_layouts/auth'
import { Dashboard } from './pages/app/dashboard'
import { SignIn } from './pages/auth/sign-in'
import { ProfileUser } from './pages/auth/profileUser'
import { Collaborators } from './pages/auth/collaborators'

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
  // { path: '/', element: <Dashboard /> },
  // { path: '/sign-in', element: <SignIn /> },
])
