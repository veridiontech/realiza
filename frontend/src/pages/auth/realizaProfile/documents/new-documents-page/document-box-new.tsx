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

  // useEffect(() => {

  // }, [])

  return (
    <div className="relative">
      <div className="absolute left-0 right-0 top-0 z-10 rounded-lg bg-white p-5 shadow-md">
        <div className="flex gap-2">
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "thirdCompany"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => {
              setSelectedTab("thirdCompany"), handleClickToggle();
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
              setSelectedTab("thirdCollaborators"), handleClickToggle();
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
      </div>
      <div className="bg-white pt-24 shadow-md">
        {selectedTab === "thirdCompany" && <ThirdCompany />}
        {selectedTab === "thirdCollaborators" && <ThirdCollaborators />}
        {selectedTab === "training" && <TrainingBox />}
        {selectedTab === "otherRequirements" && <OrtherRequirements />}
        {selectedTab === "geral" && <ActivitiesBox />}
        {selectedTab === "ambient" && <AmbientBox />}
        {selectedTab === "trabalhista" && <TrabalhistaBox />}
      </div>
    </div>
  );
}
