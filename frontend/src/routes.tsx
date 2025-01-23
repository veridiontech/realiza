import { createBrowserRouter } from "react-router-dom";
import { AppLayout } from "./_layouts/app";
import { AuthLayout } from "./_layouts/auth";
import { ConfigurationLayout } from "./_layouts/configurantion";
import { EnterpriseLayout } from "./_layouts/enterprise";
import { ForgotPassword } from "./pages/app/forgot-password";
import { NewPassword } from "./pages/app/new-password";
import { NewPassword2 } from "./pages/app/new-password2";
import { SignIn } from "./pages/app/sign-in";
import MonittoringBis from "./pages/auth/bis";
import { Collaborators } from "./pages/auth/collaborators";
import { Dashboard } from "./pages/auth/dashboard";
import { Enterprise } from "./pages/auth/enterprises/enterprise";
import { MonittoringTable } from "./pages/auth/monittoringTable";
import { ProfileClient } from "./pages/auth/profileClient";
import { EditProfile } from "./pages/auth/profileEdit";
import { ProfileUser } from "./pages/auth/profileUser/__profileUser";
import { SelectClient } from "./pages/auth/selectClient";
import { RealizaHome } from "./pages/auth/userRealiza/realizaHome";
import { ServiceProvider } from "./pages/auth/serviceProviders/ServiceProviders";
import ContractsTable from "./pages/auth/contracts/contracts";
import { EmployeesTable } from "./pages/auth/employee/employees";
import { EmailLayout } from "./_layouts/email";
import { SignUpPageEmail } from "./pages/auth/emailPages/signUpPageEmail";
import { EnterprisePageEmail } from "./pages/auth/emailPages/enterprisePageEmail";
import { DetailsEmployee } from "./pages/auth/employee/detailsEmployee";
import { ProfileEnterpriseReprise } from "./pages/auth/profileEnterprise/__profile-enterprise";
import { DocumentViewer } from "./pages/auth/employee/modals/viewDoc";
import { AtualizationPage } from "./pages/auth/atualizationsPage";
import { Branch } from "./pages/auth/branchs/branch";
import { UserProvider } from "./context/user-provider";
import EmployeeToContract from "./pages/auth/serviceProviders/employeeToContract";

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
      { path: "detailsEmployees/:id/:id", element: <DetailsEmployee /> },
      { path: "viewDoc", element: <DocumentViewer /> },
      { path: "branch/:id", element: <Branch /> },
      { path: "new-features/:id", element: <AtualizationPage /> },
      { path: "employee-to-contract", element: <EmployeeToContract /> },
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
