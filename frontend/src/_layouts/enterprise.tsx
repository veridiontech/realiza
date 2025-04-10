import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/realizaHeader";
import { useUser } from "@/context/user-provider";
import { Blocks } from "react-loader-spinner";

export function EnterpriseLayout() {
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
    <div className="flex min-h-screen flex-col bg-white">
      <Header />
      <div className="flex flex-grow bg-gray-200">
        <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
