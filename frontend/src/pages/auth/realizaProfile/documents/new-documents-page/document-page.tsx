import { NewDocumentBox } from "./document-box-new";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { ActiviteSectionBox } from "./activities-section";

export function DocumentPageNew() {
  const [selectTab, setSelectedTab] = useState("document");

  return (
    <div className="p-6 md:p-10 flex flex-col gap-6 md:gap-10">
      <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10 relative bottom-[8vw]">
        <h1 className="text-2xl md:text-[25px]">Painel de controle</h1>
        <div className="bg-[#7CA1F3] w-full h-[1px]" />
        <div className="flex items-center gap-5">
          <Button
            className={`${
              selectTab === "document"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("document")}
          >
            Documentos
          </Button>
          <Button
            className={`${
              selectTab === "activities"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("activities")}
          >
            Atividades
          </Button>
          <Button
            className={`${
              selectTab === "profiles"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("profiles")}
          >
            Perfis e permissões
          </Button>
          <Button
            className={`${
              selectTab === "profiles"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("profiles")}
          >
            Serviços 
          </Button>
        </div>
      </div>
      <div>
        {selectTab === "document" && <NewDocumentBox />}
        {selectTab === "activities" && <ActiviteSectionBox />}
      </div>
    </div>
  );
}
