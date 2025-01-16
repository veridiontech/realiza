import { useAddDocument, AddDocumentData } from "@/hooks/posts/useAddDocument";
import { Modal } from "@/components/modal";

interface AddDocumentProps {
  onClose: () => void;
}

export function AddDocument({ onClose }: AddDocumentProps) {
  const addDocumentMutation = useAddDocument();

  const handleSubmit = (data: { [key: string]: any }) => {
    // Verificação de tipo antes de enviar
    if (data.documentType && data.file) {
      // Criar a estrutura necessária para o envio do arquivo
      const formData = new FormData();
      formData.append("documentType", data.documentType);
      formData.append("file", data.file);

      addDocumentMutation.mutate(formData as unknown as AddDocumentData, {
        onSuccess: () => {
          alert("Documento adicionado com sucesso!");
          onClose();
        },
        onError: (error) => {
          alert("Erro ao adicionar documento.");
          console.error(error);
        },
      });
    } else {
      alert("Todos os campos obrigatórios devem ser preenchidos.");
    }
  };

  return (
    <Modal
      title="Adicionar Documento"
      fields={[
        {
          name: "documentType",
          label: "Tipo de Documento",
          type: "select",
          options: [
            "RG",
            "CPF",
            "CNH",
            "Comprovante de Residência",
            "Certidão de Nascimento",
            "Certidão de Casamento",
            "Certidão de Óbito",
            "Carteira de Trabalho",
            "Cartão do SUS",
            "Cartão do Plano de Saúde",
            "Cartão do Banco",
            "Cartão do Transporte",
            "Cartão de Crédito",
            "Cartão de Débito",
            "Cartão de Vacinação",
            "Cartão de Estacionamento",
            "Cartão de Benefício",
            "Cartão de Convênio",
            "Cartão de Acesso",
            "Cartão de Alimentação",
            "Cartão de Refeição",
            "Cartão de Combustível",
            "Cartão de Pagamento",
            "Cartão de Crachá",
            "Cartão de Identificação",
            "Cartão de Afiliação",
            "Cartão de Fidelidade",
            "Cartão de Desconto",
            "Cartão de Estudante",
            "Cartão de Visita",
            "Cartão de Embarque",
            "Cartão de Empréstimo",
            "Cartão de Segurança",
          ],
          required: true,
        },
        {
          name: "file",
          label: "Anexar Documento",
          type: "file",
          accept: ".pdf,.doc,.docx,.jpg,.png", // Define os formatos aceitos
          required: true,
        },
      ]}
      onSubmit={handleSubmit}
      onClose={onClose}
    />
  );
}
