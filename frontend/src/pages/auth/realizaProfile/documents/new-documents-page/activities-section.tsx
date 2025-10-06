import { Button } from "@/components/ui/button";
import { useState, useMemo } from "react";
import { useDocument } from "@/context/Document-provider";
import { ActivitiesBox } from "../boxes-selected/activities";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";

const contractFormSchema = z.object({
  title: z.string(),
  risk: z.string(),
});

type ContractFormSchema = z.infer<typeof contractFormSchema>;

interface Activity {
  id: string; 
  title: string;
}

export function ActiviteSectionBox() {
  const [selectedTab, setSelectedTab] = useState("activities");
  const { setDocuments, setNonSelected } = useDocument();
  const { selectedBranch } = useBranch();
  const [isLoading, setIsLoading] = useState(false);
  const [isAllocateModalOpen, setIsAllocateModalOpen] = useState(false);
  const [isAllocating, setIsAllocating] = useState(false);
  const [isLoadingActivities, setIsLoadingActivities] = useState(false);
  const [availableActivities, setAvailableActivities] = useState<Activity[]>([]);
  const [selectedActivityIds, setSelectedActivityIds] = useState<string[]>([]);
  const [refreshActivitiesKey, setRefreshActivitiesKey] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  
  const { register, handleSubmit } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const token = localStorage.getItem("tokenClient");

  const handleClickToggle = () => {
    setDocuments([]);
    setNonSelected([]);
  };

  const createActivitie = async (data: ContractFormSchema) => {
    const payload = {
      ...data,
      idBranch: selectedBranch?.idBranch,
    };
    
    setIsLoading(true);
    try {
      await axios.post(`${ip}/contract/activity`, payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Atividade criada com sucesso");
      setRefreshActivitiesKey(prev => prev + 1);
    } catch (err) {
      toast.error("Erro ao criar atividade");
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAvailableActivities = async () => {
    if (!token) return;

    setIsLoadingActivities(true);
    try {
      const params = {
        page: 0,
        size: 50,
        sort: 'title',
        direction: 'ASC'
      };
      
      const response = await axios.get(`${ip}/contract/activity-repo`, { 
        params: params,
        headers: { Authorization: `Bearer ${token}` },
      });
      
      const activitiesContent = response.data?.content || [];
      
      const mappedActivities = activitiesContent.map((item: any) => ({
        id: item.idActivity, 
        title: item.title
      }));
      
      setAvailableActivities(mappedActivities); 
    } catch (err) {
      toast.error("Erro ao carregar lista de atividades disponíveis.");
      setAvailableActivities([]);
    } finally {
      setIsLoadingActivities(false);
    }
  };

  const handleSelectActivity = (activityId: string) => {
    setSelectedActivityIds((prev) =>
      prev.includes(activityId)
        ? prev.filter((id) => id !== activityId)
        : [...prev, activityId]
    );
  };

  const allocateActivities = async () => {
    if (!selectedBranch?.idBranch) {
      toast.error("Erro: Nenhuma filial selecionada.");
      return;
    }

    if (selectedActivityIds.length === 0) {
      toast.error("Selecione pelo menos uma atividade para alocar.");
      return;
    }

    const payload = {
      activityIds: selectedActivityIds,
      branchIds: [selectedBranch.idBranch],
    };
    
    console.log("PAYLOAD ENVIADO PARA ALOCAÇÃO:", payload);

    setIsAllocating(true);
    try {
      await axios.post(`${ip}/contract/activity/add-to-branches`, payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      
      toast.success(`Atividades alocadas com sucesso à filial ${selectedBranch.name}!`);
      setSelectedActivityIds([]);
      setIsAllocateModalOpen(false);
      setRefreshActivitiesKey(prev => prev + 1);
      
    } catch (err) {
      toast.error("Erro ao alocar atividades. Verifique o console para detalhes da requisição.");
      console.error("Detalhes do erro:", err);
    } finally {
      setIsAllocating(false);
    }
  };

  const handleModalOpenChange = (open: boolean) => {
    setIsAllocateModalOpen(open);
    if (open) {
      fetchAvailableActivities();
      setSelectedActivityIds([]);
      setSearchTerm("");
    }
  };

  const filteredActivities = useMemo(() => {
    if (!searchTerm) {
      return availableActivities;
    }

    const lowerCaseSearchTerm = searchTerm.toLowerCase();

    return availableActivities.filter(activity => 
      activity.title.toLowerCase().includes(lowerCaseSearchTerm)
    );
  }, [availableActivities, searchTerm]);

  const isAllocationDisabled = isAllocating || selectedActivityIds.length === 0 || !selectedBranch?.idBranch;

  return (
    <div className="relative bottom-[8vw]">
      <div className="absolute left-0 right-0 top-0 z-10 gap-2 rounded-lg bg-white p-5 shadow-md md:flex justify-between">
        <Button
          variant={"ghost"}
          className={`px-4 py-2 transition-all duration-300 ${
            selectedTab === "activities"
              ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
              : "text-realizaBlue bg-white"
          }`}
          onClick={() => {
            setSelectedTab("activities"), handleClickToggle();
          }}
        >
          Atividades
        </Button>
        <div className="flex gap-2">
          <Dialog>
            <DialogTrigger>
              <Button className="bg-realizaBlue hidden">
                Criar nova atividade
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>
                  Criar atividade para filial{" "}
                  {selectedBranch?.name ? selectedBranch.name : "não selecionada"}
                </DialogTitle>
              </DialogHeader>
              <div>
                <form
                  onSubmit={handleSubmit(createActivitie)}
                  className="flex flex-col gap-2"
                >
                  <div>
                    <Label>Título da atividade</Label>
                    <Input
                      {...register("title")}
                      className="border border-neutral-400"
                    />
                  </div>
                  <div className="flex flex-col gap-1">
                    <Label>Risco</Label>
                    <select
                      className="border border-neutral-400 rounded-md p-2"
                      defaultValue={""}
                      {...register("risk")}
                    >
                      <option value="" disabled>
                        Selecione um risco
                      </option>
                      <option value="LOW">BAIXO</option>
                      <option value="MEDIUM">MÉDIO</option>
                      <option value="HIGH">ALTO</option>
                      <option value="VERY_HIGH">MUITO ALTO</option>
                    </select>
                  </div>
                  {isLoading ? (
                    <Button className="bg-realizaBlue">
                      <Oval visible={true} height="20" width="20" color="#fff" ariaLabel="oval-loading" />
                    </Button>
                  ) : (
                    <Button className="bg-realizaBlue"  disabled={!selectedBranch?.idBranch}>Criar atividade</Button>
                  )}
                </form>
              </div>
            </DialogContent>
          </Dialog>

          <Dialog open={isAllocateModalOpen} onOpenChange={handleModalOpenChange}>
            <DialogTrigger asChild>
              <Button className="bg-realizaBlue" disabled={!selectedBranch?.idBranch}>
                Alocar atividade
              </Button>
            </DialogTrigger>
            <DialogContent className="max-h-[80vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>
                  Alocar Atividades à Filial: {selectedBranch?.name || "Nenhuma"}
                </DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                {isLoadingActivities ? (
                  <div className="flex justify-center p-8">
                    <Oval height="40" width="40" color="#000" ariaLabel="loading-activities" />
                  </div>
                ) : (
                  Array.isArray(filteredActivities) && filteredActivities.length === 0
                ) ? (
                  <p className="text-center text-gray-500">
                    {searchTerm ? "Nenhuma atividade encontrada com este termo." : "Nenhuma atividade disponível para alocação."}
                  </p>
                ) : (
                  <div className="flex flex-col gap-2 max-h-60 overflow-y-auto border p-2 rounded-md">
                    <Input 
                        placeholder="Pesquisar atividades..." 
                        className="mb-2 border border-neutral-400"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    {Array.isArray(filteredActivities) && filteredActivities.map((activity) => (
                      <div
                        key={activity.id}
                        className={`flex items-center justify-between p-2 rounded-md cursor-pointer transition-colors ${
                          selectedActivityIds.includes(activity.id)
                            ? "bg-realizaBlue text-white font-semibold"
                            : "bg-gray-100 hover:bg-gray-200"
                        }`}
                        onClick={() => handleSelectActivity(activity.id)}
                      >
                        <span>{activity.title}</span>
                        {selectedActivityIds.includes(activity.id) && (
                          <span className="text-lg">✓</span>
                        )}
                      </div>
                    ))}
                  </div>
                )}

                <Button
                  className="w-full bg-realizaBlue"
                  onClick={allocateActivities}
                  disabled={isAllocationDisabled}
                >
                  {isAllocating ? (
                    <Oval visible={true} height="20" width="20" color="#fff" ariaLabel="oval-loading" />
                  ) : (
                    `Alocar ${selectedActivityIds.length} Atividade(s)`
                  )}
                </Button>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>
      
      <div className="bg-white pt-24 shadow-md">
        {selectedTab === "activities" && <ActivitiesBox key={refreshActivitiesKey} />}
      </div>
    </div>
  );
}