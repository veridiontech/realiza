import { createBrowserRouter } from "react-router-dom";
import { AppLayout } from "./_layouts/app";
import { AuthLayout } from "./_layouts/auth";
import { ConfigurationLayout } from "./_layouts/configurantion";
import { EnterpriseLayout } from "./_layouts/enterprise";
import { ForgotPassword } from "./pages/app/forgot-password";
import { NewPassword } from "./pages/app/new-password";
import { NewPassword2 } from "./pages/app/new-password2";
import { SignIn } from "./pages/app/sign-in";
import MonittoringBis from "./pages/auth/realizaProfile/bis";
import { Collaborators } from "./pages/auth/realizaProfile/collaborators";
import { Dashboard } from "./pages/auth/realizaProfile/dashboard";
import { Enterprise } from "./pages/auth/realizaProfile/enterprises/enterprise";
import { MonittoringTable } from "./pages/auth/realizaProfile/monittoringTable";
import { ProfileClient } from "./pages/auth/realizaProfile/profileClient";
import { EditProfile } from "./pages/auth/realizaProfile/profileEdit";
import { ProfileUser } from "./pages/auth/realizaProfile/profileUser/__profileUser";
import { SelectClient } from "./pages/auth/realizaProfile/selectClient";
import { RealizaHome } from "./pages/auth/realizaProfile/userRealiza/realizaHome";
import { ServiceProvider } from "./pages/auth/realizaProfile/serviceProviders/ServiceProviders";
import ContractsTable from "./pages/auth/realizaProfile/contracts/contracts";
import { EmployeesTable } from "./pages/auth/realizaProfile/employee/employees";
import { EmailLayout } from "./_layouts/email";
import { SignUpPageEmail } from "./pages/auth/realizaProfile/emailPages/signUpPageEmail";
import { EnterprisePageEmail } from "./pages/auth/realizaProfile/emailPages/enterprisePageEmail";
import { DetailsEmployee } from "./pages/auth/realizaProfile/employee/detailsEmployee";
import { ProfileEnterpriseReprise } from "./pages/auth/realizaProfile/profileEnterprise/__profile-enterprise";
// import { DocumentViewer } from "./pages/auth/employee/modals/viewDoc";
import { AtualizationPage } from "./pages/auth/realizaProfile/atualizationsPage";
import { Branch } from "./pages/auth/realizaProfile/branchs/branch";
import { UserProvider } from "./context/user-provider";
import { CreateUserRealiza } from "./pages/auth/realizaProfile/createUserRealiza/create-user-realiza";
import EmployeeToContract from "./pages/auth/realizaProfile/contracts/employeeToContract";
import { ClientAppLayout } from "./_layouts/clientApp";
import { ClientServiceProvider } from "./pages/auth/clientProfile/serviceProviders/clientServiceProviders";
import { ProtectedRoute } from "./protectedRoutes";
import ChatPage from "./pages/auth/realizaProfile/chat";
import { DocumentPage } from "./pages/auth/realizaProfile/documents/_document-page";
import { RiskMatriz } from "./pages/auth/realizaProfile/documents/risk-matriz";
import { ClientEmployee } from "./pages/auth/clientProfile/employee/clientEmployee";
import { ControlPanel } from "./pages/auth/realizaProfile/panelControl";

import { Quartered } from "./pages/auth/supplier/quartered/quartered";
import { ProviderAppLayout } from "./_layouts/providerApp";
// import { SupplierEmployee } from "./pages/auth/supplier/supplierEmployee/supplierEmployee";
import SupplierContracts from "./pages/auth/supplier/contracts/supplierContracts";
import SubContracts from "./pages/auth/subProfile/subContracts/subContracts";
import { SubContractorAppLayout } from "./_layouts/subContractorApp";
import { CreateNewManagerClient } from "./pages/auth/clientProfile/create-new-manager/create-new-manager";

