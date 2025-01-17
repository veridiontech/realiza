import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/header";
import { useUser } from "@/context/user-provider";

export function AppLayout() {
  const { authUser } = useUser();

  if (!authUser) {
    return <div className="flex items-center justify-center min-h-screen">pagina nao autenticada</div>;
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
      </div>
    </div>
  );
}
