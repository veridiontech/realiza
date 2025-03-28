import { ModalAddContract } from "@/components/realizaAddContract";
import { CardContract } from "./card-contract";

export default function ContractsTable() {

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white p-10">
        <div className="m-8 flex items-center justify-between">
          <h1 className="text-2xl">Contratos</h1>
          <ModalAddContract /> 
        </div>
        <div className="flex flex-col gap-">
          <CardContract />
        </div>
      </div>
    </div>
  );
}
