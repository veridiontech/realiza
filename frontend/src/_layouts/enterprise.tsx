import { Outlet } from "react-router-dom";
import { Header } from "@/components/header/header";

export function EnterpriseLayout() {
  return (
    <div className="flex flex-col min-h-screen bg-white">
      <Header />
      <div className="flex flex-grow bg-gray-200">
        <div className="flex flex-col flex-grow mx-4 my-6 bg-white rounded-lg shadow">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
