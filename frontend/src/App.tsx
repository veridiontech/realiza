import './global.css'

import { Helmet, HelmetProvider } from 'react-helmet-async'
import { RouterProvider } from 'react-router-dom'
import { router } from './routes'
import { ThemeProvider } from './context/ThemeProvider'

export function App() {
  return (
    <ThemeProvider>
      <HelmetProvider>
        <Helmet titleTemplate="%s | realiza" />
        <RouterProvider router={router} />
      </HelmetProvider>
    </ThemeProvider>
  )
}
