import {
  UsersRound,
  MessageCircle,
  University,
  Files,
  ChevronRight,
} from "lucide-react";
import { MainCard } from "./mainCard";
import { ActionButton } from "./actionButton";

export function QuickActions() {
  return (
    <div className="bg-gray-50 p-6">
      <h1 className="mb-6 text-xl font-semibold text-gray-800">
        Ações rápidas
      </h1>
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
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
        <MainCard title="Unidades" value={4} icon={<University size={28} />} />
        <MainCard title="Contratos" value={72} icon={<Files size={28} />} />
      </div>
      <div className="mt-8 grid grid-cols-1 gap-x-6 gap-y-4 sm:grid-cols-2 lg:grid-cols-4">
        <ActionButton label="Adicionar fornecedor" icon={<ChevronRight />} />
        <ActionButton label="Enviar documento" icon={<ChevronRight />} />
        <ActionButton label="Criar contato" icon={<ChevronRight />} />
        <ActionButton label="Gerar relatório" icon={<ChevronRight />} />
        <ActionButton label="Atualizar documentos" icon={<ChevronRight />} />
        <ActionButton label="Consultar contratos" icon={<ChevronRight />} />
        <ActionButton label="Aprovar solicitações" icon={<ChevronRight />} />
        <ActionButton label="Editar colaboradores" icon={<ChevronRight />} />
      </div>
    </div>
  );
}
