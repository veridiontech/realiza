import { createBrowserRouter } from 'react-router-dom'
import { AppLayout } from './_layouts/app'
import { AuthLayout } from './_layouts/auth'
import MonittoringBis from './pages/auth/bis'
import { Collaborators } from './pages/auth/collaborators'
import { MonittoringTable } from './pages/auth/monittoringTable'
import { ProfileClient } from './pages/auth/profileClient'
import { SignIn } from './pages/app/sign-in'
import { ForgotPassword } from './pages/app/forgot-password'
import { EditProfile } from './pages/auth/profileEdit'
import { Dashboard } from './pages/app/dashboard'
import { NewPassword } from './pages/app/new-password'
import { NewPassword2 } from './pages/app/new-password2'
import { ConfigurationLayout } from './_layouts/configurantion'
import { ProfileUser } from './pages/auth/profileUser/__profileUser'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { path: '/', element: <Dashboard /> },
      { path: '/profileClient', element: <ProfileClient /> },
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
      { path: 'new-password2', element: <NewPassword2 /> },
    ],
  },
  {
    path:"/",
    element: <ConfigurationLayout />,
    children: [
      {path: 'profile-user', element: <ProfileUser />},
    ]
  }
]);
