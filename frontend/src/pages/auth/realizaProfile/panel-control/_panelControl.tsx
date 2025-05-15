import { Button } from "@/components/ui/button";
import { ProviderSolicitations } from "./provider-solicitations";
import { useState } from "react";
import { UserSolicitations } from "./users-solicitation";

export function ControlPanel() {
  const [selectTab, setSelectTab] = useState("provider");

  return (
    <div>
      <div className="relative bottom-[3vw] flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
        <div className="flex w-full flex-row items-center justify-between gap-4">
          <div className="flex flex-col items-start gap-3">
            <h2 className="text-center text-lg font-semibold">
              Painel de Controle
            </h2>
            <div className="flex items-center gap-2">
              <Button
                className={`${
                  selectTab === "provider" ? "bg-realizaBlue" : "bg-transparent border text-black border-black hover:bg-neutral-300"
                }`}
                onClick={() => setSelectTab("provider")}
              >
                Empresas solicitantes
              </Button>

              <Button
                className={`${
                  selectTab === "user" ? "bg-realizaBlue" : "bg-transparent border text-black border-black hover:bg-neutral-300"
                }`}
                onClick={() => setSelectTab("user")}
              >
                Usuários solicitantes
              </Button>
            </div>
          </div>
        </div>
      </div>
      {selectTab === "provider" && <ProviderSolicitations />}
      {selectTab === "user" && <UserSolicitations />}
    </div>
  );
}
