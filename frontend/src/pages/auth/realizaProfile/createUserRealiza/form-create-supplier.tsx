import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { propsSupplier } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { TailSpin } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

function validarCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, "");
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;

  let soma = 0;
  for (let i = 1; i <= 9; i++) soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.substring(9, 10))) return false;

  soma = 0;
  for (let i = 1; i <= 10; i++) soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  return resto === parseInt(cpf.substring(10, 11));
}

function validarTelefoneRepetido(telefone: string) {
  const digits = telefone.replace(/\D/g, "");
  return !/^(\d)\1+$/.test(digits);
}

const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$/;
const phoneRegex = /^\(?\d{2}\)?[\s-]?\d{4,5}[-]?\d{4}$/;

const createUserRealizaSchema = z.object({
  firstName: z.string().nonempty("Insira um nome"),
  surname: z.string().nonempty("Insira um sobrenome"),
  email: z.string().email("Insira um email válido"),
  cpf: z.string().nonempty("CPF é obrigatório").regex(cpfRegex, "CPF inválido").refine(validarCPF, { message: "CPF inválido" }),
  cellPhone: z.string().nonempty("Celular é obrigatório").regex(phoneRegex, "Telefone inválido").refine(validarTelefoneRepetido, { message: "Telefone inválido" }),
  position: z.string().nonempty("Insira um cargo"),
  role: z.string().default("ROLE_SUPPLIER_MANAGER"),
  enterprise: z.string().default("SUPPLIER"),
  idEnterprise: z.string(),
});

type CreateUserRealizaSchema = z.infer<typeof createUserRealizaSchema>;

