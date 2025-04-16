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
import { BoxNonSelected } from "../new-documents-page/box-non-selected";
import { propsDocument } from "@/types/interfaces";
import { BoxSelected } from "../new-documents-page/box-selected";
import { useDocument } from "@/context/Document-provider";

export function TrainingBox() {
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
    <div className="flex items-center justify-center gap-10 p-10">
      <div>
        <BoxNonSelected documents={mockDocumentsNonSelected} />
      </div>
      <div className="flex flex-col gap-5">
        <div>
          <AlertDialog>
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-4 transition-all duration-300 ${nonSelected.length === 0 ? "cursor-not-allowed bg-gray-300 text-gray-500" : "bg-realizaBlue text-white"}`}
              disabled={nonSelected.length === 0}
            >
              Confirmar Seleção
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
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
            <AlertDialogTrigger
              className={`w-[10vw] rounded-md p-3 transition-all duration-300 ${documents.length === 0 ? "cursor-not-allowed bg-red-300 text-red-500" : "bg-red-600 text-white"}`}
              disabled={documents.length === 0}
            >
              Confirmar Remoção
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Documentos Selecionados</AlertDialogTitle>
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
  );
}
