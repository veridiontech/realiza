// import { CircleDollarSign } from "lucide-react";
import { Helmet } from "react-helmet-async";

import { DocumentTypeChart } from "@/components/BIs/BisPageComponents/documentTypeChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";

export const MonittoringBis = () => {
  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-20 flex flex-col gap-[50px]">
        <div className="shadow-custom-blue dark:bg-primary mt-[30px] flex h-[250px] flex-col gap-[50px] rounded-md bg-white pt-[35px]">
          <div className="flex flex-row justify-between px-20">
            <div className="flex items-center gap-2">
            </div>
            <span className="text-[14px] font-medium text-gray-600">
              atualizado em 2024
            </span>
          </div>
          <div className="flex flex-row items-center justify-center gap-[87px]">
            <div className="flex flex-col items-center gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Fornecedores</h3>
                <div className="bg-realizaBlue flex h-[30px] w-[30px] items-center justify-center rounded-full font-semibold text-white">
                  i
                </div>
              </div>
              <span className="text-[20px] font-semibold text-sky-500">
                3
              </span>
            </div>
            <div className="flex flex-col items-center gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Serviços</h3>
                <div className="bg-realizaBlue flex h-[30px] w-[30px] items-center justify-center rounded-full font-semibold text-white">
                  i
                </div>
              </div>
              <span className="text-[20px] font-semibold text-sky-500">
                4
              </span>
            </div>
            <div className="flex flex-col items-start gap-[30px]">
              <div className="flex flex-row items-center gap-2">
                <h3 className="text-[18px]">Funcionários Alocados</h3>
                <div className="bg-realizaBlue flex h-[30px] w-[30px] items-center justify-center rounded-full font-semibold text-white">
                  i
                </div>
              </div>
              <div className="w-full text-center">
                <span className="text-[20px] font-semibold text-sky-500">
                  703
                </span>
              </div>
            </div>

          </div>
        </div>
        <div>
          <DocumentTypeChart />
        </div>
        <div>
          <div className="flex flex-row gap-[20px]">
            {/* distribuicao(pizza) */}
            <div>
              <ExemptionPendingChart />
            </div>
            <div>
              <ConformityRankingTable />
            </div>
          </div>
        </div>
      </section>
    </>
  );
};