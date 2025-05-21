import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface MoreDetailsProps {
  clientCnpj: string;
  clientTradeName: string;
  creationDate: string;
  idSolicitation: string;
  requesterEmail: string;
  requesterFullName: string;
  // solicitationType: string;
  // status: string;
  userFullName: string;
}

export function MoreDetailsUser({
  idSolicitation,
  clientCnpj,
  clientTradeName,
  // creationDate,
  requesterEmail,
  requesterFullName,
  // solicitationType,
  // status,
  userFullName,
}: MoreDetailsProps) {
  return (
    <Dialog>
      <DialogTrigger className="bg-realizaBlue rounded-md p-2 text-white">
        Mais detalhes
      </DialogTrigger>
      <DialogContent className="min-w-[45vw]">
        <DialogHeader>
          <DialogTitle>Detalhes da Solicitação</DialogTitle>
        </DialogHeader>
        <div key={idSolicitation}>
          <div className="flex flex-col gap-2">
            <h1 className="font-semibold">Solicitante:</h1>
            <div className="flex items-center gap-1">
              <p className="font-medium">Nome do solicitante:</p>
              <span>{requesterFullName}</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">Nome da empresa solicitante:</p>
              <span>{clientTradeName}</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">CNPJ:</p>
              <span>{clientCnpj}</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">Email:</p>
              <span>{requesterEmail}</span>
            </div>
            <div className="h-[1px] w-full bg-neutral-900"></div>
            <div>
              <h1 className="font-semibold">Usuário solicitado:</h1>
              <div className="flex items-center gap-2">
                <p className="font-medium">Nome do novo usuário:</p>
                <span>{userFullName}</span>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
