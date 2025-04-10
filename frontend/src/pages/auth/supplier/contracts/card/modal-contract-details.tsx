import { jsPDF } from "jspdf";

const generatePDF = (contract: any) => {
  const doc = new jsPDF();
  doc.setFontSize(18);
  doc.text(`Contrato: ${contract.serviceName}`, 20, 20);
  doc.setFontSize(12);
  doc.text(`Descrição: ${contract.description}`, 20, 30);
  doc.text(`Data de Início: ${contract.dateStart}`, 20, 40);
  doc.save(`${contract.serviceName}_contrato.pdf`);
};

interface ModalProps {
  contract: any;
  closeModal: () => void;
}

export function ModalContractDetails({ contract, closeModal }: ModalProps) {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div className="border-l-realizaBlue border-l-8 bg-white p-6 rounded-lg w-1/2">
        <button onClick={closeModal} className="text-gray-500 float-right text-2xl">×</button>
        <h2 className="text-xl font-bold">{contract.serviceName}</h2>
        <p className="mt-2"><strong>Descrição:</strong> {contract.description}</p>
        <p className="mt-2"><strong>Data de Início:</strong> {contract.dateStart}</p>
        <div className="mt-4">
          <button
            className="bg-realizaBlue text-white p-2 rounded-lg hover:bg-gray-600"
            onClick={() => generatePDF(contract)} 
          >
            Baixar PDF
          </button>
        </div>
      </div>
    </div>
  );
}
