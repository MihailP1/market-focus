import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.scss";

export default function Navbar() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar__logo">Market Focus</div>
      <ul className="navbar__links">
        <li><Link to="/">Home</Link></li>
        <li><Link to="/market">Market</Link></li>
        <li><Link to="/dashboard">User Dashboard</Link></li>
        <li><Link to="/about">About</Link></li>
      </ul>
      <div className="navbar__auth">
        {isAuthenticated ? (
          <button onClick={handleLogout}>Logout</button>
        ) : (
          <>
            <button onClick={() => navigate("/login")}>Login</button>
            <button onClick={() => navigate("/register")}>Register</button>
          </>
        )}
      </div>
    </nav>
  );
}
