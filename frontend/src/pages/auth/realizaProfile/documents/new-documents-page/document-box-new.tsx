import { Button } from "@/components/ui/button";
import { useState } from "react";
import { BoxNonSelected } from "./box-non-selected";
import { BoxSelected } from "./box-selected";
import { propsDocument } from "@/types/interfaces";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { useDocument } from "@/context/Document-provider";

export function NewDocumentBox() {
  const [selectedTab, setSelectedTab] = useState("thirdCompany");
  const { documents, nonSelected } = useDocument();

  const mockDocumentsNonSelected: propsDocument[] = [
    { idDocument: "1", name: "Documento 1" },
    { idDocument: "2", name: "Documento 2" },
    { idDocument: "3", name: "Documento 3" },
    { idDocument: "4", name: "Documento 4" },
  ];

  const mockDocumentsSelected: propsDocument[] = [
    { idDocument: "1", name: "Documento 11231231" },
    { idDocument: "2", name: "Documento 21231231" },
    { idDocument: "3", name: "Documento 31231231" },
    { idDocument: "4", name: "Documento 4231321" },
  ];

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
            onClick={() => setSelectedTab("thirdCompany")}
          >
            Empresa terceiros
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "thirdCollaborators"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("thirdCollaborators")}
          >
            Colaboradores terceiros
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "training"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("training")}
          >
            Treinamentos
          </Button>
          <Button
            variant={"ghost"}
            className={`px-4 py-2 transition-all duration-300 ${
              selectedTab === "otherRequirements"
                ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                : "text-realizaBlue bg-white"
            }`}
            onClick={() => setSelectedTab("otherRequirements")}
          >
            Outras exigências
          </Button>
        </div>
      </div>
      <div className="bg-white pt-24 shadow-md">
        {selectedTab === "thirdCompany" && (
          <div className="flex items-center justify-center gap-10 p-10">
            <div>
              <BoxNonSelected documents={mockDocumentsNonSelected} />
            </div>
            <div className="flex flex-col gap-5">
              <div>
                <AlertDialog>
                  <AlertDialogTrigger className="bg-realizaBlue w-[10vw] rounded-md p-4 text-white">
                    Confirmar Seleção
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>
                        Documentos Selecionados
                      </AlertDialogTitle>
                    </AlertDialogHeader>
                    <div>
                      <ul>
                        {nonSelected.length > 0 ? (
                          nonSelected.map((doc) => (
                            <li key={doc.idDocument}>{doc.name}</li>
                          ))
                        ) : (
                          <p>Nenhum documento selecionado.</p>
                        )}
                      </ul>
                    </div>
                    <AlertDialogFooter>
                      <AlertDialogCancel>Cancelar</AlertDialogCancel>
                      <AlertDialogAction>Confirmar</AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
              <div>
                <AlertDialog>
                  <AlertDialogTrigger className="w-[10vw] rounded-md bg-red-600 p-3 text-white">
                    Confirmar Remoção
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>
                        Documentos Selecionados
                      </AlertDialogTitle>
                    </AlertDialogHeader>
                    <div>
                      <ul>
                        {documents.length > 0 ? (
                          documents.map((doc) => (
                            <li key={doc.idDocument}>{doc.name}</li>
                          ))
                        ) : (
                          <p>Nenhum documento selecionado.</p>
                        )}
                      </ul>
                    </div>
                    <AlertDialogFooter>
                      <AlertDialogCancel>Cancelar</AlertDialogCancel>
                      <AlertDialogAction>Confirmar</AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </div>
            </div>
            <div>
              <BoxSelected documents={mockDocumentsSelected} />
            </div>
          </div>
        )}
        {selectedTab === "thirdCollaborators" && (
          <div className="flex items-center justify-center gap-10 p-10">
          <div>
            <BoxNonSelected documents={mockDocumentsNonSelected} />
          </div>
          <div className="flex flex-col gap-5">
            <div>
              <AlertDialog>
                <AlertDialogTrigger className="bg-realizaBlue w-[10vw] rounded-md p-4 text-white">
                  Confirmar Seleção
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Documentos Selecionados
                    </AlertDialogTitle>
                  </AlertDialogHeader>
                  <div>
                    <ul>
                      {nonSelected.length > 0 ? (
                        nonSelected.map((doc) => (
                          <li key={doc.idDocument}>{doc.name}</li>
                        ))
                      ) : (
                        <p>Nenhum documento selecionado.</p>
                      )}
                    </ul>
                  </div>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancelar</AlertDialogCancel>
                    <AlertDialogAction>Confirmar</AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
            <div>
              <AlertDialog>
                <AlertDialogTrigger className="w-[10vw] rounded-md bg-red-600 p-3 text-white">
                  Confirmar Remoção
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Documentos Selecionados
                    </AlertDialogTitle>
                  </AlertDialogHeader>
                  <div>
                    <ul>
                      {documents.length > 0 ? (
                        documents.map((doc) => (
                          <li key={doc.idDocument}>{doc.name}</li>
                        ))
                      ) : (
                        <p>Nenhum documento selecionado.</p>
                      )}
                    </ul>
                  </div>
                  <AlertDialogFooter>
                    <AlertDialogCancel>Cancelar</AlertDialogCancel>
                    <AlertDialogAction>Confirmar</AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            </div>
          </div>
          <div>
            <BoxSelected documents={mockDocumentsSelected} />
          </div>
        </div>
        )}
        {selectedTab === "training" && <div className="p-4">ola deive2</div>}
        {selectedTab === "otherRequirements" && (
          <div className="p-4">ola deive3</div>
        )}
      </div>
    </div>
  );
}
