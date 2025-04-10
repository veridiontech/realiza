import { Outlet } from "react-router-dom";
import { HeaderProvider } from "@/components/header/providerHeader";
import { useUser } from "@/context/user-provider";
import { Blocks } from "react-loader-spinner";

export function ProviderAppLayout() {
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
      <HeaderProvider />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
      </div>
    </div>
  );
}
