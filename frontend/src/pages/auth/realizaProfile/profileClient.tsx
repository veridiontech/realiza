import { Instagram, Mail, MapPin, Pencil, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { Link, NavLink } from "react-router-dom";

import { Button } from "@/components/ui/button";

import bulletBlue from "@/assets/bulletBlue.svg";
import logoExemaple from "@/assets/logoExample.svg";
import menExample from "@/assets/menExample.svg";
export function ProfileClient() {
  return (
    <>
      <Helmet title="profile" />
      <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
        <div className="mb-10">
          <div className="mt-6 min-h-[220px] w-full rounded-t-xl bg-blue-800" />
          <div className="shadow-custom-blue relative flex w-full flex-col px-4 pb-10 sm:px-6 md:px-8 lg:px-12">
            <div className="flex flex-col items-center justify-between md:flex-row">
              <div className="relative bottom-10 left-1 flex items-center gap-4">
                <img
                  src={logoExemaple}
                  alt="Logo Example"
                  className="h-auto w-24"
                />
                <div className="relative top-5 flex flex-col gap-2 md:gap-5">
                  <h2 className="mt-10 text-lg font-medium text-sky-800">
                    DISTRIBUIDORA DE GAS LTDA
                  </h2>
                  <span className="text-xs font-medium text-sky-700">
                    Ultragaz | Distribuidora de Gás Residencial e GLP para
                    Empresas
                  </span>
                </div>
              </div>
              <div>
                <NavLink to="/editProfile">
                  <Button className="bg-blue-700">Editar perfil</Button>
                </NavLink>
              </div>
            </div>
            <div className="flex flex-col gap-6 md:gap-10">
              <div className="flex flex-row items-center gap-2">
                <h3 className="font-medium">Descrição da empresa</h3>
                <Pencil />
              </div>
              <div className="w-full md:w-[460px]">
                <p className="text-sm font-normal">
                  A Ultragaz, fundada em 1937, é líder na distribuição de Gás
                  Liquefeito de Petróleo (GLP) no Brasil. Pioneira na introdução
                  do botijão de gás, a empresa oferece soluções seguras e
                  eficientes para residências e empresas. Parte do Grupo Ultra,
                  a Ultragaz destaca-se pela inovação, qualidade e compromisso
                  com a sustentabilidade.
                </p>
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
                <span>E-mail: exemplo@gmail.com</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Phone />
                <span>Telefone: 55 11-12345 6789</span>
              </div>
            </div>
            <span>Outras formas de contato</span>
          </div>
          <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
            <h2 className="text-lg font-medium">Serviços oferecidos</h2>
            <div className="flex flex-col gap-6 md:flex-row md:gap-8">
              <div className="flex flex-col gap-4">
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={bulletBlue}
                    alt="Bullet Blue"
                    className="h-auto w-5"
                  />
                  <span>Distribuição de Gás LP</span>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={bulletBlue}
                    alt="Bullet Blue"
                    className="h-auto w-5"
                  />
                  <span>Distribuição de Gás LP</span>
                </div>
              </div>
              <div className="flex flex-col gap-4">
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={bulletBlue}
                    alt="Bullet Blue"
                    className="h-auto w-5"
                  />
                  <span>Distribuição de Gás LP</span>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={bulletBlue}
                    alt="Bullet Blue"
                    className="h-auto w-5"
                  />
                  <span>Distribuição de Gás LP</span>
                </div>
              </div>
            </div>
            <Button className="w-full bg-blue-700 md:w-40">
              Adicionar serviços
            </Button>
          </div>
        </div>
        <div className="flex flex-col gap-8 lg:flex-row">
          <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
            <div className="flex flex-row items-center gap-2">
              <MapPin />
              <h2 className="text-lg font-medium">
                Unidades Cadastradas e gestores fiscais correspondentes
              </h2>
            </div>
            <div className="flex flex-col md:flex-row md:gap-8">
              <div className="flex flex-col gap-6">
                <div className="flex flex-row items-center gap-2">
                  <div className="flex h-11 w-12 items-center justify-center rounded-xl bg-rose-700">
                    <span className="text-zinc-50">SP</span>
                  </div>
                  <div className="flex flex-col">
                    <span>Avenida Paulista, 1000, São Paulo</span>
                    <span className="text-xs">Matriz</span>
                  </div>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <div className="flex h-11 w-12 items-center justify-center rounded-xl bg-rose-700">
                    <span className="text-zinc-50">SP</span>
                  </div>
                  <div className="flex flex-col">
                    <span>Avenida Paulista, 1000, São Paulo</span>
                    <span className="text-xs">Matriz</span>
                  </div>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <div className="flex h-11 w-12 items-center justify-center rounded-xl bg-rose-700">
                    <span className="text-zinc-50">SP</span>
                  </div>
                  <div className="flex flex-col">
                    <span>Avenida Paulista, 1000, São Paulo</span>
                    <span className="text-xs">Matriz</span>
                  </div>
                </div>
              </div>
              <div className="flex flex-col gap-6">
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={menExample}
                    alt="Men Example"
                    className="h-auto w-8"
                  />
                  <div className="flex flex-col">
                    <span>André Luiz Barros</span>
                    <span className="text-xs">Gestor</span>
                  </div>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={menExample}
                    alt="Men Example"
                    className="h-auto w-8"
                  />
                  <div className="flex flex-col">
                    <span>André Luiz Barros</span>
                    <span className="text-xs">Gestor</span>
                  </div>
                </div>
                <div className="flex flex-row items-center gap-2">
                  <img
                    src={menExample}
                    alt="Men Example"
                    className="h-auto w-8"
                  />
                  <div className="flex flex-col">
                    <span>André Luiz Barros</span>
                    <span className="text-xs">Gestor</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
            <h2 className="text-lg font-medium">Redes sociais</h2>
            <div className="flex flex-col gap-4">
              <div className="flex flex-row items-center gap-2">
                <Instagram />
                <span>@ultragaz_company</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Instagram />
                <span>@ultragaz_company</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Instagram />
                <span>@ultragaz_company</span>
              </div>
            </div>
            <Link to="/collaborators">
              <Button className="w-full bg-blue-700 md:w-40">
                Ver colaboradores
              </Button>
            </Link>
          </div>
        </div>
        <div className="m-10 flex flex-col gap-4 md:flex-row">
          <Button className="w-full md:w-auto">Cancelar</Button>
          <Button className="w-full md:w-auto">Excluir conta</Button>
        </div>
      </section>
    </>
  );
}
