import { Outlet } from "react-router-dom";
import { Header } from "@/components/header/header";
import { LateralMenu } from "@/components/asideConfiguration";

export function ConfigurationLayout() {
  return (
    <div className="flex flex-col min-h-screen bg-white">
      <Header />
      <div className="flex flex-grow bg-gray-200">
        <div className="flex flex-shrink-0 w-1/5 bg-white mx-4 my-6">
          <LateralMenu />
        </div>
        <div className="flex flex-col flex-grow mx-4 my-6 bg-white rounded-lg shadow">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
