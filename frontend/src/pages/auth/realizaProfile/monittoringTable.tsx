import { Filter, Search } from "lucide-react";

import { PieChartProjects } from "@/components/BIs/pieChartProjects";
import { Button } from "@/components/ui/button";

import calendar from "@/assets/calendar.png";

export function MonittoringTable() {
  return (
    <div className="mx-40 mt-[30px] flex flex-col gap-16">
      <div className="shadow-custom-blue flex h-[244px] flex-col gap-20 rounded-md bg-white px-[30px] py-[20px]">
        <div className="flex items-center justify-between">
          <div className="flex flex-row gap-4">
            <Button variant={"ghost"}>Por data</Button>
            <Button variant={"ghost"}>Por ganho</Button>
          </div>
          <span>Mostrando 10 de 72 registros</span>
        </div>
        <div className="flex flex-row justify-between">
          <div className="flex w-[460px] items-center gap-3 rounded-lg border border-sky-800 bg-zinc-100 px-4 py-2">
            <Search className="size-5 text-zinc-900" />
            <input
              className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
              placeholder="Pesquisar unidades, ações etc..."
            />
          </div>
          <Button variant={"outline"} className="border border-black">
            <Filter />
            Filtros
          </Button>
        </div>
      </div>
      <div className="shadow-custom-blue flex h-[280px] items-center justify-center gap-5 rounded-md bg-white">
        <div className="flex h-[250px] w-[460px] flex-row items-center justify-center gap-4 rounded-md border border-gray-200">
          <img src={calendar} className="h-[108px] w-[108px]"></img>
          <div>
            <h3>Atualização do Mês 08</h3>
            <span>09/08/2024</span>
          </div>
        </div>
        <div>
          <PieChartProjects />
        </div>
      </div>
    </div>
  );
}
