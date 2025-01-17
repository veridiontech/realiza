import { Outlet, useLocation } from 'react-router-dom'
import imageLogin from '@/assets/imageLogin.png'
import imageForgotPassword from '@/assets/imageForgotPassword.svg'
import bgAuth from '@/assets/bg-auth.svg'
// import { useUser } from '@/context/user-provider'

export function AuthLayout() {
  // const { authUser } = useUser()
  const location = useLocation()

  const imageMap: Record<string, string> = {
    "/": imageLogin,
    "/forgot-password": imageForgotPassword,
    "/new-password": imageForgotPassword,
    "/new-password2": imageForgotPassword,
  };

  const dynamicImage = imageMap[location.pathname]

  // if(!authUser) {
  //   <div>
  //     pagina nao autenticada
  //   </div>
  // }  

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
      <div className='w-6/12 h-screen md:flex hidden '>
          <img src={dynamicImage} alt="imagem" className="w-full object-cover" />
      </div>
    </div>
  )
}
