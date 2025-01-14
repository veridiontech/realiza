import { NavLink } from "react-router-dom";

export function MenuNav() {
  return (
    <div>
      <nav className="flex items-center">
      <NavLink
          to={"/email/Enterprise-sign-up"}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center bg-gray-300 rounded-l-md ${isActive ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md p-2 font-bold text-white" : "text-black cursor-not-allowed pointer-events-none"}`
          }
        >
          {" "}
          Empresa{" "}
        </NavLink>
        <NavLink
          to={"/email/Sign-Up"}
          className={({ isActive }) =>
            `roudned flex h-[6vh] w-[10vw] items-center justify-center bg-gray-300 ${isActive ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md font-bold text-white" : "text-black cursor-not-allowed pointer-events-none"}`
          }
        >
          {" "}
          Cadastro{" "}
        </NavLink>
        <NavLink
          to={"/email/Login"}
          className={({ isActive }) =>
            `flex h-[6vh] w-[10vw] items-center justify-center rounded-r-md bg-gray-300 ${isActive ? "bg-realizaBlue h-[9vh] w-[10vw] rounded-md font-bold text-white shadow-md" : `text-black cursor-not-allowed pointer-events-none`}`
          }
        >
          {" "}
          Login{" "}
        </NavLink>
      </nav>
    </div>
  );
}
