import { Button } from "@/components/ui/button";
import { ProviderSolicitations } from "./provider-solicitations";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

export function ControlPanel() {
  return (
    <div>
      <div className="relative bottom-[3vw] flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
        <div className="flex w-full flex-row items-center justify-between gap-4">
          <div>
            <h2 className="text-center text-lg font-semibold">
              Painel de Controle
            </h2>
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
      <ProviderSolicitations />
    </div>
  );
}
