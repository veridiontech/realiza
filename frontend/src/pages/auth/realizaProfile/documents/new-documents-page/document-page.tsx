import { NewDocumentBox } from "./document-box-new";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { ActiviteSectionBox } from "./activities-section";
import { ServicesSection } from "./services-section";
import { HistorySection } from "./history-section"; // Importando o componente de Histórico

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
            Alocar documentos
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
              selectTab === "services"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("services")}
          >
            Serviços
          </Button>
          {/* Novo botão Histórico */}
          <Button
            className={`${
              selectTab === "historical"
                ? "bg-realizaBlue"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }`}
            onClick={() => setSelectedTab("historical")}
          >
            Histórico
          </Button>
        </div>
      </div>
      <div>
        {selectTab === "document" && <NewDocumentBox />}
        {selectTab === "activities" && <ActiviteSectionBox />}
        {selectTab === "services" && <ServicesSection />}
        {/* Condição para renderizar o conteúdo do Histórico */}
        {selectTab === "historical" && <HistorySection />} {/* Exibe a seção de Histórico */}
      </div>
    </div>
  );
}
