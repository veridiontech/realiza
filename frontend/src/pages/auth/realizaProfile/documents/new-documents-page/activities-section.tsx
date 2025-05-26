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
import { useDocument } from "@/context/Document-provider";
import { ActivitiesBox } from "../boxes-selected/activities";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Plus } from "lucide-react";
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
export function ActiviteSectionBox() {
  const [selectedTab, setSelectedTab] = useState("activities");
  const { setDocuments, setNonSelected } = useDocument();
  const { selectedBranch } = useBranch();
  const [isLoading, setIsLoading] = useState(false);

  const { register, handleSubmit } = useForm<ContractFormSchema>({
    resolver: zodResolver(contractFormSchema),
  });

  const handleClickToggle = () => {
    setDocuments([]);
    setNonSelected([]);
  };

  // useEffect(() => {

  // }, [])

  const createActivitie = async (data: ContractFormSchema) => {
    const token = localStorage.getItem("tokenClient");
    const payload = {
      ...data,
      idBranch: selectedBranch?.idBranch,
    };
    console.log(payload);
    
    setIsLoading(true);
    try {
      await axios.post(`${ip}/contract/activity`, payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Atividade criada com sucesso")
    } catch (err) {
      toast.error("Erro ao criar atividade")
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  };

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
        <Dialog>
          <DialogTrigger>
            <Button className="bg-realizaBlue">
              <Plus />
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
                    <Oval
                      visible={true}
                      height="80"
                      width="80"
                      color="#4fa94d"
                      ariaLabel="oval-loading"
                      wrapperStyle={{}}
                      wrapperClass=""
                    />
                  </Button>
                ) : (
                  <Button className="bg-realizaBlue"  disabled={!selectedBranch?.idBranch}>Criar atividade</Button>
                )}
              </form>
            </div>
          </DialogContent>
        </Dialog>
      </div>
      <div className="bg-white pt-24 shadow-md">
        {selectedTab === "activities" && <ActivitiesBox/>}
      </div>
    </div>
  );
}
