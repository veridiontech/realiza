import { Locate, Mail, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { EditModalEnterprise } from "./edit-modal-enterprise";
import { useClient } from "@/context/Client-Provider";
import { Skeleton } from "@/components/ui/skeleton";
import { useSupplier } from "@/context/Supplier-context";
// import axios from "axios";
// import { ip } from "@/utils/ip";
// import { useEffect, useState } from "react";
import { useUser } from "@/context/user-provider";
// import axios from "axios";
// import { ip } from "@/utils/ip";
// import { useEffect } from "react";
// import { z } from "zod";
// import { useForm } from "react-hook-form";
// import { zodResolver } from "@hookform/resolvers/zod";

// const profileLogoClientFormSchema = z.object({
//   file: z
//     .instanceof(File, { message: "O arquivo deve ser uma imagem válida." })
//     .refine(
//       (file) => file.size <= 2 * 1024 * 1024,
//       "O arquivo deve ter no máximo 2MB.",
//     )
//     .refine(
//       (file) => ["image/png", "image/jpeg", "image/jpg"].includes(file.type),
//       "Apenas imagens PNG ou JPG são permitidas.",
//     ),
// });

// type ProfileLogoClientFormSchema = z.infer<typeof profileLogoClientFormSchema>
export function ProfileEnterpriseReprise() {
  // const [selectedFile, setSelectedFile] = useState<File | null>(null);
  // const [logoClient, setLogoClient] = useState<string | null>(null);
  const { client } = useClient();
  const { supplier } = useSupplier();
  const { user } = useUser();
  // const [supplierData, setSupplierData] = useState(null);
  const firstLetter = client?.tradeName?.charAt(0) || "";
  const lastLetter = client?.tradeName?.slice(-1) || "";

  // const {
  //   // handleSubmit,
  //   setValue,
  //   // formState: { errors },
  // } = useForm<ProfileLogoClientFormSchema>({
  //   resolver: zodResolver(profileLogoClientFormSchema)
  // })

  // const getLogoClient = async () => {
  //   try {
  //     const res = await axios.get(
  //       `${ip}/client/change-logo/${client?.idClient}`,
  //     );
  //     // setLogoClient(res.data);
  //   } catch (err) {
  //     console.log("erro ao buscar logo:", err);
  //   }
  // };

  // const handleFileUpload = async () => {
  //   if (!selectedFile) {
  //     console.error("Nenhum arquivo selecionado.");
  //     return;
  //   }
  // }

  // const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
  //   const file = event.target.files?.[0];
  //   if (file) {
  //     setSelectedFile(file);
  //     setValue("file", file);
  //   }
  // };

  // useEffect(() => {
  //   getLogoClient();
  // });

  // const getSupplier = async () => {
  //   if (!supplier?.idProvider) return; 
  //   try {
  //     const res = await axios.get(`${ip}/supplier/${supplier.idProvider}`);
  //     setSupplierData(res.data);
  //   } catch (error) {
  //     console.error("Erro ao buscar fornecedor:", error);
  //   }
  // };

  // useEffect(() => {
  //   if( supplier?.idProvider) {
  //     getSupplier()
  //   }
  // }, [supplier?.idProvider])

  // console.log(user)

  if (user?.role === "ROLE_SUPPLIER_RESPONSIBLE") {
    return (
      <>
        <Helmet title="profile" />
        <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
          <div className="dark:bg-primary mb-10 rounded-t-xl bg-white">
            <div className="bg-realizaBlue min-h-[220px] w-full rounded-t-xl" />
            <div className="shadow-custom-blue relative flex w-full flex-col px-4 pb-10 sm:px-6 md:px-8 lg:px-12">
              <div className="flex flex-col items-center justify-between md:flex-row">
                <div className="relative bottom-10 left-1 flex items-center gap-4">
                  <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
                    <div className="text-[40px] text-white">
                      {firstLetter}
                      {lastLetter}
                    </div>
                  </div>
                  <div className="relative top-5 flex flex-col gap-2 md:gap-5 dark:text-white">
                    <div className="text-lg font-medium text-sky-800">
                      {supplier?.tradeName ? (
                        <h2>{supplier.tradeName}</h2>
                      ) : (
                        <span className="font-normal">
                          Nenhum fornecedor selecionado
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-2">
                      <div>
                        {supplier?.tradeName ? (
                          <span className="text-xs font-medium text-sky-700">
                            {supplier.tradeName} / {supplier?.cnpj}
                          </span>
                        ) : (
                          <div className="flex items-center gap-2">
                            <Skeleton className="h-[1vh] w-[4vw]" />
                            <span className="text-sky-700"> / </span>
                            <Skeleton className="h-[1vh] w-[4vw]" />
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
                <div>
                  <EditModalEnterprise />
                </div>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-8 lg:flex-row">
            <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
              <h2 className="text-lg font-medium">Formas de contato</h2>
              <div className="flex flex-col gap-4">
                <div className="flex flex-row items-center gap-2">
                  <Mail />
                  <span>E-mail: {supplier?.email || "Não disponível"}</span>
                </div>
                <div className="flex flex-row items-center gap-2">
                    <Locate/>
                  <span className="flex items-center gap-2">
                    <span>Cep: </span>
                    <span>{supplier?.cep || "Não disponível"}</span>
                  </span>
                </div>
              </div>
              <span>Outras formas de contato</span>
            </div>
          </div>
        </section>
      </>
    );
  }
  

  return (
    <>
      <Helmet title="profile" />
      <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
        <div className="dark:bg-primary mb-10 rounded-t-xl bg-white">
          <div className="bg-realizaBlue min-h-[220px] w-full rounded-t-xl" />
          <div className="shadow-custom-blue relative flex w-full flex-col px-4 pb-10 sm:px-6 md:px-8 lg:px-12">
            <div className="flex flex-col items-center justify-between md:flex-row">
              <div className="relative bottom-10 left-1 flex items-center gap-4">
              <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
                      <div className="text-[40px] text-white">
                        {firstLetter}
                        {lastLetter}
                      </div>
                    </div>
                <div className="relative top-5 flex flex-col gap-2 md:gap-5 dark:text-white">
                  <div className="text-lg font-medium text-sky-800">
                    {client ? (
                      <h2>{client.tradeName}</h2>
                    ) : (
                      <span className="font-normal">
                        Nenhum cliente selecionado
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-2">
                    <div>
                      {client ? (
                        <span className="text-xs font-medium text-sky-700">
                          {client.tradeName} / {client?.cnpj}
                        </span>
                      ) : (
                        <div className="flex items-center gap-2">
                          <Skeleton className="h-[1vh] w-[4vw]" />
                          <span className="text-sky-700"> / </span>
                          <Skeleton className="h-[1vh] w-[4vw]" />
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
              <div>
                <EditModalEnterprise />
              </div>
            </div>
          </div>
        </div>
        <div className="flex flex-col gap-8 lg:flex-row">
          <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
            <h2 className="text-lg font-medium">Formas de contato</h2>
            <div className="flex flex-col gap-4">
              <div className="flex flex-row items-center gap-2">
                <Mail />
                <span>E-mail: {client?.email}</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Phone />
                <span className="flex items-center gap-2">
                  <span>Telefone: </span>
                  <span>{client?.telephone}</span>
                </span>
              </div>
            </div>
            <span>Outras formas de contato</span>
          </div>
        </div>
      </section>
    </>
  );
}
