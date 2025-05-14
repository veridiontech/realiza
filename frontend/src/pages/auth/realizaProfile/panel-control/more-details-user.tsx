import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface MoreDetailsProps {
  idSolicitation: string;
  requesterFirstName: string;
  requesterSurname: string;
  requesterCpf: string | undefined;
  nameEnterprise: string | undefined;
  newUserFirstName: string;
  newUserSurname: string;
  newUserEmail:  string | undefined;
  newUserEnterprise:  string | undefined;
}

export function MoreDetailsUser({
  idSolicitation,
  requesterFirstName,
  requesterSurname,
  requesterCpf,
  nameEnterprise,
  newUserFirstName,
  newUserSurname,
  newUserEmail,
  newUserEnterprise
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
              <span>
                {requesterFirstName} {requesterSurname}
              </span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">Nome da empresa solicitante:</p>
              <span>{nameEnterprise}</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">CPF:</p>
              <span>{requesterCpf}</span>
            </div>
            <div className="h-[1px] w-full bg-neutral-900"></div>
            <div>
              <h1 className="font-semibold">Usuário solicitado:</h1>
              <div className="flex items-center gap-2">
                <p className="font-medium">Nome do novo usuário:</p>
                <span>
                  {newUserFirstName} {newUserSurname}
                </span>
              </div>
              <div className="flex items-center gap-2">
                <p className="font-medium">Email do novo usuário:</p>
                <span>{newUserEmail}</span>
              </div>
              <div className="flex items-center gap-2">
                <p className="font-medium">Empresa:</p>
                <span>{newUserEnterprise}</span>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
