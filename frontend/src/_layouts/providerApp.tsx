import { Outlet } from "react-router-dom";
import { HeaderProvider } from "@/components/header/providerHeader";

export function ProviderAppLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <HeaderProvider />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
      </div>
    </div>
  );
}
