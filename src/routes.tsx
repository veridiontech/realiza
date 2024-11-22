import { createBrowserRouter } from 'react-router-dom'

import { AppLayout } from './_layouts/app'
import { AuthLayout } from './_layouts/auth'
import MonittoringBis from './pages/auth/bis'
import { Collaborators } from './pages/auth/collaborators'
import { MonittoringTable } from './pages/auth/monittoringTable'
import { ProfileUser } from './pages/auth/profileUser'
import { SignIn } from './pages/app/sign-in'
import { ForgotPassword } from './pages/app/forgot-password'
import { EditProfile } from './pages/auth/profileEdit'
import { Dashboard } from './pages/app/dashboard'
import { NewPassword } from './pages/app/new-password'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { path: '/', element: <Dashboard /> },
      { path: '/profile', element: <ProfileUser /> },
      { path: '/collaborators', element: <Collaborators /> },
      { path: '/bis', element: <MonittoringBis /> },
      { path: '/monittoring', element: <MonittoringTable /> },
      { path: '/editProfile', element: <EditProfile /> },
    ],
  },
  {
    path: '/',
    element: <AuthLayout />,
    children: [
      { path: 'sign-in', element: <SignIn /> },
      { path: 'forgot-password', element: <ForgotPassword /> },
      { path: 'new-password', element: <NewPassword /> },
    ],
  },
]);
