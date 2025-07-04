import { Dialog, DialogContent, DialogTitle } from "@/components/ui/dialog";
import { useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { toast } from "sonner";

interface CreateDocumentProps {
  isOpen: boolean;
  onClose: () => void;
  contractId: string;
  onSuccess: () => void;
}

export function CreateDocument({
  isOpen,
  onClose,
  contractId,
  onSuccess,
}: CreateDocumentProps) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [mandatory, setMandatory] = useState(false);

  const token = localStorage.getItem("tokenClient");

  const handleSubmit = async () => {
    if (!title || !contractId) {
      toast.error("Título e contrato são obrigatórios.");
      return;
    }

    try {
      await axios.post(
        `${ip}/document`,
        {
          title,
          description,
          dueDate,
          contractId,
          isMandatory: mandatory,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast.success("Documento criado com sucesso!");
      onClose();
      setTitle("");
      setDescription("");
      setDueDate("");
      setMandatory(false);
      onSuccess();
    } catch (error) {
      console.error(error);
      toast.error("Erro ao criar o documento.");
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="flex flex-col gap-4">
        <DialogTitle>Criar Novo Documento</DialogTitle>

        <input
          type="text"
          placeholder="Título do documento"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 w-full"
        />

        <textarea
          placeholder="Descrição (opcional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 w-full h-[80px]"
        />

        <input
          type="date"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
          className="border border-gray-300 rounded px-3 py-2 w-full"
        />

        <label className="flex items-center gap-2 text-sm text-gray-600">
          <input
            type="checkbox"
            checked={mandatory}
            onChange={(e) => setMandatory(e.target.checked)}
          />
          Documento obrigatório?
        </label>

        <button
          onClick={handleSubmit}
          className="mt-4 bg-realizaBlue text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Criar Documento
        </button>
      </DialogContent>
    </Dialog>
  );
}
