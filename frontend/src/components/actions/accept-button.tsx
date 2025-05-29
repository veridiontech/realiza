import { CheckCircle } from "lucide-react";
import { Button } from "@/components/ui/button";

interface AcceptButtonProps {
  onAccept: () => void;
}

export function AcceptButton({ onAccept }: AcceptButtonProps) {
  return (
    <Button
      className="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded-md"
      onClick={onAccept}
    >
      <CheckCircle size={18} className="mr-1" />
      Aceitar
    </Button>
  );
}
