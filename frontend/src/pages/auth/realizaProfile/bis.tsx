import { Helmet } from "react-helmet-async";

import { StatusDocumentChart } from "@/components/BIs/BisPageComponents/statusDocumentChart";
import { ExemptionPendingChart } from "@/components/BIs/BisPageComponents/exemptionRankingChart";
import { ConformityRankingTable } from "@/components/BIs/BisPageComponents/conformityRankingTable";

export const MonittoringBis = () => {
  return (
    <>
      <Helmet title="monitoring table" />
      <section className="mx-5 md:mx-20 flex flex-col gap-12 pb-20">


        <div className="overflow-x-auto mt-10 pb-10">
          <StatusDocumentChart />
        </div>


        <div className="overflow-x-auto mt-10">
          <div className="flex flex-col md:flex-row gap-6 md:gap-8 min-w-[320px] md:min-w-full">
            <div className="flex-shrink-0 w-full md:w-[300px]">
              <ExemptionPendingChart />
            </div>
            <div className="flex-grow min-w-[320px]">
              <ConformityRankingTable />
            </div>
          </div>
        </div>
      </section>
    </>
  );
};
