import { useState } from "react";
import { Button } from "@/components/ui/button";
import { ChatBot } from "./chat-bot";
import { BotMessageSquareIcon } from "lucide-react";

export function ChatBotToggle() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="fixed bottom-4 right-10 flex flex-col items-end">
      {isOpen && (
        <div>
          <ChatBot />
        </div>
      )}
      <Button
        onClick={() => setIsOpen(!isOpen)}
        className="p-3 rounded-full bg-realizaBlue hover:bg-blue-600 text-white shadow-lg"
      >
        <BotMessageSquareIcon className="h-6 w-6" />
      </Button>
    </div>
  );
}
