import "./global.css";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Helmet, HelmetProvider } from "react-helmet-async";
import { RouterProvider } from "react-router-dom"; // Somente o RouterProvider
import { router } from "./routes"; // Certifique-se de que foi criado com `createBrowserRouter`
import { FormDataProvider } from "./context/formDataProvider";
import { ThemeProvider } from "./context/Theme-Provider";
// import { UserProvider } from "./context/user-provider";
import { Toaster } from "sonner";
import { ClientProvider } from "./context/Client-Provider";
import { DocumentProvider } from "./context/Document-provider";
import { BranchProvider } from "./context/Branch-provider";
import { SupplierProvider } from "./context/Supplier-context";

const queryClient = new QueryClient();

export function App() {
  return (
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <FormDataProvider>
          <HelmetProvider>
            <Toaster
              richColors
              closeButton
              expand={false}
              className="w-[20vw]"
            />
            <Helmet titleTemplate="%s | realiza" />
            <ClientProvider>
              <BranchProvider>
              <SupplierProvider>
                <DocumentProvider>
                  <RouterProvider router={router} />
                </DocumentProvider>
              </SupplierProvider>
              </BranchProvider>
            </ClientProvider>
          </HelmetProvider>
        </FormDataProvider>
      </QueryClientProvider>
    </ThemeProvider>
  );
}
