import { Outlet, useLocation } from "react-router-dom";
import imageLogin from "@/assets/sign-in-image.jpg";
import imageForgotPassword from "@/assets/imageForgotPassword.png";
import bgAuth from "@/assets/bg-auth.svg";

export function AuthLayout() {
  const location = useLocation();

  const imageMap: Record<string, string> = {
    "/": imageLogin,
    "/forgot-password": imageForgotPassword,
    "/new-password": imageForgotPassword,
    "/new-password2": imageForgotPassword,
  };

  const dynamicImage = imageMap[location.pathname];

  return (
    <div className="flex h-screen w-full overflow-hidden">
      <div
        className="relative z-10 w-full md:w-6/12 bg-[#34495E] bg-left-bottom bg-no-repeat"
        style={{
          backgroundImage: `url(${bgAuth})`,
          backgroundSize: "300px 300px",
        }}
      >
        <Outlet />
      </div>

      <div className="hidden md:block relative w-6/12 h-full overflow-hidden">
        <img
          src={dynamicImage}
          alt="imagem"
          className="absolute inset-0 w-full h-full object-cover z-0"
        />
        <div className="absolute inset-0 z-1 bg-gradient-to-r from-[#34495e]/100 to-transparent" />
      </div>
    </div>
  );
}
