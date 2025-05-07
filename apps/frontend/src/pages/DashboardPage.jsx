import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  return (
    <div>
      <h2>Dashboard</h2>
      <p>Добро пожаловать в защищенную зону приложения!</p>
    </div>
  );
}
