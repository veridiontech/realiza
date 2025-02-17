import { useEffect, useRef, useState } from "react";
import axios from "axios";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import {
  Bot,
  BotMessageSquare,
  BotMessageSquareIcon,
  Send,
  User,
} from "lucide-react";
import CHATGPT_PROMPT from "@/prompt";
import { motion } from "framer-motion";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import { Comment } from "react-loader-spinner";

interface Message {
  sender: "user" | "bot";
  text: string;
  isLink?: boolean;
}

export function ChatBot() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = async () => {
    if (!input.trim()) return;
    setLoading(true);

    const userMessage: Message = { sender: "user", text: input };
    setMessages((prev) => [...prev, userMessage]);

    try {
      const gptResponse = await axios.post(
        "https://api.openai.com/v1/chat/completions",
        {
          model: "gpt-4",
          messages: [
            { role: "system", content: CHATGPT_PROMPT },
            { role: "user", content: input },
          ],
        },
        {
          headers: {
            Authorization: `Bearer ${import.meta.env.VITE_OPENAI_API_KEY}`,
          },
        },
      );

      const processedQuestion = gptResponse.data.choices[0].message.content;
      if (
        processedQuestion.includes(
          "Eu sou a assistente virtual da Realiza Assessoria",
        ) ||
        processedQuestion.includes(
          "Por favor, informe qual consulta deseja realizar",
        )
      ) {
        const botMessage: Message = {
          sender: "bot",
          text: processedQuestion,
        };
        setMessages((prev) => [...prev, botMessage]);
        setLoading(false);
        return;
      }
      if (
        processedQuestion.toLowerCase().includes("quantos colaboradores") ||
        processedQuestion.toLowerCase().includes("quantos funcionários") ||
        processedQuestion.toLowerCase().includes("quantos clientes") ||
        processedQuestion.toLowerCase().includes("quantos documentos") ||
        processedQuestion
          .toLowerCase()
          .includes("me mande um excel dos colaboradores")
      ) {
        const response = await axios.post("http://localhost:3001/pergunta", {
          question: processedQuestion,
        });

        if (response.data.link) {
          const botMessage: Message = {
            sender: "bot",
            text: response.data.link,
            isLink: true,
          };
          setMessages((prev) => [...prev, botMessage]);
        } else {
          const botMessage: Message = {
            sender: "bot",
            text: response.data.awnser || response.data.resposta,
          };
          setMessages((prev) => [...prev, botMessage]);
        }
      } else {
        const botMessage: Message = {
          sender: "bot",
          text: processedQuestion,
        };
        setMessages((prev) => [...prev, botMessage]);
      }
    } catch (error) {
      console.error("Erro ao enviar mensagem:", error);
      setMessages((prev) => [
        ...prev,
        { sender: "bot", text: "Erro ao processar a pergunta." },
      ]);
    }

    setLoading(false);
    setInput("");
  };

  return (
    <div className="rounded-md bg-white p-2 shadow-lg">
      <div className="flex w-[22vw] flex-col justify-center gap-5 rounded-md bg-gray-100 p-4">
        <div className="flex items-start gap-1">
          <div className="flex w-[2vw] items-center justify-center rounded-md bg-white p-1 shadow-md">
            <BotMessageSquare />
          </div>
          <div>
            <motion.p
              initial={{ opacity: 0 }}
              animate={{ opacity: 2 }}
              transition={{ duration: 0.7 }}
              className="whitespace-pre-wrap"
            >
              Olá sou a assitente da realiza, como posso ajudar?
            </motion.p>
          </div>
        </div>
        <Card className="w-full max-w-2xl shadow-lg">
          <CardContent className="space-y-4 p-4">
            <ScrollArea className="flex h-[60vh] flex-col gap-5 overflow-auto border-b border-gray-300 p-3">
              <div className="flex flex-col gap-10">
                {messages.map((msg, index) => (
                  <div
                    key={index}
                    className={`rounded-lg p-2 ${
                      msg.sender === "user"
                        ? "bg-realizaBlue flex w-auto flex-row-reverse items-center gap-1 self-end text-white"
                        : "flex w-auto items-start gap-1 self-start bg-gray-200 text-end text-black"
                    }`}
                  >
                    {msg.sender === "user" ? (
                      <User className="h-5 w-5 flex-shrink-0" />
                    ) : (
                      <Bot className="h-5 w-5 flex-shrink-0" />
                    )}
                    {msg.isLink ? (
                      <a
                        href={msg.text}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-realizaBlue underline"
                      >
                        Baixar Excel
                      </a>
                    ) : (
                      msg.text
                    )}
                  </div>
                ))}
                {loading && (
                  <div className="flex items-center gap-2">
                    <BotMessageSquareIcon className="h-5 w-5 flex-shrink-0" />
                    <div className="flip-horizontal">
                      <Comment
                        visible={true}
                        height="40"
                        width="30"
                        ariaLabel="comment-loading"
                        color="#34495D"
                        backgroundColor="#3498db"
                      />
                    </div>
                  </div>
                )}
              </div>
              <div ref={messagesEndRef} />
            </ScrollArea>
            <div className="flex gap-2">
              <Input
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Faça sua pergunta..."
                className="flex-1"
              />
              <Button
                onClick={handleSend}
                disabled={loading}
                className="bg-realizaBlue"
              >
                <Send className="h-5 w-5" />
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
