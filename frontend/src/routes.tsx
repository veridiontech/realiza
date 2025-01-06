import { createBrowserRouter } from "react-router-dom";
import { AppLayout } from "./_layouts/app";
import { AuthLayout } from "./_layouts/auth";
import MonittoringBis from "./pages/auth/bis";
import { Collaborators } from "./pages/auth/collaborators";
import { MonittoringTable } from "./pages/auth/monittoringTable";
import { ProfileClient } from "./pages/auth/profileClient";
import { SignIn } from "./pages/app/sign-in";
import { ForgotPassword } from "./pages/app/forgot-password";
import { EditProfile } from "./pages/auth/profileEdit";
import { Dashboard } from "./pages/auth/dashboard";
import { NewPassword } from "./pages/app/new-password";
import { NewPassword2 } from "./pages/app/new-password2";
import { ConfigurationLayout } from "./_layouts/configurantion";
import { ProfileUser } from "./pages/auth/profileUser/__profileUser";
import { RealizaHome } from "./pages/auth/userRealiza/realizaHome";
import { SelectClient } from "./pages/auth/selectClient";
import { Enterprise } from "./pages/auth/enterprises/enterprise";
import { EnterpriseLayout } from "./_layouts/enterprise";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      { path: "/", element: <SelectClient /> },
      { path: "/profileClient", element: <ProfileClient /> },
      { path: "/collaborators", element: <Collaborators /> },
      { path: "/bis", element: <MonittoringBis /> },
      { path: "/monittoring", element: <MonittoringTable /> },
      { path: "/editProfile", element: <EditProfile /> },
      { path: "/realizaHome", element: <RealizaHome /> },
      { path: "/selectClient", element: <SelectClient /> },
    ],
  },
  {
    path: "/",
    element: <AuthLayout />,
    children: [
      { path: "sign-in", element: <SignIn /> },
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
]);
