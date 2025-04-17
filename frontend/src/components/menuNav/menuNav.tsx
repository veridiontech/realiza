import { NavLink } from "react-router-dom";
import { useUser } from "@/context/user-provider";

export function MenuNav() {
  const { token } = useUser();

  const handleClick = (e: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
    e.preventDefault();
  };

  return (
    <div>
      <nav className="flex items-center">
        <NavLink
          to={`/email/Enterprise-sign-up/validate?token=${token}`}
          onClick={handleClick}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center rounded-l-md bg-gray-300 cursor-not-allowed ${
              isActive
                ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md p-2 font-bold text-white"
                : "text-black"
            }`
          }
        >
          Empresa
        </NavLink>
        <NavLink
          to={`/email/Sign-Up?token=${token}`}
          onClick={handleClick}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center rounded-r-md bg-gray-300 cursor-not-allowed ${
              isActive
                ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md font-bold text-white"
                : "text-black"
            }`
          }
        > 
          Dados Pessoais
        </NavLink>
      </nav>
    </div>
  );
}
