import { Mail, Upload } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { EditModalEnterprise } from "./edit-modal-enterprise";
import { UploadDocumentButton } from "@/components/ui/upload-document-button";
import { useState, useRef } from "react";
import { useClient } from "@/context/Client-Provider";

export function ProfileEnterpriseReprise() {
  const [loading] = useState(false);
  const { client } = useClient();

  const firstLetter = client?.corporateName?.charAt(0) || "";
  const lastLetter = client?.corporateName?.slice(-1) || "";

  const [avatar, setAvatar] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatar(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <>
      <Helmet title="Perfil da Empresa" />
      <section className="mx-4 my-10 flex flex-col gap-8 md:mx-10 lg:mx-32 xl:mx-40 lg:flex-row">
        <div className="w-full max-w-sm rounded-xl bg-white shadow-md p-6 flex flex-col items-center gap-6">
          <div
            className="w-28 h-28 rounded-full border-4 border-realizaBlue flex items-center justify-center text-3xl font-bold text-realizaBlue cursor-pointer overflow-hidden relative group"
            onClick={() => fileInputRef.current?.click()}
          >
            {avatar ? (
              <img src={avatar} alt="Avatar" className="w-full h-full object-cover" />
            ) : (
              <>
                {firstLetter}{lastLetter}
                <div className="absolute inset-0 bg-black bg-opacity-30 opacity-0 group-hover:opacity-100 transition flex items-center justify-center">
                  <Upload size={20} className="text-white" />
                </div>
              </>
            )}
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleImageUpload}
            />
          </div>

          <UploadDocumentButton text="Visualizar documentos" />
          <EditModalEnterprise />
        </div>

       
        <div className="flex-1 rounded-xl shadow-md bg-white overflow-hidden">
          
          <div className="bg-gradient-to-r from-[#6983BE] to-[#546A96] text-white px-8 py-6">
            <h2 className="text-xl font-semibold">{client?.corporateName || "Nome da Empresa"}</h2>
            <p className="text-sm underline opacity-90">
              {client?.tradeName || "Nome Fantasia"} / {client?.cnpj || "CNPJ não disponível"}
            </p>
          </div>

          <div className="p-8 space-y-6">
            <h3 className="text-base font-semibold text-gray-800">Formas de contato</h3>

            <div className="flex flex-col gap-3 text-sm">
              <div className="flex items-center gap-2 rounded-md border border-gray-300 bg-white px-4 py-2">
                <Mail size={18} />
                <span>
                  E-mail:&nbsp;
                  <a href={`mailto:${client?.email}`} className="text-[#2A3F57] underline">
                    {client?.email || "Não disponível"}
                  </a>
                </span>
              </div>

              {/*<div className="flex items-center gap-2 rounded-md border border-gray-300 bg-white px-4 py-2">
                <Phone size={18} />
                <span>
                  Telefone:&nbsp;
                  <span className="text-[#2A3F57] underline">
                    {client?.phone || "Não disponível"}
                  </span>
                </span>
              </div>

              <div className="flex items-center gap-2 rounded-md border border-gray-300 bg-white px-4 py-2">
                <MapPin size={18} />
                <span>
                  Cep:&nbsp;
                  <span className="text-[#2A3F57] underline">
                    {client?.cep || "Não disponível"}
                  </span>
                </span>
              </div>*/}
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
