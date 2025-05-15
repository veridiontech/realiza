import {
  // ArrowLeft,
  // ArrowRight,
  Building2,
  ChevronRight,
  Files,
  MessageCircle,
  // Search,
  // Search,
  Settings2,
  University,
  Users,
  UsersRound,
} from "lucide-react";
import { Helmet } from "react-helmet-async";
import { Link, NavLink } from "react-router-dom";

import { EnterpriseResume } from "@/components/home/enterpriseResume";
// import { GraphicHomeLeft } from "@/components/home/graphicHomeLeft";
// import { GraphicHomeRight } from "@/components/home/graphicHomeRight";
import { ConformityGaugeChart } from "@/components/BIs/BisPageComponents/conformityChart";
import { Button } from "@/components/ui/button";
import { MainCard } from "@/components/quickActions/mainCard";
import { ActionButton } from "@/components/quickActions/actionButton";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { useClient } from "@/context/Client-Provider";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useBranch } from "@/context/Branch-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { toast } from "sonner";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { AddNewBranch } from "../branchs/modals/add-new-branch";
import { UltraSection } from "../ultra/ultra-branchs";
import { ScrollArea } from "@/components/ui/scroll-area";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { StatusDocumentChart } from "@/components/BIs/BisPageComponents/statusDocumentChat";
import { BranchesTable } from "./branchesTable";

const createUserClient = z.object({
  firstName: z.string().nonempty("Nome é obrigatório"),
  surname: z.string().nonempty("Sobrenome é obrigatório"),
  cellPhone: z.string().nonempty("Celular é obrigatório"),
  cpf: z.string().nonempty("Cpf é obrigatório"),
  email: z
    .string()
    .email("Formato de email inválido")
    .nonempty("Email é obrigatório"),
  position: z.string().nonempty("Seu cargo é obrigatório"),
  // password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
  role: z.string().default("ROLE_CLIENT_MANAGER"),
});

