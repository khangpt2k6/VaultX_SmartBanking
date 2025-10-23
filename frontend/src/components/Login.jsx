import React, { useState } from "react";
import { Spinner } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { GlassForm, GlassInput } from "./ui/GlassForm";
import { GlassButton } from "./ui/GlassButton";

const Login = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, formData);
      const { token, user } = response.data;

      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(user));

      toast.success("Login successful!");
      navigate("/");
    } catch (error) {
      console.error("Login error:", error);
      toast.error("Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="min-vh-100 d-flex align-items-center justify-content-center"
      style={{ padding: "2rem" }}
    >
      <div style={{ width: "100%", maxWidth: "420px" }}>
        <div className="text-center mb-5 animate-fade-in-up">
          <h1
            className="text-gradient fw-bold mb-2"
            style={{ fontSize: "2.5rem" }}
          >
            VaultX
          </h1>
          <p className="text-secondary">Secure Financial Management</p>
        </div>

        <GlassCard size="lg" className="animate-fade-in-up">
          <h4 className="text-center mb-4 fw-bold">Login to Your Account</h4>

          <GlassForm onSubmit={handleSubmit}>
            <GlassInput
              label="Email Address"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="your@email.com"
            />

            <GlassInput
              label="Password"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Enter your password"
            />

            <GlassButton
              type="submit"
              variant="primary"
              className="w-100 mb-3"
              disabled={loading}
            >
              {loading ? (
                <>
                  <Spinner size="sm" className="me-2" />
                  Logging in...
                </>
              ) : (
                "Login"
              )}
            </GlassButton>
          </GlassForm>

          <div className="text-center">
            <p className="text-secondary small mb-0">
              Don't have an account?{" "}
              <Link
                to="/register"
                className="text-white fw-bold text-decoration-none"
              >
                Register here
              </Link>
            </p>
          </div>
        </GlassCard>
      </div>
    </div>
  );
};

const GlassCard = ({ children, size = "md", className = "" }) => {
  const sizeClasses = {
    sm: "p-3",
    md: "p-4",
    lg: "p-6",
  };

  return (
    <div className={`card-glass ${sizeClasses[size]} ${className}`}>
      {children}
    </div>
  );
};

export default Login;
