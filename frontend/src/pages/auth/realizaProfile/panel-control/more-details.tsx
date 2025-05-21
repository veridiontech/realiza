import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface MoreDetailsProps {
  idSolicitation: string;
  client: {
    idClient?: string;
    cnpj?: string;
    tradeName?: string;
    corporateName?: string;
    logo?: string;
    email?: string;
    telephone?: string;
    cep?: string;
    state?: string;
    city?: string;
    address?: string;
    isUltragaz?: true;
    number?: string;
    isActive?: true;
    deleteRequest?: boolean;
    creationDate?: string;
  };
  newProvider: {
    cnpj?: string;
    corporateName?: string;
  };
  requester: {
    fullName?: string;
    email?: string;
  };
}

export function MoreDetails({
  idSolicitation,
  client,
  newProvider,
  requester,
}: MoreDetailsProps) {
  return (
    <Dialog>
      <DialogTrigger className="bg-realizaBlue rounded-md p-2 text-white">
        Mais detalhes
      </DialogTrigger>
      <DialogContent className="min-w-[45vw]">
        <DialogHeader>
          <DialogTitle>DETALHES DA SOLICITAÇÃO</DialogTitle>
        </DialogHeader>
        <div key={idSolicitation}>
          <div className="flex flex-col gap-5">
            <h1 className="font-semibold">Solicitação de:</h1>
            <div className="flex gap-1 flex-col">
              <p className="">
                <strong>Nome:</strong> {requester.fullName}
              </p>
              <p className="">
                <strong>Email:</strong> {requester.email}
              </p>
            </div>
            <div>
              <div>
                <h2 className="font-semibold">Nova Empresa Solicitante:</h2>
              </div>
              <div>
                <p>
                  {newProvider.corporateName} - {newProvider.cnpj}
                </p>
              </div>
            </div>
            <div className="bg-neutral-900 h-[1px] w-full"></div>
            <div>
              <h1 className="font-semibold">Cliente</h1>
              <div className="flex gap-1 flex-col">
                <div className="flex items-center gap-1">
                  <p className="font-medium">Razão social: </p>
                  <span>{client.corporateName}</span>
                </div>
                <div className="flex items-center gap-1">
                  <p className="font-semibold">Nome Fantasia: </p>
                  <span>{client.tradeName}</span>
                </div>
                <div className="flex items-center gap-1">
                  <p className="font-semibold">cnpj:</p>
                  <span>{client.cnpj}</span>
                </div>
                                                <div className="flex items-center gap-1">
                  <p className="font-semibold">Email:</p>
                  <span>{client.email}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
