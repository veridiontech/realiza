import { Outlet } from "react-router-dom";

import { Header } from "@/components/header/realizaHeader";
// import { ChatBotToggle } from "@/components/chat-bot/chat-bot-toggle";
import { useUser } from "@/context/user-provider";
import { Blocks } from "react-loader-spinner";

export function AppLayout() {
  const { authUser, loading } = useUser();

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Blocks
          height="80"
          width="80"
          color="#34495E"
          ariaLabel="blocks-loading"
          wrapperStyle={{}}
          wrapperClass="blocks-wrapper"
          visible={true}
        />
      </div>
    );
  }

  if (!authUser) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        pagina nao autenticada
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col">
      <Header />
      <div className="dark:bg-primary-foreground flex-grow bg-[#F4F4F5]">
        <Outlet />
        {/* <ChatBotToggle /> */}
      </div>
    </div>
  );
}
