// import { useState } from "react";
// import { Modal } from "@/components/modal";
// import { Search } from "lucide-react";
// import { fetchCompanyByCNPJ } from "@/hooks/gets/useCnpjApi";
// import axios from "axios";
// import * as z from "zod";
// import { ip } from "@/utils/ip";
// import { useClient } from "@/context/Client-Provider";
// import { useForm } from "react-hook-form";
// import { zodResolver } from "@hookform/resolvers/zod";

// interface StepOneServiceProvidersProps {
//   onClose: () => void;
//   onSubmit: (data: Record<string, any>) => void;
// }

// const providerSchema = z.object({
//   cnpj: z
//     .string()
//     .regex(/^\d{2}\.\d{3}\.\d{3}\/\d{4}\-\d{2}$/, "CNPJ inválido"),
//   email: z.string().email("E-mail inválido"),
//   company: z.string().default("SUPPLIER")
// });

// type ProviderSchema = z.infer<typeof providerSchema>
// export function StepOneServiceProviders({
//   onClose,
//   onSubmit,
// }: StepOneServiceProvidersProps) {
//   const [cnpj, setCnpj] = useState("");
//   const [providerData, setProviderData] = useState<{
//     nome?: string;
//     email?: string;
//   } | null>(null);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState<string | null>(null);
//   const { client } = useClient()

//   const handleSearchCNPJ = async () => {
//     setError(null);
//     setLoading(true);

//     try {
//       const cleanedCNPJ = cnpj.replace(/\D/g, "");
//       if (!cleanedCNPJ || cleanedCNPJ.length !== 14) {
//         throw new Error("CNPJ inválido. Por favor, verifique o formato.");
//       }

//       const data = await fetchCompanyByCNPJ(cleanedCNPJ);
//       setProviderData({
//         nome: data.razaoSocial || data.nomeFantasia,
//         email: "",
//       });
//     } catch (err: any) {
//       setError(err.message || "Erro desconhecido.");
//       setProviderData(null);
//     } finally {
//       setLoading(false);
//     }
//   };

  

//   const getIdUniqueClient = client?.idClient
  
//   console.log(getIdUniqueClient);
  

//   const handleSubmit = async (data: ProviderSchema) => {
//     const payload = {
//       ...data,
//       idCompany: getIdUniqueClient,
//       company: data.company || "SUPPLIER", // Garante um valor padrão
//     };
//     try {
//       console.log('teste envio');
      
//       await axios.post(`${ip}/invite`, payload)
//       // console.log(res.data);
      
//       console.log('sucesso');
      
//     } catch (error) {
//       if (error instanceof z.ZodError) {
//         setError(error.errors[0]?.message || "Erro de validação.");
//       } else if (axios.isAxiosError(error)) {
//         setError(error.response?.data?.message || "Erro ao enviar o e-mail.");
//       }
//     }
//   };

//   return (
//     // <Modal
//     //   title="Adicionar Prestador"
//     //   fields={[
//     //     {
//     //       name: "cnpj",
//     //       label: "CNPJ",
//     //       type: "custom",
//     //       render: ({ value, onChange }) => (
//     //         <div className="flex flex-col gap-2">
//     //           <form className="flex items-center gap-2" >
//     //             <input
//     //               type="text"
//     //               value={value}
//     //               onChange={(e) => {
//     //                 setCnpj(e.target.value);
//     //                 onChange(e.target.value);
//     //               }}
//     //               placeholder="00.000.000/0001-01"
//     //               required
//     //               className="flex-1 rounded-md border text-black border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:ring-blue-500"
//     //             />
//     //             <button
//     //               type="button"
//     //               onClick={handleSearchCNPJ}
//     //               className="rounded-md bg-blue-600 px-3 py-2 text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
//     //               disabled={loading}
//     //             >
//     //               {loading ? "Buscando..." : <Search className="h-5 w-5" />}
//     //             </button>
//     //           </form>
//     //           {providerData?.nome && (
//     //             <span className="text-sm text-white">
//     //               Nome: {providerData.nome}
//     //             </span>
//     //           )}
//     //           {error && <p className="text-sm text-red-500">{error}</p>}
//     //         </div>
//     //       ),
//     //     },
//     //     ...(providerData
//     //       ? [
//     //           {
//     //             name: "email",
//     //             label: "E-mail",
//     //             type: "email" as const,
//     //             placeholder: "exemplo@email.com",
//     //             required: true,
//     //             defaultValue: providerData.email,
//     //           },
//     //         ]
//     //       : []),
//     //   ]}
//     //   onSubmit={(data) => handleSubmit(data)}
//     //   onClose={onClose}
//     // />
//   );
// }
