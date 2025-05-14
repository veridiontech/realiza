import { Button } from "@/components/ui/button";
import { ProviderSolicitations } from "./provider-solicitations";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
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
                className="bg-realizaBlue"
                onClick={() => setSelectTab("provider")}
              >
                Empresas solicitantes
              </Button>
              <Button
                className="bg-realizaBlue"
                onClick={() => setSelectTab("user")}
              >
                Usuários solicitantes
              </Button>
            </div>
          </div>
          <Dialog>
            <DialogTrigger asChild>
              <Button className="bg-realizaBlue hidden md:block">
                Todas solicitações
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Are you absolutely sure?</DialogTitle>
              </DialogHeader>
            </DialogContent>
          </Dialog>
        </div>
      </div>
      {selectTab === "provider" && <ProviderSolicitations />}
      {selectTab === "user" && <UserSolicitations />}
    </div>
  );
}
