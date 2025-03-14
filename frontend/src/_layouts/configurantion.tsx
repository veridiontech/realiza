import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/realizaHeader";
import { useUser } from "@/context/user-provider";
import { HeaderClient } from "@/components/header/clientHeader";
import { HeaderProvider } from "@/components/header/providerHeader";

export function ConfigurationLayout() {
  const { user } = useUser();

  if (user?.role === "ROLE_ADMIN") {
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

  if (user?.role === "ROLE_CLIENT_RESPONSIBLE") {
    return (
      <div className="flex min-h-screen flex-col bg-white">
        <HeaderClient />
        <div className="flex flex-grow bg-gray-200">
          <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
            <Outlet />
          </div>
        </div>
      </div>
    );
  }

  if (user?.role === "ROLE_CLIENT_MANAGER") {
    return (
      <div className="flex min-h-screen flex-col bg-white">
        <HeaderClient />
        <div className="flex flex-grow bg-gray-200">
          <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
            <Outlet />
          </div>
        </div>
      </div>
    );
  }

  if (user?.role === "ROLE_SUPPLIER_RESPONSIBLE") {
    return (
      <div className="flex min-h-screen flex-col bg-white">
        <HeaderProvider />
        <div className="flex flex-grow bg-gray-200">
          <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
            <Outlet />
          </div>
        </div>
      </div>
    );
  }

  if (user?.role === "ROLE_SUBCONTRACTOR_RESPONSIBLE") {
    return (
      <div className="flex min-h-screen flex-col bg-white">
        <HeaderProvider />
        <div className="flex flex-grow bg-gray-200">
          <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
            <Outlet />
          </div>
        </div>
      </div>
    );
  }

  if(user?.role === "ROLE_SUBCONTRACTOR_RESPONSIBLE") {
    return (
      <div className="flex min-h-screen flex-col bg-white">
        <HeaderProvider />
        <div className="flex flex-grow bg-gray-200">
          <div className="mx-4 my-6 flex flex-grow flex-col rounded-lg bg-white shadow">
            <Outlet />
          </div>
        </div>
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
