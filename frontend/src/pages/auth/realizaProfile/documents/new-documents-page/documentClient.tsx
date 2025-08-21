import { useState } from "react";
import { Button } from "@/components/ui/button";
import { HistorySection } from "./history-section";
import { useBranch } from "@/context/Branch-provider"; 

export function DocumentClient() {
  const [selectTab, setSelectedTab] = useState("document");

  const { selectedBranch } = useBranch();
  const idBranch = selectedBranch?.idBranch;

  return (
    <div className="p-6 md:p-10 flex flex-col gap-6 md:gap-10">
      <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10 relative bottom-[8vw]">
        <h1 className="text-2xl md:text-[25px]">Painel de controle</h1>
        <div className="bg-[#7CA1F3] w-full h-[1px]" />
        <div className="flex items-center gap-5">
          {["historical"].map((tab) => (
            <Button
              key={tab}
              className={`${
                selectTab === tab
                  ? "bg-realizaBlue"
                  : "bg-transparent border text-black border-black hover:bg-neutral-300"
              }`}
              onClick={() => setSelectedTab(tab)}
            >
              {{
                historical: "Histórico",
              }[tab]}
            </Button>
          ))}
        </div>
      </div>

      <div>
        {selectTab === "historical" && idBranch && (
          <HistorySection idBranch={idBranch} />
        )}
      </div>
    </div>
  );
}