import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

interface Employee {
    idEmployee: string;
    name: string;
    surname: string;
}

interface ManageEmployeesModalProps {
    idProvider: string | null;
}

export function ManageEmployeesModal({ idProvider }: ManageEmployeesModalProps) {
    const [activeTab, setActiveTab] = useState<"alocar" | "desalocar">("alocar");
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const getEmployee = async () => {
        if (!idProvider) return;
        setIsLoading(true);
        try {
            const res = await axios.get(
                `${ip}/employee?idSearch=${idProvider}&enterprise=SUPPLIER`,
            );
            console.log("employees:", res.data.content);
            setEmployees(res.data.content);
        } catch (error) {
            console.log("Erro ao buscar colaboradores:", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (idProvider) {
            getEmployee();
        }
    }, [idProvider]);

    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button className="hidden md:block bg-realizaBlue border border-white rounded-md">
                    Gerenciar colaboradores
                </Button>
            </DialogTrigger>
            <DialogTrigger asChild>
                <Button className="md:hidden bg-realizaBlue">⚙️</Button>
            </DialogTrigger>

            <DialogContent
                style={{ backgroundImage: `url(${bgModalRealiza})` }}
                className="max-w-[90vw] sm:max-w-[45vw] md:max-w-[45vw]"
            >
                <DialogHeader>
                    <DialogTitle className="text-white">Gerenciar colaboradores</DialogTitle>
                </DialogHeader>

                <ScrollArea className="h-[75vh]">
                    {/* Tabs */}
                    <div className="flex gap-2 bg-[#1F2A40] rounded-md p-1 w-fit mb-6">
                        <button
                            onClick={() => setActiveTab("alocar")}
                            className={`px-5 py-2 rounded-md text-sm font-semibold transition-all duration-300 ${activeTab === "alocar"
                                    ? "bg-white text-[#1F2A40]"
                                    : "text-white opacity-60 hover:opacity-100"
                                }`}
                        >
                            Alocar funcionário
                        </button>
                        <button
                            onClick={() => setActiveTab("desalocar")}
                            className={`px-5 py-2 rounded-md text-sm font-semibold transition-all duration-300 ${activeTab === "desalocar"
                                    ? "bg-white text-[#1F2A40]"
                                    : "text-white opacity-60 hover:opacity-100"
                                }`}
                        >
                            Desalocar funcionário
                        </button>
                    </div>

                    {/* ALOCAÇÃO */}
                    {activeTab === "alocar" && (
                        <div className="text-white space-y-2">
                            {isLoading ? (
                                <p>Carregando colaboradores...</p>
                            ) : employees.length > 0 ? (
                                employees.map((emp) => (
                                    <div key={emp.idEmployee} className="bg-[#2E3C57] p-2 rounded-md">
                                        {emp.name} {emp.surname}
                                    </div>
                                ))
                            ) : (
                                <p>Nenhum colaborador encontrado.</p>
                            )}
                        </div>
                    )}

                    {/* DESALOCAÇÃO */}
                    {activeTab === "desalocar" && (
                        <div className="text-white">
                            <p>Em breve: desalocar funcionários...</p>
                        </div>
                    )}
                </ScrollArea>
            </DialogContent>
        </Dialog>
    );
}