type CreateUserClient = z.infer<typeof createUserClient>;
export function Dashboard() {
  const [selectedTab, setSelectedTab] = useState("filiais");
  const [usersFromBranch, setUsersFromBranch] = useState([]);
  // const [searchBranches, setSearchBranches] = useState([])
  const { client } = useClient();
  const { selectedBranch } = useBranch();
  const { user } = useUser();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CreateUserClient>({
    resolver: zodResolver(createUserClient),
  });

  const onSubmitUserClient = async (data: CreateUserClient) => {
    const payload = {
      ...data,
      idEnterprise: selectedBranch?.idBranch,
      enterprise: "CLIENT",
      idUser: user?.idUser,
    };
    console.log("Enviando dados do novo usuário:", payload);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/user/manager/new-user`, payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      toast.success("Sucesso ao criar usuário");
      await getUsersFromBranch();
      reset();
    } catch (err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        console.log(mensagemBackend);
      }
      toast.error("Erro ao criar novo usuário");
      console.log(err);
    }
  };

  const getUsersFromBranch = async () => {
    // setLoading(true);
    // setError(null);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      const { content } = res.data;
      console.log("usuários da branch:", content);
      setUsersFromBranch(content);
      // setTotalPages(total);
    } catch (err) {
      console.error("erro ao buscar usuários:", err);
      // setError("Erro ao buscar usuários.");
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getUsersFromBranch();
    }
  }, [selectedBranch?.idBranch]);

  if (client?.isUltragaz) {
    return (
      <>
        <Helmet title="Dashboard" />
        <section className="relative bottom-[5vw] pb-10 pt-14">
          <div className="mx-auto max-w-7xl">
            <div className="flex flex-col gap-10">
              <EnterpriseResume />
              {client?.isUltragaz && <UltraSection />}
            </div>
            <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
              <StatusDocumentChart />
              <ConformityGaugeChart percentage={29} />
            </div>
            <div className="mt-5 w-full text-right">
              <NavLink to={`sistema/dashboard-details/${user?.idUser}`}>
                <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
                  Ver mais <ChevronRight />
                </Button>
              </NavLink>
            </div>

            <div className="pt-20">
              <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>

              <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
                <MainCard
                  title="Fornecedores"
                  value={324}
                  icon={<UsersRound size={28} />}
                />
                <MainCard
                  title="Mensagens"
                  value={12}
                  icon={<MessageCircle size={28} />}
                />
                <MainCard
                  title="Unidades"
                  value={4}
                  icon={<University size={28} />}
                />
                <MainCard
                  title="Contratos"
                  value={72}
                  icon={<Files size={28} />}
                />
              </div>

              <div className="mt-8 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
                <ActionButton
                  label="Adicionar fornecedor"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Enviar documento"
                  icon={<ChevronRight />}
                />
                <ActionButton label="Criar contato" icon={<ChevronRight />} />
                <ActionButton label="Gerar relatório" icon={<ChevronRight />} />
                <ActionButton
                  label="Atualizar documentos"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Consultar contratos"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Aprovar solicitações"
                  icon={<ChevronRight />}
                />
                <ActionButton
                  label="Editar colaboradores"
                  icon={<ChevronRight />}
                />
              </div>
            </div>
          </div>
        </section>
      </>
    );
  }

  return (
    <>
      <Helmet title="Dashboard" />
      <section className="dark:bg-primary-foreground pb-10 pt-14">
        <div className="container relative bottom-[6vw] mx-auto max-w-7xl">
          <div className="flex flex-col gap-10">
            <EnterpriseResume />
            <div className="flex items-center gap-5">
              <div className="h-[60vh] w-[95vw] rounded-lg border bg-white p-8 shadow-sm">
                <div className="flex flex-col gap-4">
                  <div>
                    <nav className="flex items-center justify-between">
                      <div className="flex items-center gap-5">
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${selectedTab === "filiais"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-sm"
                              : "text-realizaBlue border-realizaBlue border bg-white"
                            }`}
                          onClick={() => setSelectedTab("filiais")}
                        >
                          <Building2 /> Filiais
                        </Button>
                        <Button
                          variant={"ghost"}
                          className={`bg-realizaBlue px-4 py-2 transition-all duration-300${selectedTab === "usuarios"
                              ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                              : "text-realizaBlue border-realizaBlue border bg-white"
                            }`}
                          onClick={() => setSelectedTab("usuarios")}
                        >
                          <Users /> Usuários
                        </Button>
                      </div>
                      {selectedTab === "filiais" && <AddNewBranch />}
                      {selectedTab === "usuarios" && (
                        <Dialog>
                          <DialogTrigger asChild>
                            <Button className="bg-realizaBlue hidden md:block">
                              Criar Usuário
                            </Button>
                          </DialogTrigger>
                          <DialogTrigger asChild>
                            <Button className="bg-realizaBlue md:hidden">
                              +
                            </Button>
                          </DialogTrigger>
                          <DialogContent
                            style={{
                              backgroundImage: `url(${bgModalRealiza})`,
                            }}
                          >
                            <DialogHeader>
                              <DialogTitle className="flex flex-col gap-2 text-white md:flex-row md:items-center">
                                Criar usuário para o cliente{" "}
                                {client ? (
                                  <span className="font-semibold text-white">
                                    {client.corporateName}
                                  </span>
                                ) : (
                                  <span className="text-red-600">
                                    Nenhum cliente selecionado
                                  </span>
                                )}
                              </DialogTitle>
                            </DialogHeader>

                            <ScrollArea className="h-[40vh] p-3">
                              <form
                                onSubmit={handleSubmit(onSubmitUserClient)}
                                className="m-2 flex flex-col gap-5"
                              >
                                <div>
                                  <Label className="text-white">Nome</Label>
                                  <Input
                                    type="text"
                                    {...register("firstName")}
                                    placeholder="Digite seu nome"
                                  />
                                  {errors.firstName && (
                                    <span className="text-sm text-red-600">
                                      {errors.firstName.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">
                                    Sobrenome
                                  </Label>
                                  <Input
                                    type="text"
                                    {...register("surname")}
                                    placeholder="Digite seu sobrenome"
                                  />
                                  {errors.surname && (
                                    <span className="text-sm text-red-600">
                                      {errors.surname.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">Email</Label>
                                  <Input
                                    type="email"
                                    {...register("email")}
                                    placeholder="Digite seu e-mail"
                                  />
                                  {errors.email && (
                                    <span className="text-sm text-red-600">
                                      {errors.email.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">CPF</Label>
                                  <Input
                                    type="text"
                                    {...register("cpf")}
                                    placeholder="Digite seu CPF"
                                  />
                                  {errors.cpf && (
                                    <span className="text-sm text-red-600">
                                      {errors.cpf.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">Telefone</Label>
                                  <Input
                                    type="text"
                                    {...register("cellPhone")}
                                    placeholder="Digite seu telefone"
                                  />
                                  {errors.cellPhone && (
                                    <span className="text-sm text-red-600">
                                      {errors.cellPhone.message}
                                    </span>
                                  )}
                                </div>

                                <div>
                                  <Label className="text-white">Cargo</Label>
                                  <Input
                                    type="text"
                                    {...register("position")}
                                    placeholder="Digite seu cargo"
                                  />
                                  {errors.position && (
                                    <span className="text-sm text-red-600">
                                      {errors.position.message}
                                    </span>
                                  )}
                                </div>

                                {/* <div>
                                  <Label className="text-white">Senha</Label>
                                  <Input
                                    type="password"
                                    {...register("password")}
                                    placeholder="Digite sua senha"
                                  />
                                  {errors.password && (
                                    <span className="text-sm text-red-600">
                                      {errors.password.message}
                                    </span>
                                  )}
                                </div> */}

                                <div className="flex justify-end">
                                  <Button
                                    type="submit"
                                    className="bg-realizaBlue"
                                  >
                                    Criar
                                  </Button>
                                </div>
                              </form>
                            </ScrollArea>
                          </DialogContent>
                        </Dialog>
                      )}
                    </nav>
                  </div>
                  {selectedTab === "filiais" && (
                    <div>
                      <BranchesTable />
                    </div>
                  )}
                  {selectedTab === "usuarios" && (
                    <div>
                      <div>
                        <span>
                          {selectedBranch ? (
                            <div>
                              <p>
                                <strong>Filial:</strong> {selectedBranch.name}
                              </p>
                            </div>
                          ) : (
                            <div>
                              <p>
                                <strong>Filial:</strong> Filial não selecionada
                              </p>
                            </div>
                          )}
                        </span>
                      </div>

                      <div className="block space-y-4 md:hidden">
                        {usersFromBranch && usersFromBranch.length > 0 ? (
                          usersFromBranch.map((users: any) => (
                            <div
                              key={users.idUser}
                              className="rounded-lg border border-gray-300 bg-white p-4 shadow-sm"
                            >
                              <p className="text-sm font-semibold text-gray-700">
                                Nome:
                              </p>
                              <p className="text-realizaBlue mb-2">
                                {users.firstName}
                              </p>

                              <p className="text-sm font-semibold text-gray-700">
                                CPF:
                              </p>
                              <p className="mb-2 text-gray-800">{users.cpf}</p>

                              <Link
                                to={`/sistema/detailsEmployees/${users.idEmployee}`}
                              >
                                <button className="text-realizaBlue mt-2 flex items-center gap-1 hover:underline">
                                  <Settings2 size={18} /> Acessar
                                </button>
                              </Link>
                            </div>
                          ))
                        ) : (
                          <p className="text-center text-gray-600">
                            Nenhum colaborador encontrado
                          </p>
                        )}
                      </div>
                      <div className="hidden overflow-x-auto rounded-lg border bg-white p-4 shadow-lg md:block">
                        <table className="w-full border-collapse border border-gray-300">
                          <thead>
                            <tr>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                Nome
                              </th>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                CPF
                              </th>
                              <th className="border border-gray-300 px-4 py-2 text-start">
                                Ações
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {usersFromBranch && usersFromBranch.length > 0 ? (
                              usersFromBranch.map((users: any) => (
                                <tr key={users.idUser}>
                                  <td className="border border-gray-300 px-4 py-2">
                                    {users.firstName}
                                  </td>
                                  <td className="border border-gray-300 px-4 py-2">
                                    {users.cpf}
                                  </td>
                                  <td className="border border-gray-300 px-4 py-2">
                                    <Link
                                      to={`/sistema/detailsEmployees/${users.idEmployee}`}
                                    >
                                      <button className="text-realizaBlue flex items-center justify-center hover:underline">
                                        <Settings2 size={18} />
                                      </button>
                                    </Link>
                                  </td>
                                </tr>
                              ))
                            ) : (
                              <tr>
                                <td
                                  colSpan={3}
                                  className="border border-gray-300 px-4 py-2 text-center"
                                >
                                  Nenhum colaborador encontrado
                                </td>
                              </tr>
                            )}
                          </tbody>
                        </table>
                      </div>
                      {/* <Pagination
                                  currentPage={currentPage}
                                  totalPages={totalPages}
                                  onPageChange={handlePageChange}
                                /> */}
                    </div>
                  )}
                </div>
              </div>
              <ConformityGaugeChart percentage={71.29} />
            </div>
          </div>
          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
            <StatusDocumentChart />
          </div>
          <div className="mt-5 w-full text-right">
            <Link to={`/sistema/dashboard-details/${user?.idUser}`}>
              <Button className="hover:bg-realizaBlue dark:bg-primary bg-realizaBlue dark:text-white dark:hover:bg-blue-950">
                Ver mais <ChevronRight />
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </>
  );
}
