import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/clientHeader";

export function ClientAppLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
      </div>
    </div>
  );
}
