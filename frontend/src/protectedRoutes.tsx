// ProtectedRoute.tsx
import { ReactNode, useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import axios from "axios";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";

interface ProtectedRouteProps {
  allowedRoles: string[];
  children: ReactNode;
}

export function ProtectedRoute({
  allowedRoles,
  children,
}: ProtectedRouteProps) {
  const { user, setUser } = useUser();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchUser() {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const userId = localStorage.getItem("userId");
      const role = localStorage.getItem("role")
      if (tokenFromStorage && userId && role) {
        let url = "";
        switch (role) {
          case "ROLE_ADMIN":
          case "ROLE_REALIZA_PLUS":
          case "ROLE_REALIZA_BASIC":
            url = `${ip}/user/manager/${userId}`;
            break;
          case "ROLE_CLIENT_RESPONSIBLE":
          case "ROLE_CLIENT_MANAGER":
            url = `${ip}/user/client/${userId}`;
            break;
          case "ROLE_SUPPLIER_RESPONSIBLE":
          case "ROLE_SUPPLIER_MANAGER":
            url = `${ip}/user/supplier/${userId}`;
            break;
          case "ROLE_SUBCONTRACTOR_RESPONSIBLE":
          case "ROLE_SUBCONTRACTOR_MANAGER":
            url = `${ip}/user/subcontractor/${userId}`;
            break;
          case "ROLE_VIEWER":
            url = `${ip}/user/viewer/${userId}`;
            break;
          default:
            url = "";
        }
        if (url) {
          try {
            const res = await axios.get(url, {
              headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            setUser(res.data);
          } catch (error) {}
        }
      }
      setLoading(false);
    }
    fetchUser();
  }, [setUser]);

  if (loading) return null;
  if (!user) return <Navigate to="/" replace />;
  if (!allowedRoles.includes(user.role)) {
    return user.role === "ROLE_CLIENT_RESPONSIBLE" ? (
      <Navigate to="/cliente" replace />
    ) : (
      <Navigate to="/sistema" replace />
    );
  }
  return <>{children}</>;
}
