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
//import { LoginPageEmail } from "./pages/auth/emailPages/login-page";
import { SignUpPageEmail } from "./pages/auth/emailPages/signUpPageEmail";
import { EnterprisePageEmail } from "./pages/auth/emailPages/enterprisePageEmail";
import { DetailsEmployee } from "./pages/auth/employee/detailsEmployee";
import { ProfileEnterpriseReprise } from "./pages/auth/profileEnterprise/__profile-enterprise";

export const router = createBrowserRouter([
  {
    path: "/sistema",
    element: <AppLayout />,
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
    ],
  },
  {
    path: "/",
    element: <AuthLayout />,
    children: [
      { path: "/", element: <SignIn /> },
      { path: "forgot-password", element: <ForgotPassword /> },
      { path: "new-password", element: <NewPassword /> },
      { path: "new-password2", element: <NewPassword2 /> },
    ],
  },
  {
    path: "/",
    element: <ConfigurationLayout />,
    children: [{ path: "profile-user", element: <ProfileUser /> }],
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
      { path: "Enterprise-sign-up", element: <EnterprisePageEmail /> },
    ],
  },
]);