export function FormCreateUserSupplier() {
  const [userPreview, setUserPreview] = useState({ firstName: "", surname: "", email: "" });
  const [isLoading, setIsLoading] = useState(false);
  const [suppliers, setSuppliers] = useState<propsSupplier[]>([]);
  const [phoneValue, setPhoneValue] = useState("");
  const [cpfValue, setCpfValue] = useState("");
  const [image, setImage] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
    reset,
    setValue,
  } = useForm<CreateUserRealizaSchema>({
    resolver: zodResolver(createUserRealizaSchema),
  });

  const formatCPF = (value: string) =>
    value.replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
      .slice(0, 14);

  const formatPhone = (value: string) => {
    const digits = value.replace(/\D/g, "");
    if (digits.length <= 2) return digits;
    else if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    else if (digits.length <= 10) return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
    else return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImage(file);
      const reader = new FileReader();
      reader.onloadend = () => setPreviewUrl(reader.result as string);
      reader.readAsDataURL(file);
    }
  };

  const handleRemovePhoto = () => {
    setImage(null);
    setPreviewUrl(null);
    const fileInput = document.getElementById("photo-upload") as HTMLInputElement;
    if (fileInput) {
      fileInput.value = "";
    }
  };

  const firstName = watch("firstName");
  const surname = watch("surname");
  const email = watch("email");

  useEffect(() => {
    setUserPreview({ firstName, surname, email });
  }, [firstName, surname, email]);

  const getAllSupplier = async () => {
    const token = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/supplier`, {
        headers: { Authorization: `Bearer ${token}` },
        params: { size: 10000 },
      });
      setSuppliers(res.data.content);
    } catch (err) {
      console.log("Erro ao buscar fornecedores", err);
    }
  };

  useEffect(() => {
    getAllSupplier();
  }, []);

  const createUser = async (data: CreateUserRealizaSchema) => {
    setIsLoading(true);
    try {
      const token = localStorage.getItem("tokenClient");

      const formData = new FormData();

      Object.keys(data).forEach(key => {
        formData.append(key, data[key as keyof CreateUserRealizaSchema]);
      });

      if (image) {
          formData.append("profilePicture", image);
      }

      await axios.post(`${ip}/user/manager/new-user`, formData, {
        headers: { 
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      toast.success("Sucesso ao criar novo usuário Realiza");
      reset();
      setCpfValue("");
      setPhoneValue("");
      setImage(null);
      setPreviewUrl(null);
      const fileInput = document.getElementById("photo-upload") as HTMLInputElement;
      if (fileInput) {
          fileInput.value = "";
      }
    } catch (err: any) {
      toast.error("Erro ao criar um novo usuário, tente novamente");
      console.error("Erro:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(createUser)} className="flex flex-col gap-6">
      <div className="flex flex-col lg:flex-row gap-6">
        <div className="w-full lg:w-[70%] border rounded-md p-6 shadow-md bg-white">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Informações Pessoais</h2>

          <div className="flex flex-col gap-2 mb-4">
            <Label>Selecione um fornecedor</Label>
            <select {...register("idEnterprise")} className="border border-neutral-300 p-2 rounded-md">
              {suppliers.map((supplier) => (
                <option value={supplier.idProvider} key={supplier.idProvider}>
                  {supplier.corporateName}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <Label>Nome</Label>
              <Input type="text" placeholder="Digite seu nome" {...register("firstName")} />
              {errors.firstName && <p className="text-red-500">{errors.firstName.message}</p>}
            </div>
            <div>
              <Label>Sobrenome</Label>
              <Input type="text" placeholder="Digite seu sobrenome" {...register("surname")} />
              {errors.surname && <p className="text-red-500">{errors.surname.message}</p>}
            </div>
          </div>

          <div className="mt-4">
            <Label>CPF</Label>
            <Input
              type="text"
              value={cpfValue}
              onChange={(e) => {
                const formatted = formatCPF(e.target.value);
                setCpfValue(formatted);
                setValue("cpf", formatted, { shouldValidate: true });
              }}
              placeholder="000.000.000-00"
              maxLength={14}
            />
            {errors.cpf && <p className="text-red-500">{errors.cpf.message}</p>}
          </div>

          <div className="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <Label>Cargo</Label>
              <Input type="text" placeholder="Digite seu cargo" {...register("position")} />
              {errors.position && <p className="text-red-500">{errors.position.message}</p>}
            </div>
            <div>
              <Label>Email</Label>
              <Input type="email" placeholder="Digite seu email" {...register("email")} />
              {errors.email && <p className="text-red-500">{errors.email.message}</p>}
            </div>
          </div>

          <div className="mt-4">
            <Label>Telefone</Label>
            <Input
              type="text"
              value={phoneValue}
              onChange={(e) => {
                const formatted = formatPhone(e.target.value);
                setPhoneValue(formatted);
                setValue("cellPhone", formatted, { shouldValidate: true });
              }}
              placeholder="(00) 00000-0000"
              maxLength={15}
            />
            {errors.cellPhone && <p className="text-red-500">{errors.cellPhone.message}</p>}
          </div>

          <div className="mt-6 flex justify-end">
            <Button type="submit" className="bg-[#1f2e4d] text-white px-6 py-2 rounded-md hover:bg-[#2e3e5e]">
              {isLoading ? <TailSpin height="24" width="24" color="#fff" /> : "Criar usuário"}
            </Button>
          </div>
        </div>

        <div className="w-full lg:w-[30%] bg-[#34495E] p-6 rounded-md flex flex-col gap-6 text-white">
          <h3 className="font-semibold text-base">Selecione uma foto:</h3>

          <label
            htmlFor="photo-upload"
            className="cursor-pointer h-[200px] w-full flex items-center justify-center border-2 border-dashed border-[#7d8aa3] rounded-md hover:border-white transition"
          >
            {previewUrl ? (
              <img
                src={previewUrl}
                alt="Preview"
                className="h-full w-full object-cover rounded-md"
              />
            ) : (
              <User size={48} className="text-[#7d8aa3]" />
            )}
            <input
              id="photo-upload"
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="hidden"
            />
          </label>
          
          {previewUrl && (
            <Button
              type="button"
              onClick={handleRemovePhoto}
              className="w-full bg-red-600 text-white hover:bg-red-700 transition"
            >
              Remover Foto
            </Button>
          )}

          <div className="mt-2">
            <h4 className="text-sm font-medium text-gray-200 mb-1">Pré visualização</h4>
            <div className="bg-[#3d5a73] p-4 rounded-md space-y-4">
              <div>
                <p className="text-xs text-[#d1d5db] mb-1">Nome:</p>
                <div className="text-sm font-semibold bg-[#2e3e50] p-2 rounded-md">
                  {userPreview.firstName} {userPreview.surname}
                </div>
              </div>
              <div>
                <p className="text-xs text-[#d1d5db] mb-1">E-mail:</p>
                <div className="text-sm font-semibold bg-[#2e3e50] p-2 rounded-md">
                  {userPreview.email || "exemplo@empresa.com"}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  );
}