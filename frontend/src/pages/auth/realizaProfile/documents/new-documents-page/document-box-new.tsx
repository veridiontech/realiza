import { Button } from "@/components/ui/button";
import { useState } from "react";
// import { BoxNonSelected } from "./box-non-selected";
// import { BoxSelected } from "./box-selected";
// import { propsDocument } from "@/types/interfaces";
// import {
//   AlertDialog,
//   AlertDialogAction,
//   AlertDialogCancel,
//   AlertDialogContent,
//   AlertDialogFooter,
//   AlertDialogHeader,
//   AlertDialogTitle,
//   AlertDialogTrigger,
// } from "@/components/ui/alert-dialog";
// import { useDocument } from "@/context/Document-provider";
import { ThirdCompany } from "../boxes-selected/third-company";
import { ThirdCollaborators } from "../boxes-selected/third-collaborators";
import { TrainingBox } from "../boxes-selected/services";
import { OrtherRequirements } from "../boxes-selected/orther-requirements";
import { useDocument } from "@/context/Document-provider";
import { ActivitiesBox } from "../boxes-selected/activities";
import { AmbientBox } from "../boxes-selected/ambient-box";
import { TrabalhistaBox } from "../boxes-selected/trabalhista-box";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { GeralBox } from "../boxes-selected/geral";
// import {
//   Dialog,
//   DialogContent,
//   DialogDescription,
//   DialogHeader,
//   DialogTitle,
//   DialogTrigger,
// } from "@/components/ui/dialog";

export function NewDocumentBox() {
  const [selectedTab, setSelectedTab] = useState("thirdCompany");
  const { setDocuments, setNonSelected } = useDocument();

  // const { documents, nonSelected } = useDocument();

  // const mockDocumentsNonSelected: propsDocument[] = [
  //   { idDocument: "1", name: "Documento 1" },
  //   { idDocument: "2", name: "Documento 2" },
  //   { idDocument: "3", name: "Documento 3" },
  //   { idDocument: "4", name: "Documento 4" },
  // ];

  // const mockDocumentsSelected: propsDocument[] = [
  //   { idDocument: "1", name: "Documento 11231231" },
  //   { idDocument: "2", name: "Documento 21231231" },
  //   { idDocument: "3", name: "Documento 31231231" },
  //   { idDocument: "4", name: "Documento 4231321" },
  // ];

  const handleClickToggle = () => {
    setDocuments([]);
    setNonSelected([]);
  };

  const tabsOrder = [
    "thirdCompany",
    "thirdCollaborators",
    "otherRequirements",
    "activities",
  ];

  const handlePrev = () => {
    const currentIndex = tabsOrder.indexOf(selectedTab);
    if (currentIndex > 0) {
      setSelectedTab(tabsOrder[currentIndex - 1]);
      handleClickToggle();
    }
  };

  const handleNext = () => {
    const currentIndex = tabsOrder.indexOf(selectedTab);
    if (currentIndex < tabsOrder.length - 1) {
      setSelectedTab(tabsOrder[currentIndex + 1]);
      handleClickToggle();
    }
  };

  const renderTabName = () => {
    if (selectedTab === "thirdCompany") return "Empresa terceiros";
    if (selectedTab === "thirdCollaborators") return "Colaboradores terceiros";
    if (selectedTab === "otherRequirements") return "Outras exigências";
    if (selectedTab === "activities") return "Atividades";
    return "";
  };

  // useEffect(() => {

  // }, [])

  return (
    <div className="relative bottom-[8vw]">
      <div className="absolute left-0 right-0 top-0 z-10 hidden gap-2 rounded-lg bg-white p-5 shadow-md md:flex items-center justify-between">
        <div>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "thirdCompany"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("thirdCompany");
              handleClickToggle();
            }}
          >
            Cadastro e certidões
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "thirdCollaborators"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("thirdCollaborators");
              handleClickToggle();
            }}
          >
            Saúde
          </Button>
          {/* <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "training"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("training")}
          >
            Treinamentos
          </Button> */}
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "otherRequirements"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("otherRequirements"), handleClickToggle();
            }}
          >
            Segurança do Trabalho
          </Button>{" "}
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "ambient"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("ambient"), handleClickToggle();
            }}
          >
            Meio Ambiente
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "trabalhista"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("trabalhista"), handleClickToggle();
            }}
          >
            Trabalhista
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "geral"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("geral"), handleClickToggle();
            }}
          >
            Geral
          </Button>
        </div>
        {/* <Dialog>
          <DialogTrigger className="">
            <Button className="bg-realizaBlue">+</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Are you absolutely sure?</DialogTitle>
              <DialogDescription>
                This action cannot be undone. This will permanently delete your
                account and remove your data from our servers.
              </DialogDescription>
            </DialogHeader>
          </DialogContent>
        </Dialog> */}
      </div>
      <div className="absolute left-0 right-0 top-0 z-10 flex items-center justify-between gap-4 rounded-lg bg-white p-5 shadow-md md:hidden">
        <Button
          variant={"ghost"}
          onClick={handlePrev}
          disabled={selectedTab === "thirdCompany"}
          className={`text-realizaBlue ${selectedTab === "thirdCompany" ? "cursor-not-allowed opacity-50" : ""}`}
        >
          <ChevronLeft className="h-6 w-6" />
        </Button>
        <div className="flex flex-1 justify-center">
          <Button
            variant={"ghost"}
            className="bg-realizaBlue pointer-events-none px-6 py-3 font-bold text-white shadow-lg"
          >
            {renderTabName()}
          </Button>
        </div>
        <Button
          variant={"ghost"}
          onClick={handleNext}
          disabled={selectedTab === "activities"}
          className={`text-realizaBlue ${selectedTab === "activities" ? "cursor-not-allowed opacity-50" : ""}`}
        >
          <ChevronRight className="h-6 w-6" />
        </Button>
      </div>
      <div className="bg-white pt-24 shadow-md">
        {selectedTab === "thirdCompany" && <ThirdCompany />}
        {selectedTab === "thirdCollaborators" && <ThirdCollaborators />}
        {selectedTab === "training" && <TrainingBox />}
        {selectedTab === "otherRequirements" && <OrtherRequirements />}
        {selectedTab === "geral" && <GeralBox />}
        {selectedTab === "activities" && <ActivitiesBox />}
        {selectedTab === "ambient" && <AmbientBox />}
        {selectedTab === "trabalhista" && <TrabalhistaBox />}
      </div>
    </div>
  );
}
