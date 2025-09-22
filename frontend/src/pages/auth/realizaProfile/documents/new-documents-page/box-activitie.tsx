import { ScrollArea } from "@/components/ui/scroll-area";
import { useDocument } from "@/context/Document-provider";
import { propsActivities } from "@/types/interfaces";
import { Search } from "lucide-react";
import { useState } from "react";
import { Blocks } from "react-loader-spinner";

interface BoxActivitiesProps {
  activities: propsActivities[];
  onSelectActivitie: (activitie: any) => void;
  isLoading: boolean;
}

export function BoxActivities({
  activities,
  onSelectActivitie,
  isLoading,
}: BoxActivitiesProps) {
  const [activitiesCheck, setCheckedActivities] = useState<string | null>(null);
  const { setActivitiesSelected } = useDocument();
  // 1. Criando o estado para o termo de busca
  const [searchTerm, setSearchTerm] = useState<string>("");

  const toggleCheckbox = (id: string, document: propsActivities) => {
    setCheckedActivities((prev) => (prev === id ? null : id));
    setActivitiesSelected((prevSelectedActivity) => {
      if (prevSelectedActivity?.idActivity === id) {
        return null;
      } else {
        return document;
      }
    });
  };

  // 2. Filtrando as atividades
  const filteredActivities = activities.filter((activitie) =>
    // Garante que a busca é case-insensitive (não diferencia maiúsculas de minúsculas)
    // E que o título da atividade existe antes de tentar convertê-lo
    activitie.title?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (isLoading) {
    return (
      <div className="border p-5 shadow-md w-[35vw]">
        <div className="flex items-center gap-2 rounded-md border p-2">
          <Search />
          {/* Adicionando o valor e o manipulador de eventos ao input */}
          <input
            className="outline-none w-full"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="h-[30vh] flex items-center justify-center">
          <Blocks
            height="80"
            width="80"
            color="#4fa94d"
            ariaLabel="blocks-loading"
            wrapperStyle={{}}
            wrapperClass="blocks-wrapper"
            visible={true}
          />
        </div>
      </div>
    );
  }

  return (
    <div className="w-[35vw] border p-5 shadow-md">
      <div className="flex items-center gap-2 rounded-md border p-2">
        <Search />
        {/* 3. Adicionando o valor e o manipulador de eventos (onChange) ao input */}
        <input
          className="outline-none w-full"
          placeholder="Pesquisar atividades"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>
      <ScrollArea className="h-[30vh]">
        <div>
          {/* 4. Mapeando a lista filtrada, não a original */}
          {filteredActivities.length > 0 ? (
            filteredActivities.map((activitie) => (
              <div
                key={activitie.idActivity}
                className="flex cursor-pointer items-center gap-2 rounded-sm p-1 hover:bg-gray-200"
                onClick={() => {
                  toggleCheckbox(activitie.idActivity, activitie);
                  onSelectActivitie(activitie);
                }}
              >
                <input
                  type="radio"
                  checked={activitiesCheck === activitie.idActivity}
                  readOnly
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