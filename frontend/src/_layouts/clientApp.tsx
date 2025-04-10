import { Outlet } from "react-router-dom";

import { HeaderClient } from "@/components/header/clientHeader";
import { Blocks } from "react-loader-spinner";
import { useUser } from "@/context/user-provider";

export function ClientAppLayout() {
    const { authUser, loading } = useUser();
  
    if (loading) {
      return (
        <div className="flex min-h-screen items-center justify-center">
          <Blocks
            height="80"
            width="80"
            color="#34495E"
            ariaLabel="blocks-loading"
            wrapperStyle={{}}
            wrapperClass="blocks-wrapper"
            visible={true}
          />
        </div>
      );
    }
  
    if (!authUser) {
      return (
        <div className="flex min-h-screen items-center justify-center">
          pagina nao autenticada
        </div>
      );
    }

  return (
    <div className="flex min-h-screen flex-col">
      <HeaderClient />
      <div className="dark:bg-primary-foreground flex-grow">
        <Outlet />
      </div>
    </div>
  );
}
