import { NavLink } from "react-router-dom";

export function MenuNav() {
  return (
    <div>
      <nav className="flex items-center">
        <NavLink
          to={"/email/Enterprise-sign-up"}
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
          to={"/email/Sign-Up"}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center bg-gray-300 ${
              isActive
                ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md font-bold text-white"
                : "text-black"
            }`
          }
        >
          Cadastro
        </NavLink>
      </nav>
    </div>
  );
}
