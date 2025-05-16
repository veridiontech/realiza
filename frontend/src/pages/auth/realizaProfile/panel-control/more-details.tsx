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
  nameEnterprise: string | undefined;
  requesterCpf: string | undefined;
  corporateName: string | undefined;
  cnpj: string | undefined;
}

export function MoreDetails({
  idSolicitation,
 requesterFirstName,
  nameEnterprise,
  requesterSurname,
  requesterCpf,
  corporateName,
  cnpj,
}: MoreDetailsProps) {
  return (
    <Dialog>
      <DialogTrigger className="bg-realizaBlue rounded-md p-2 text-white">
        Mais detalhes
      </DialogTrigger>
      <DialogContent className="min-w-[45vw]">
        <DialogHeader>
          <DialogTitle>Are you absolutely sure?</DialogTitle>
        </DialogHeader>
        <div className="" key={idSolicitation}>
          <div className="flex flex-col gap-2">
            <h1 className="font-semibold">Solicitante:</h1>
            <div className="flex items-center gap-1">
              <p className="font-medium">Nome do solicitante:</p>
              <span>
                {requesterFirstName} {requesterSurname}
              </span>
            </div>
            <div className="flex items-center gap-1">
              <p className="font-medium">Nome da empresa solicitante: </p>
              <span>{nameEnterprise}</span>
            </div>
            <div className="flex items-center gap-1">
                <p className="font-medium">CPF:</p>
                <span>{requesterCpf}</span>
            </div>
            <div className="bg-neutral-900 h-[1px] w-full"></div>
            <div>
                <h1 className="font-semibold">Solicitado: </h1>
                <div className="flex items-center gap-1">
                    <p className="font-medium">Razão social:</p>
                    <span>{corporateName}</span>
                </div>
                <div className="flex items-center gap-1">
                    <p className="font-medium">CNPJ:</p>
                    <span>{cnpj}</span>
                </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
