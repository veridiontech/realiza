import { Outlet } from "react-router-dom";

import { HeaderClient } from "@/components/header/clientHeader";

export function ClientAppLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <HeaderClient />
      <div className="dark:bg-primary-foreground flex-grow">
        <Outlet />
      </div>
    </div>
  );
}
