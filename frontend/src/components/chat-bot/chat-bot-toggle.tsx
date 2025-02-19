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
        className="bg-realizaBlue hover:bg-realizaBlue h-[4vh] w-[2vw] rounded-full p-6 text-white shadow-lg"
      >
        <BotMessageSquareIcon className="h-6 w-6" />
      </Button>
    </div>
  );
}