export const router = createBrowserRouter([
  {
    path: "/sistema",
    element: (
      <UserProvider>
        <AppLayout />
      </UserProvider>
    ),
    children: [
      { path: "select-client/:id", element: <SelectClient /> },
      { path: "dashboard/:id", element: <Dashboard /> },
      { path: "profile/:id", element: <ProfileEnterpriseReprise /> },
      { path: "profileClient/:id", element: <ProfileClient /> },
      { path: "collaborators/:id", element: <Collaborators /> },
      { path: "bis/:id", element: <MonittoringBis /> },
      { path: "monittoring/:id", element: <MonittoringTable /> },
      { path: "editProfile/:id", element: <EditProfile /> },
      { path: "realizaHome/:id", element: <RealizaHome /> },
      { path: "selectClient/:id", element: <SelectClient /> },
      { path: "serviceProviders/:id", element: <ServiceProvider /> },
      { path: "contracts/:id", element: <ContractsTable /> },
      { path: "employees/:id", element: <EmployeesTable /> },
      { path: "detailsEmployees/:id", element: <DetailsEmployee /> },
      {
        path: "employee-to-contract/:contractId",
        element: <EmployeeToContract />,
      },
      { path: "branch/:id", element: <Branch /> },
      { path: "new-features/:id", element: <AtualizationPage /> },
      { path: "create-new-user/:id", element: <CreateUserRealiza /> },
      { path: "chat/:id", element: <ChatPage /> },
      { path: "documents/:id", element: <DocumentPage /> },
      { path: "risk-matriz/:id", element: <RiskMatriz /> },
      { path: "controlPanel", element: <ControlPanel /> },
    ],
  },

  {
    path: "/viewer",
    element: (
      <UserProvider>
        <AppLayout />
      </UserProvider>
    ),
    children: [{ path: "teste" }],
  },

  {
    path: "/cliente",
    element: (
      <UserProvider>
        <ProtectedRoute
          allowedRoles={["ROLE_CLIENT_RESPONSIBLE", "ROLE_CLIENT_MANAGER"]}
        >
          <ClientAppLayout />
        </ProtectedRoute>
      </UserProvider>
    ),
    children: [
      { path: "serviceProviders/:id", element: <ClientServiceProvider /> },
      { path: "contracts/:id", element: <ContractsTable /> },
      { path: "profile/:id", element: <ProfileEnterpriseReprise /> },
      { path: "branch/:id", element: <Branch /> },
      { path: "employees/:id", element: <ClientEmployee /> },
      { path: "create-manager/:id", element: <CreateNewManagerClient />}
    ],
  },
  {
    path: "/fornecedor",
    element: (
      <UserProvider>
        <ProtectedRoute
          allowedRoles={["ROLE_SUPPLIER_RESPONSIBLE", "ROLE_SUPPLIER_MANAGER"]}
        >
          <ProviderAppLayout />
        </ProtectedRoute>
      </UserProvider>
    ),
    children: [
      { path: "quartered/:id", element: <Quartered /> },
      { path: "contracts/:id", element: <SupplierContracts /> },
      { path: "profile/:id", element: <ProfileEnterpriseReprise /> },
      { path: "branch/:id", element: <Branch /> },
      { path: "employees/:id", element: <EmployeesTable /> },
    ],
  },
  {
    path: "/sub",
    element: (
      <UserProvider>
        <ProtectedRoute
          allowedRoles={[
            "ROLE_SUBCONTRACTOR_RESPONSIBLE",
            "ROLE_SUBCONTRACTOR_MANAGER",
          ]}
        >
          <SubContractorAppLayout />
        </ProtectedRoute>
      </UserProvider>
    ),
    children: [
      { path: "contracts/:id", element: <SubContracts /> },
      { path: "contracts/:id", element: <SupplierContracts /> },
      { path: "profile/:id", element: <ProfileEnterpriseReprise /> },
      { path: "branch/:id", element: <Branch /> },
      { path: "employees/:id", element: <EmployeesTable /> },
    ],
  },
  {
    path: "/",
    element: (
      <UserProvider>
        <AuthLayout />
      </UserProvider>
    ),
    children: [
      { path: "/", element: <SignIn /> },
      { path: "forgot-password", element: <ForgotPassword /> },
      { path: "new-password", element: <NewPassword /> },
      { path: "new-password2", element: <NewPassword2 /> },
    ],
  },
  {
    path: "/",
    element: (
      <UserProvider>
        <ConfigurationLayout />
      </UserProvider>
    ),
    children: [{ path: "profile-user/:id", element: <ProfileUser /> }],
  },

  {
    path: "/",
    element: <EnterpriseLayout />,
    children: [{ path: "enterprise", element: <Enterprise /> }],
  },
  {
    path: "/email",
    element: <EmailLayout />,
    children: [
      { path: "Sign-Up", element: <SignUpPageEmail /> },
      //{ path: "Login", element: <LoginPageEmail /> },
      { path: "Enterprise-sign-up/validate", element: <EnterprisePageEmail /> },
    ],
  },
]);
