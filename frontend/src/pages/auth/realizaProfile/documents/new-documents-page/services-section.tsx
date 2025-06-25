import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Blocks, Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

type Service = {
  idServiceType: string;
  title: string;
  risk: "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH";
};

const createNewService = z.object({
  title: z.string().nonempty("É obrigatório o preenchimento do título"),
  risk: z.string().nonempty("É obrigatório a seleção de risco"),
});

type CreateNewService = z.infer<typeof createNewService>;

export function ServicesSection() {
  const { selectedBranch } = useBranch();
  const [services, setServices] = useState<Service[]>([]);
  const [editingTitleId, setEditingTitleId] = useState<string | null>(null);
  const [tempTitle, setTempTitle] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoadingCreate, setIsLoadingCreate] = useState(false);

  const tokenFromStorage = localStorage.getItem("tokenClient");

  const getServices = async () => {
    setIsLoading(true);
    try {
      const res = await axios.get(`${ip}/contract/service-type`, {
        params: {
          owner: "BRANCH",
          idOwner: selectedBranch?.idBranch,
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setServices(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  };

  // Atualiza só o título, mas envia título + risco juntos
  const updateTitle = async (idServiceType: string, title: string) => {
    if (!title.trim()) return alert("Título não pode ser vazio");

    const serviceAtual = services.find(
      (s) => s.idServiceType === idServiceType
    );
    if (!serviceAtual) return;

    const payload = {
      title,
      risk: serviceAtual.risk,
    };

    console.log("Enviando updateTitle:", payload);

    try {
      await axios.put(
        `${ip}/contract/service-type/branch/${idServiceType}`,
        payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      setServices((old) =>
        old.map((s) =>
          s.idServiceType === idServiceType ? { ...s, title } : s
        )
      );
      setEditingTitleId(null);
    } catch (err) {
      console.log(err);
    }
  };

  // Atualiza só o risco, mas envia título + risco juntos
  const updateRisk = async (idServiceType: string, risk: Service["risk"]) => {
    const serviceAtual = services.find(
      (s) => s.idServiceType === idServiceType
    );
    if (!serviceAtual) return;

    const payload = {
      title: serviceAtual.title,
      risk,
    };

    console.log("Enviando updateRisk:", payload);

    try {
      await axios.put(
        `${ip}/contract/service-type/branch/${idServiceType}`,
        payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      setServices((old) =>
        old.map((s) => (s.idServiceType === idServiceType ? { ...s, risk } : s))
      );
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getServices();
    }
  }, [selectedBranch?.idBranch]);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateNewService>({
    resolver: zodResolver(createNewService),
  });

  const createService = async (data: CreateNewService) => {
    setIsLoadingCreate(true);

    const payload = {
      ...data,
      branchId: selectedBranch?.idBranch,
    };
    console.log("enviando dados:", payload);

    try {
      await axios.post(
        `${ip}/contract/service-type/branch/${selectedBranch?.idBranch}`,
        payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      toast.success("Sucesso ao criar novo serviço");
      // Atualizar lista após criar novo serviço
      getServices();
    } catch (err: any) {
      console.log(err);
    } finally {
      setIsLoadingCreate(false);
    }
  };

  const deleteService = async () => {
    if (!serviceToDelete) return;

    try {
      await axios.delete(
        `${ip}/contract/service-type/${serviceToDelete}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      setServices((prevServices) =>
        prevServices.filter(
          (service) => service.idServiceType !== serviceToDelete
        )
      );

      toast.success("Serviço excluído com sucesso!");
    } catch (err) {
      console.log(err);
      toast.error("Erro ao excluir serviço.");
    } finally {
      closeDeleteModal(); // Fecha o modal após a operação
    }
  };

  // Filtra serviços pelo título com busca case-insensitive
  const filteredServices = services.filter((service) =>
    service.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [serviceToDelete, setServiceToDelete] = useState<string | null>(null);

  const openDeleteModal = (idServiceType: string) => {
    setServiceToDelete(idServiceType);
    setIsModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsModalOpen(false);
    setServiceToDelete(null);
  };

  return (
    <div className="relative bottom-[8vw] bg-white rounded-md shadow-md p-10 flex flex-col gap-5">
      <div>
        <h1 className="text-[30px]">Serviços</h1>
      </div>
      <div className="flex items-start gap-10">
        <div className="border border-neutral-300 rounded-md w-[50vw] p-3 shadow-md">
          <div className="flex items-center gap-1 rounded-sm border border-neutral-400 p-2">
            <Search />
            <input
              type="text"
              className="outline-none w-full"
              placeholder="Pesquise por um serviço"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          {isLoading ? (
            <div className="flex justify-center">
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
          ) : (
            <ScrollArea className="h-[50vh]">
              <div className="p-5 flex flex-col gap-4 ">
                {filteredServices.length > 0 ? (
                  filteredServices.map((service) => (
                    <div
                      key={service.idServiceType}
                      className="flex items-center gap-5 "
                    >
                      {editingTitleId === service.idServiceType ? (
                        <>
                          <input
                            value={tempTitle}
                            onChange={(e) => setTempTitle(e.target.value)}
                            onBlur={() =>
                              updateTitle(service.idServiceType, tempTitle)
                            }
                            autoFocus
                            className="border border-neutral-400 rounded p-1 flex-none"
                          />
                          <button
                            onClick={() => setEditingTitleId(null)}
                            className="text-gray-500"
                          >
                            Cancelar
                          </button>
                        </>
                      ) : (
                        <li
                          onClick={() => {
                            setEditingTitleId(service.idServiceType);
                            setTempTitle(service.title);
                          }}
                          className="cursor-pointer"
                          title="Clique para editar título"
                        >
                          {service.title}
                        </li>
                      )}

                      <span>-</span>
                      <button
                        type="button"
                        onClick={() => openDeleteModal(service.idServiceType)}
                      >
                        <Trash2 height="15" width="15" color="#9d2626" />
                      </button>

                      <select
                        value={service.risk}
                        onChange={(e) =>
                          updateRisk(
                            service.idServiceType,
                            e.target.value as Service["risk"]
                          )
                        }
                        className="border border-neutral-400 rounded-sm p-1"
                      >
                        <option value="LOW">BAIXO</option>
                        <option value="MEDIUM">MÉDIO</option>
                        <option value="HIGH">ALTO</option>
                        <option value="VERY_HIGH">MUITO ALTO</option>
                      </select>
                    </div>
                  ))
                ) : (
                  <p className="text-center text-gray-500">
                    Nenhum serviço encontrado.
                  </p>
                )}
              </div>
            </ScrollArea>
          )}
        </div>
        <div className="border border-neutral-300 p-5 rounded-md w-[30vw]">
          <h2>Cadastrar novo serviço</h2>
          <div>
            <form
              onSubmit={handleSubmit(createService)}
              className="flex flex-col gap-5"
            >
              <div className="flex flex-col gap-1">
                <Label>Titulo do serviço</Label>
                <Input
                  {...register("title")}
                  className="border border-neutral-400"
                />
                {errors.title && <span>{errors.title.message}</span>}
              </div>
              <div className="flex flex-col gap-1">
                <Label>Risco</Label>
                <select
                  {...register("risk")}
                  defaultValue={""}
                  className="border border-neutral-400 p-2 rounded-md"
                >
                  <option value="" disabled>
                    Selecione um risco
                  </option>
                  <option value="LOW">BAIXO</option>
                  <option value="MEDIUM">MEDIO</option>
                  <option value="HIGH">ALTO</option>
                  <option value="VERY_HIGH">MUITO ALTO</option>
                </select>
                {errors.risk && <span>{errors.risk.message}</span>}
              </div>
              {isLoadingCreate ? (
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
                <Button className="bg-realizaBlue" type="submit">
                  Cadastrar novo serviço
                </Button>
              )}
            </form>
            {isModalOpen && (
              <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
                <div className="bg-white p-6 rounded-md w-96">
                  <h2 className="text-lg font-bold">Confirmação</h2>
                  <p>Tem certeza que deseja excluir este serviço?</p>
                  <div className="flex justify-between mt-4">
                    <button
                      onClick={closeDeleteModal}
                      className="bg-gray-300 p-2 rounded-md"
                    >
                      Cancelar
                    </button>
                    <button
                      onClick={deleteService}
                      className="bg-red-500 text-white p-2 rounded-md"
                    >
                      Excluir
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
