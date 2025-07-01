import { ProfilePic } from "@/components/header/profile-pic";
import { Skeleton } from "@/components/ui/skeleton";
import { useUser } from "@/context/user-provider";
import { Mail } from "lucide-react";

export function TopSection() {
  const { user } = useUser();

  return (
    <div className="w-full rounded-lg overflow-hidden shadow-md">
      {/* Topo com gradiente azul e padrão decorativo */}
      <div
        className="relative h-24 bg-gradient-to-r from-[#3a5a9d] to-[#4668b0]"
        style={{
          backgroundImage: `
            linear-gradient(135deg, rgba(255,255,255,0.07) 25%, transparent 25%),
            linear-gradient(225deg, rgba(255,255,255,0.07) 25%, transparent 25%),
            linear-gradient(45deg, rgba(255,255,255,0.07) 25%, transparent 25%),
            linear-gradient(315deg, rgba(255,255,255,0.07) 25%, transparent 25%)
          `,
          backgroundSize: '20px 20px',
          backgroundPosition: '0 0, 0 10px, 10px -10px, -10px 0px',
        }}
      />
      {/* Parte branca com conteúdo */}
      <div className="relative -mt-12 px-6 pb-6 flex flex-col md:flex-row md:items-center md:justify-between">
        {/* Foto + nome + email */}
        <div className="flex items-center gap-6">
          <div className="w-24 h-24 rounded-full border-4 overflow-hidden bg-gray-200">
            <ProfilePic />
          </div>

          <div className="flex flex-col gap-1">
            <h2 className="text-xl font-semibold text-[#1f2f54]">
              {user ? `${user.firstName} ${user.surname}` : <Skeleton className="h-5 w-40" />}
            </h2>

            <div className="flex items-center gap-1 text-xs text-gray-500">
              <Mail className="w-4 h-4" />
              <span>E-mail</span>
            </div>
            <span className="text-sm text-gray-800">
              {user ? user.email : <Skeleton className="h-4 w-32" />}
            </span>
          </div>
        </div>

        {/* Botões */}
        <div className="mt-6 md:mt-0 flex gap-2">
          <button className="flex items-center gap-1 border border-gray-300 rounded px-3 py-1.5 text-sm text-[#1f2f54] hover:bg-gray-50 transition">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={1.5} viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7" />
            </svg>
            Sair da conta
          </button>
          <button className="flex items-center gap-1 bg-[#1f2f54] text-white rounded px-3 py-1.5 text-sm hover:bg-[#152446] transition">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={1.5} viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487a2.433 2.433 0 013.438 3.44l-9.75 9.75-4.125.688.688-4.126 9.75-9.75z" />
            </svg>
            Editar perfil
          </button>
        </div>
      </div>
    </div>
  );
}
