import { Outlet, useLocation } from 'react-router-dom'
import imageLogin from '@/assets/imageLogin.png'
import imageForgotPassword from '@/assets/imageForgotPassword.svg'
import bgAuth from '@/assets/bg-auth.svg'

export function AuthLayout() {
  const location = useLocation()

  const imageMap: Record<string, string> = {
    "/sign-in": imageLogin,
    "/forgot-password": imageForgotPassword,
  };

  const dynamicImage = imageMap[location.pathname]

  return (
    <div className='flex h-screen overflow-hidden'>
     <div
        className="w-6/12 bg-white bg-no-repeat bg-left-bottom"
        style={{ 
          backgroundImage: `url(${bgAuth})`,
          backgroundSize: '300px 300px'
         }}
      >
        <Outlet />
      </div>
      <div className='w-6/12 h-screen '>
          <img src={dynamicImage} alt="imagem" className="w-full object-cover" />
      </div>
    </div>
  )
}
