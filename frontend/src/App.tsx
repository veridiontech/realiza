import "./global.css";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Helmet, HelmetProvider } from "react-helmet-async";
import { RouterProvider } from "react-router-dom";
import { router } from "./routes";
import { FormDataProvider } from "./context/formDataProvider";
import { ThemeProvider } from "./context/Theme-Provider";
import { UserProvider } from "./context/user-provider";
import { Toaster } from 'sonner'
import { ClientProvider } from "./context/Client-Provider";


const queryClient = new QueryClient();

export function App() {
  return (
    <ThemeProvider>
      <UserProvider>
        <ClientProvider>
        <QueryClientProvider client={queryClient}>
          <FormDataProvider>
            <HelmetProvider>
            <Toaster richColors closeButton expand={false} className="w-[20vw]"/>
              <Helmet titleTemplate="%s | realiza" />
              <RouterProvider router={router} />
            </HelmetProvider>
          </FormDataProvider>
        </QueryClientProvider>
        </ClientProvider>
      </UserProvider>
    </ThemeProvider>
  );
}
