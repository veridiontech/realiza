import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Pencil } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useClient } from "@/context/Client-Provider";
import { useBranch } from "@/context/Branch-provider";
import { toast } from "sonner";
import bgModalRealiza from "@/assets/modalBG.jpeg";

const editModalEnterpriseSchema = z.object({
    cnpj: z.string(),
    cep: z.string().nonempty("CEP é obrigatório"),
    corporateName: z.string(),
    tradeName: z.string(),
    email: z.string(),
    telephone: z.string().nonempty("Celular é obrigatório"),
    state: z.string(),
    city: z.string(),
    address: z.string(),
    number: z.string(),
});

type EditModalEnterpriseSchema = z.infer<typeof editModalEnterpriseSchema>;

export function EditModalEnterprise() {
    const [cepValue, setCepValue] = useState("");
    const { client, refreshClient, refreshClients } = useClient();
    const { selectedBranch } = useBranch();
    const [isOpen, setIsOpen] = useState(false);
    const [phoneValue, setPhoneValue] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        handleSubmit,
        setValue,
        getValues,
        formState: { errors },
        reset
    } = useForm<EditModalEnterpriseSchema>({
        resolver: zodResolver(editModalEnterpriseSchema),
    });

    const formatCEP = (value: string) => {
        return value
            .replace(/\D/g, "")
            .replace(/(\d{5})(\d)/, "$1-$2")
            .slice(0, 9);
    };

    const getDatasEnterprise = async () => {
        const entityId = selectedBranch?.idBranch || client?.idClient;
        const entityType = selectedBranch ? 'branch' : 'client';
        
        if (!entityId) {
            reset();
            return;
        }

        try {
            const tokenFromStorage = localStorage.getItem("tokenClient");
            const endpoint = selectedBranch ? `${ip}/branch/${entityId}` : `${ip}/client/${entityId}`;
            
            console.log(`[DEBUG] Buscando dados da entidade: ${entityType}`);
            console.log(`[DEBUG] Endpoint: ${endpoint}`);

            const res = await axios.get(endpoint, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });

            const data = res.data;
            console.log(`[DEBUG] Dados retornados da API (${entityType}):`, data);
            const formCnpj = data.cnpj || client?.cnpj || "";

            const formCorporateName = data.corporateName || data.name || client?.corporateName || "";
            const formTradeName = data.tradeName || client?.tradeName || "";
            const formEmail = data.email || client?.email || ""; 
            
            setValue("cnpj", formCnpj);
            setValue("corporateName", formCorporateName);
            setValue("tradeName", formTradeName);
            setValue("email", formEmail);
            setValue("telephone", data.telephone || "");
            setValue("cep", data.cep || "");
            setValue("state", data.state || "");
            setValue("city", data.city || "");
            setValue("address", data.address || data.adress || ""); 
            setValue("number", data.number || "");
            setCepValue(formatCEP(data.cep || ""));
            setPhoneValue(data.telephone || ""); 
            
        } catch (err) {
            console.error(`[ERRO] Não foi possível recuperar os dados da ${entityType}`, err);
            reset(); 
        }
    };

    const getEditHistory = async () => {
        if (!client?.idClient) return;
        
        try {
            const tokenFromStorage = localStorage.getItem("tokenClient");
            await axios.get(`${ip}/client/${client.idClient}/history`, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
        } catch (err) {
            console.error("[ERRO] Erro ao buscar histórico de edições", err);
        }
    };
    
    const onSubmit = async (data: EditModalEnterpriseSchema) => {
        setIsLoading(true);
        const payload = {
            ...data,
            cnpj: selectedBranch?.cnpj || client?.cnpj || data.cnpj 
        };
        
        console.log("[DEBUG] Payload de Edição Enviado (FINAL):", payload);

        try {
            const tokenFromStorage = localStorage.getItem("tokenClient");
            await axios.put(`${ip}/client/${client?.idClient}`, payload, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            if (client) {
                await refreshClient(client.idClient);
                await refreshClients();
            }
            
            toast.success("Sucesso ao atualizar perfil");
            setIsOpen(false);
        } catch (err) {
            console.error("[ERRO] Erro ao atualizar perfil (Requisição Falhou):", err);
            toast.error("Erro ao atualizar perfil, tente novamente.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (isOpen && (client || selectedBranch)) {
            getDatasEnterprise();
            getEditHistory();
        }
        if (!isOpen) {
             reset();
        }
    }, [isOpen, client, selectedBranch]);

    useEffect(() => {
        const rawCEP = getValues("cep") || "";
        setCepValue(formatCEP(rawCEP));
    }, [getValues]);

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
                <Button className="border border-gray-300 bg-[#34495E] text-white hover:bg-gray-100 px-14 py-2 flex items-center gap-2 text-sm font-medium ">
                    <Pencil size={12} />
                    Editar perfil
                </Button>
            </DialogTrigger>

            <DialogTrigger asChild>
                <Button className="md:hidden bg-realizaBlue">
                    Editar perfil empresarial
                </Button>
            </DialogTrigger>

            <DialogContent style={{ backgroundImage: `url(${bgModalRealiza})` }}>
                <DialogHeader>
                    <DialogTitle className="text-white">
                        Editar {selectedBranch ? `Filial: ${selectedBranch.name}` : 'Empresa'}
                    </DialogTitle>
                </DialogHeader>

                <ScrollArea className="h-[60vh] pr-5">
                    <form
                        onSubmit={handleSubmit(onSubmit)}
                        className="flex flex-col gap-4"
                    >
                        <div className="text-white">
                            <Label>CNPJ</Label>
                            <div>{selectedBranch?.cnpj || client?.cnpj}</div> 
                        </div>

                        <div>
                            <Label className="text-white">Nome da empresa</Label>
                            <Input
                                placeholder="Digite o nome da empresa"
                                {...register("corporateName")}
                            />
                        </div>

                        <div>
                            <Label className="text-white">Nome fantasia</Label>
                            <Input
                                placeholder="Digite o nome fantasia"
                                {...register("tradeName")}
                            />
                        </div>

                        <div>
                            <Label className="text-white">Email corporativo</Label>
                            <Input
                                placeholder="Digite o email corporativo"
                                {...register("email")}
                            />
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label className="text-white">Telefone</Label>
                            <Input
                                type="text"
                                value={phoneValue}
                                {...register("telephone")}
                                onChange={(e) => {
                                    setPhoneValue(e.target.value);
                                }}
                                placeholder="(00) 00000-0000"
                                maxLength={15}
                            />
                            {errors.telephone && (
                                <span className="text-sm text-red-600">
                                    {errors.telephone.message}
                                </span>
                            )}
                        </div>

                        <div>
                            <Label className="text-white">CEP</Label>
                            <Input
                                value={cepValue}
                                {...register("cep")}
                                onChange={(e) => {
                                    setCepValue(formatCEP(e.target.value)); 
                                }}
                                placeholder="00000-000"
                                maxLength={9}
                            />
                            {errors.cep && (
                                <span className="text-sm text-red-600">
                                    {errors.cep.message}
                                </span>
                            )}
                        </div>

                        <div>
                            <Label className="text-white">Estado</Label>
                            <Input
                                {...register("state")}
                                readOnly
                                placeholder="Digite o estado"
                            />
                        </div>

                        <div>
                            <Label className="text-white">Cidade</Label>
                            <Input
                                {...register("city")}
                                readOnly
                                placeholder="Digite a cidade"
                            />
                        </div>

                        <div className="flex flex-col md:flex-row gap-3">
                            <div className="w-full md:w-[80%]">
                                <Label className="text-white">Endereço</Label>
                                <Input
                                    {...register("address")}
                                    readOnly
                                    placeholder="Digite o endereço"
                                />
                            </div>
                            <div className="w-full md:w-[20%]">
                                <Label className="text-white">Número</Label>
                                <Input {...register("number")} />
                            </div>
                        </div>
                        <Button className="bg-realizaBlue w-full md:w-auto" type="submit" disabled={isLoading}>
                            {isLoading ? "Carregando..." : "Confirmar edição"}
                        </Button>
                    </form>
                </ScrollArea>
            </DialogContent>
        </Dialog>
    );
}