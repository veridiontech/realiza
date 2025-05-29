import { XCircle } from "lucide-react";
import { Button } from "@/components/ui/button";

interface RejectButtonProps {
  onReject: () => void;
}

export function RejectButton({ onReject }: RejectButtonProps) {
  return (
    <Button
      className="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded-md"
      onClick={onReject}
    >
      <XCircle size={18} className="mr-1" />
      Recusar
    </Button>
  );
}
