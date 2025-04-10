import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

interface cardContractProps {
  idContract: string;
  serviceName: string;
  dateStart: string;
  description: string;
  // corporateName: string,
  // serviceDuration: string,
  // startDate: string
}

export function CardContract({
  idContract,
  dateStart,
  description,
  serviceName,
}: cardContractProps) {
  return (
    <div>
      <Dialog>
        <DialogTrigger asChild>
          <div className="flex flex-col gap-5">
            <div className=" border-l-realizaBlue w-auto cursor-pointer rounded-lg border border-l-8 p-5 shadow-lg hover:bg-gray-100 flex flex-col items-start gap-5" key={idContract}>
              <div className="flex gap-1">
                <h1>Nome do serviço: </h1>
                <p>{serviceName}</p>
              </div>
              <div className="text-[12px]">
                <p>Data de início</p>
                <p>{new Date(dateStart).toLocaleDateString("pt-BR")}</p>
              </div>
            </div>
          </div>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Detalhes do Contrato</DialogTitle>
            <div key={idContract} className="flex flex-col gap-5">
              <div className="flex flex-col gap-1">
                <h1>{serviceName}</h1>
                <div className="text-[12px]">
                  <p>{description}</p>
                </div>
              </div>
              <div>
                <p>Data de início</p>
                <p>{new Date(dateStart).toLocaleDateString("pt-BR")}</p>
              </div>
            </div>
          </DialogHeader>
        </DialogContent>
      </Dialog>
    </div>
  );
}
