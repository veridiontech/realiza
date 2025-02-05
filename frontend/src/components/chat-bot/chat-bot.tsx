import { useState } from "react";
import axios from "axios";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Send } from "lucide-react";
import CHATGPT_PROMPT from "@/prompt";

interface Message {
  sender: "user" | "bot";
  text: string;
  isLink?: boolean;
}

export function ChatBot() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);

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

      // Verifica se a resposta do ChatGPT é uma mensagem padrão
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
        return; // Não faz a requisição para a API local
      }

      // Verifica se a pergunta foi reformulada para a API
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
        // Se a pergunta não for reformulada, exibe a resposta do ChatGPT
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
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-100 p-4">
      <Card className="w-full max-w-2xl shadow-lg">
        <CardContent className="space-y-4 p-4">
          <div className="h-96 overflow-y-auto border-b border-gray-300 p-2">
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`rounded-lg p-2 ${msg.sender === "user" ? "self-end bg-realizaBlue text-white" : "self-start bg-gray-200 text-black"}`}
              >
                {msg.isLink ? (
                  <a
                    href={msg.text}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-blue-500 underline"
                  >
                    Baixar Excel
                  </a>
                ) : (
                  msg.text
                )}
              </div>
            ))}
          </div>
          <div className="flex gap-2">
            <Input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Faça sua pergunta..."
              className="flex-1"
            />
            <Button onClick={handleSend} disabled={loading}>
              <Send className="h-5 w-5" />
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
