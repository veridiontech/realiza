import { Header } from "@/components/header/realizaHeader";
import { Outlet } from "react-router-dom";

export function ProviderAppLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
      </div>
    </div>
  );
}
