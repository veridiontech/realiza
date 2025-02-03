import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/realizaHeader";

export function ConfigurationLayout() {
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
