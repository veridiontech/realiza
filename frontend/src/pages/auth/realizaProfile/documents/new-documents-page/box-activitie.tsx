import { ScrollArea } from "@/components/ui/scroll-area";
import { useDocument } from "@/context/Document-provider";
import { propsActivities } from "@/types/interfaces";
import { Search } from "lucide-react";
import { useState } from "react";

interface BoxActivitiesProps {
  activities: propsActivities[];
  onSelectActivitie: (activitie: any) => void;
}

export function BoxActivities({
  activities,
  onSelectActivitie,
}: BoxActivitiesProps) {
  const [activitiesCheck, setCheckedActivities] = useState<string | null>(null); // Armazena o ID da atividade selecionada
  const { setActivitiesSelected } = useDocument();

  // Lógica de alternância de seleção (agora apenas uma atividade)
  const toggleCheckbox = (id: string, document: propsActivities) => {
    // Se a atividade já está selecionada, desmarque, caso contrário, marque
    setCheckedActivities((prev) => (prev === id ? null : id));

    setActivitiesSelected((prevSelectedActivity) => {
      if (prevSelectedActivity?.idActivity === id) {
        // Se já estiver selecionada, desmarque
        return null;
      } else {
        // Caso contrário, selecione essa atividade
        return document;
      }
    });
  };

  return (
    <div className="w-[35vw] border p-5 shadow-md">
      <div className="flex items-center gap-2 rounded-md border p-2">
        <Search />
        <input className="outline-none" placeholder="Pesquisar atividades" />
      </div>
      <ScrollArea className="h-[30vh]">
        <div>
          {activities.length > 0 ? (
            activities.map((activitie) => (
              <div
                key={activitie.idActivity}
                className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
                onClick={() => {
                  toggleCheckbox(activitie.idActivity, activitie);
                  onSelectActivitie(activitie); // garante que o pai atualiza
                }} // aqui que faltava!
              >
                <input
                  type="checkbox"
                  checked={activitiesCheck === activitie.idActivity}
                  readOnly // evita warning do React
                />
                <span>{activitie.title || "Documento"}</span>
              </div>
            ))
          ) : (
            <p className="text-sm text-gray-500">
              Nenhuma atividade encontrada.
            </p>
          )}
        </div>
      </ScrollArea>
    </div>
  );
}
