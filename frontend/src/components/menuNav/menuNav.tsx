import { NavLink } from "react-router-dom";
import { useUser } from "@/context/user-provider";

export function MenuNav() {
  const { token } = useUser();

  return (
    <div>
      <nav className="flex items-center">
        <NavLink
          to={`/email/Enterprise-sign-up/validate?token=${token}`}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center rounded-l-md bg-gray-300 ${
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
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center rounded-r-md bg-gray-300 ${
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
