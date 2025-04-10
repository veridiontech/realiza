import {
  Briefcase,
  ChevronRight,
  Clock,
  Files,
  LineChartIcon,
  MessageCircle,
  Settings2,
  University,
  User,
  UsersRound,
} from "lucide-react";
import { Helmet } from "react-helmet-async";
import { Link, NavLink } from "react-router-dom";

import { EnterpriseResume } from "@/components/home/enterpriseResume";
import { GraphicHomeLeft } from "@/components/home/graphicHomeLeft";
import { GraphicHomeRight } from "@/components/home/graphicHomeRight";
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
import { Label } from "recharts";
import { Input } from "@/components/ui/input";

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
  password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
  role: z.string().default("ROLE_CLIENT_MANAGER"),
});

type CreateUserClient = z.infer<typeof createUserClient>;
export function Dashboard() {
  const [selectedTab, setSelectedTab] = useState("filiais");
  const [branches, setBranches] = useState([]);
  const [usersFromBranch, setUsersFromBranch] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredBranches, setFilteredBranches] = useState([]);
  const { client } = useClient();
  const { selectedBranch } = useBranch();
  const { user } = useUser();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateUserClient>({
    resolver: zodResolver(createUserClient),
  });

  const onSubmitUserClient = async (data: CreateUserClient) => {
    const payload = {
      ...data,
      branch: selectedBranch?.idBranch,
      idUser: user?.idUser,
    };
    console.log("Enviando dados do novo usuário:", payload);
    try {
      await axios.post(`${ip}/user/client`, payload);
      toast.success("Sucesso ao criar usuário");
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
      const res = await axios.get(
        `${ip}/user/client/filtered-client?idSearch=${selectedBranch?.idBranch}`,
      );
      const { content, } = res.data;
      console.log("usuários da branch:", content);
      setUsersFromBranch(content);
      // setTotalPages(total);
    } catch (err) {
      console.error("erro ao buscar usuários:", err);
      // setError("Erro ao buscar usuários.");
    } finally {
      // setLoading(false);
    }
  };

  const fetchBranches = async () => {
    try {
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
      );
      const { content } = response.data;
      setBranches(content);
      setFilteredBranches(content);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const term = event.target.value.toLowerCase();
    setSearchTerm(term);
    const filtered = branches.filter(
      (branch: any) =>
        branch.name.toLowerCase().includes(term) || branch.cnpj.includes(term),
    );
    setFilteredBranches(filtered);
  };

  useEffect(() => {
    console.log("id da branch:", selectedBranch);

    if (selectedBranch?.idBranch) {
      getUsersFromBranch();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    if (client?.idClient) {
      fetchBranches();
    }
  }, [client?.idClient]);

  return (
    <>
      <Helmet title="Dashboard" />
      <section className="dark:bg-primary-foreground bg-zinc-100 pb-10 pt-14">
        <div className="container mx-auto max-w-7xl">
          <div className="flex flex-col gap-10">
            <EnterpriseResume />
            <div className="rounded-lg border bg-white p-8 shadow-lg">
              <div className="flex flex-col gap-4">
                <div>
                  <nav className="flex items-center justify-between">
                    <div>
                      <Button
                        variant={"ghost"}
                        className={`bg-realizaBlue px-4 py-2 transition-all duration-300 ${
                          selectedTab === "filiais"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                        }`}
                        onClick={() => setSelectedTab("filiais")}
                      >
                        Filiais
                      </Button>
                      <Button
                        variant={"ghost"}
                        className={`bg-realizaBlue px-4 py-2 transition-all duration-300${
                          selectedTab === "usuarios"
                            ? "bg-realizaBlue scale-110 font-bold text-white shadow-lg"
                            : "text-realizaBlue bg-white"
                        }`}
                        onClick={() => setSelectedTab("usuarios")}
                      >
                        Usuários
                      </Button>
                    </div>
                    {selectedTab === "usuarios" && (
                      <div>
                        <Dialog>
                          <DialogTrigger asChild>
                            <Button className="bg-realizaBlue">+</Button>
                          </DialogTrigger>
                          <DialogContent className="max-w-[30vw]">
                            <DialogHeader>
                              <DialogTitle className="flex items-center gap-2">
                                Criar usuário para o cliente{" "}
                                {client ? (
                                  <p>{client.corporateName}</p>
                                ) : (
                                  <p>Nenhum cliente selecionado</p>
                                )}
                              </DialogTitle>
                            </DialogHeader>
                            <form onSubmit={handleSubmit(onSubmitUserClient)}>
                              <div className="flex flex-col gap-2">
                                <div>
                                  <Label>Nome</Label>
                                  <Input
                                    type="text"
                                    {...register("firstName")}
                                  />
                                  {errors.firstName && (
                                    <span className="text-red-600">
                                      {errors.firstName.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>Sobrenome</Label>
                                  <Input type="text" {...register("surname")} />
                                  {errors.surname && (
                                    <span className="text-red-600">
                                      {errors.surname.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>Email</Label>
                                  <Input type="text" {...register("email")} />
                                  {errors.email && (
                                    <span className="text-red-600">
                                      {errors.email.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>CPF</Label>
                                  <Input type="text" {...register("cpf")} />
                                  {errors.cpf && (
                                    <span className="text-red-600">
                                      {errors.cpf.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>Telefone</Label>
                                  <Input
                                    type="text"
                                    {...register("cellPhone")}
                                  />
                                  {errors.cellPhone && (
                                    <span className="text-red-600">
                                      {errors.cellPhone.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>Cargo</Label>
                                  <Input
                                    type="text"
                                    {...register("position")}
                                  />
                                  {errors.position && (
                                    <span className="text-red-600">
                                      {errors.position.message}
                                    </span>
                                  )}
                                </div>
                                <div>
                                  <Label>Senha</Label>
                                  <Input
                                    type="text"
                                    {...register("password")}
                                  />
                                  {errors.password && (
                                    <span className="text-red-600">
                                      {errors.password.message}
                                    </span>
                                  )}
                                </div>
                                <Button
                                  className="bg-realizaBlue"
                                  type="submit"
                                >
                                  Criar
                                </Button>
                              </div>
                            </form>
                          </DialogContent>
                        </Dialog>
                      </div>
                    )}
                  </nav>
                </div>
                {selectedTab === "filiais" && (
                  <div className="flex flex-col gap-5">
                    <div className="flex items-center gap-4">
                      <div className="text-sm font-semibold text-sky-900">
                        Buscar Filial:
                      </div>
                      <input
                        type="text"
                        value={searchTerm}
                        onChange={handleSearch}
                        placeholder="Pesquisar filiais"
                        className="w-64 rounded-md border p-2"
                      />
                    </div>

                    <div className="rounded-lg border bg-white p-8 shadow-lg">
                      <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                        <thead>
                          <tr>
                            <th className="border border-gray-300 px-4 py-2 text-start">
                              Filiais
                            </th>
                            <th className="border">CNPJ</th>
                          </tr>
                        </thead>
                        <tbody>
                          {filteredBranches && filteredBranches.length > 0 ? (
                            filteredBranches.map((branch: any) => (
                              <tr key={branch.idBranch}>
                                <td className="border border-gray-300 px-4 py-2">
                                  <li className="text-realizaBlue">
                                    {branch.name}
                                  </li>
                                </td>
                                <td className="border border-gray-300 text-center">
                                  {branch.cnpj}
                                </td>
                              </tr>
                            ))
                          ) : (
                            <tr>
                              <td
                                colSpan={2}
                                className="border border-gray-300 px-4 py-2 text-center"
                              >
                                Nenhuma filial encontrada
                              </td>
                            </tr>
                          )}
                        </tbody>
                      </table>
                    </div>
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
                    <table className="mt-4 w-[40vw] border-collapse border border-gray-300">
                      <thead>
                        <tr>
                          <th className="border border-gray-300 px-4 py-2">
                            Nome
                          </th>
                          <th className="border border-gray-300 px-4 py-2">
                            CPF
                          </th>
                          <th className="border border-gray-300 px-4 py-2">
                            Ações
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {usersFromBranch && usersFromBranch.length > 0 ? (
                          usersFromBranch.map((users: any) => (
                            <tr key={users.idUser} className="overflow-auto">
                              <td className="border border-gray-300 px-4 py-2">
                                {users.firstName}
                              </td>
                              <td className="border border-gray-300 px-4 py-2">
                                <span>{users.cpf}</span>
                              </td>
                              <td className="border border-gray-300 px-4 py-2">
                                <Link
                                  to={`/sistema/detailsEmployees/${users.idEmployee}`}
                                >
                                  <button className="text-realizaBlue ml-4 flex items-center justify-center hover:underline">
                                    <Settings2 />
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
                    {/* <Pagination
                                  currentPage={currentPage}
                                  totalPages={totalPages}
                                  onPageChange={handlePageChange}
                                /> */}
                  </div>
                )}
              </div>
            </div>
          </div>
          <div className="mt-8 grid grid-cols-1 gap-8 md:grid-cols-[5fr_3fr]">
            <GraphicHomeLeft />
            <GraphicHomeRight />
          </div>
          <div className="mt-5 w-full text-right">
            <NavLink to="/BIs">
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
              <ActionButton label="Enviar documento" icon={<ChevronRight />} />
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

          {/* OUTROS CARDS */}
          <div className="pt-20">
            <h2 className="pb-6 text-xl font-medium">Ações rápidas</h2>
            <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 md:grid-cols-4">
              <MainCard
                title="Solicitações de homologação"
                value={28}
                icon={<LineChartIcon />}
              />
              <MainCard
                title="Contratos em andamento"
                value={95}
                icon={<Briefcase />}
              />
              <MainCard
                title="Documentos pendentes"
                value={1022}
                icon={<Clock />}
              />
              <MainCard
                title="Número de fornecedores"
                value={22}
                icon={<User />}
              />
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
