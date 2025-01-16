import { CircleDollarSign } from "lucide-react";
import { Helmet } from "react-helmet-async";

import { BarChartMonitoring } from "@/components/BIs/BisPageComponents/barChartRecive";
import { BarChartTotalSale } from "@/components/BIs/BisPageComponents/barChartTotalSale";
import { PieChartDespesas } from "@/components/BIs/BisPageComponents/pieChartDespesas";
import { PrejudiceChart } from "@/components/BIs/BisPageComponents/prejudiceChart";
import { ProfitChart } from "@/components/BIs/BisPageComponents/profitChart";

const MonittoringBis = () => {
  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-20 flex flex-col gap-[50px]">
        <div className="shadow-custom-blue mt-[30px] flex h-[250px] flex-col gap-[50px] rounded-md bg-white dark:bg-primary pt-[35px]">
          <div className="flex flex-row justify-between px-20">
            <div className="flex items-center gap-2">
              <CircleDollarSign />
              <h2 className="font-semibold">Receita total da enterprise</h2>
            </div>
            <span className="text-[14px] font-medium text-gray-600">
              atualizado em 2024
            </span>
          </div>
          <div className="flex flex-row items-center justify-center gap-[87px]">
            <div className="flex flex-col items-center gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Total arrecadado</h3>
                <div className="flex h-[30px] w-[30px] items-center justify-center rounded-full bg-blue-500 font-semibold text-white">
                  i
                </div>
              </div>
              <span className="text-[20px] font-semibold text-sky-500">
                1,7 BILHÕES
              </span>
            </div>
            <div className="flex flex-col items-center gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Total de solicitações</h3>
                <div className="flex h-[30px] w-[30px] items-center justify-center rounded-full bg-blue-500 font-semibold text-white">
                  i
                </div>
              </div>
              <span className="text-[20px] font-semibold text-sky-500">
                3.455.000.000.00
              </span>
            </div>
            <div className="flex flex-col items-start gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Total vendido</h3>
                <div className="flex h-[30px] w-[30px] items-center justify-center rounded-full bg-blue-500 font-semibold text-white">
                  i
                </div>
              </div>
              <span className="text-[20px] font-semibold text-sky-500">
                2.000.000.00
              </span>
            </div>
          </div>
        </div>
        <div>
          <BarChartMonitoring />
        </div>
        <div>
          <div className="flex flex-row gap-[20px]">
            {/* distribuicao(pizza) */}
            <div>
              <PieChartDespesas />
            </div>
            <div>
              <ProfitChart />
              <PrejudiceChart />
            </div>
          </div>
        </div>
        <div>
          <h2>Indicadores de venda</h2>
        </div>
        <BarChartTotalSale />
      </section>
    </>
  );
};
export default MonittoringBis;
