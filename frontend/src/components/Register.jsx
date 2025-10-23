import React, { useState } from "react";
import { Spinner, Row, Col } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { GlassForm, GlassInput } from "./ui/GlassForm";
import { GlassButton } from "./ui/GlassButton";

const Register = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    address: "",
    phone: "",
    dateOfBirth: "",
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

    if (formData.password !== formData.confirmPassword) {
      toast.error("Passwords do not match");
      return;
    }

    setLoading(true);

    try {
      const { confirmPassword, ...userData } = formData;
      await axios.post(
        `${import.meta.env.VITE_API_URL}/api/auth/register`,
        userData
      );

      toast.success("Registration successful! Please login.");
      navigate("/login");
    } catch (error) {
      console.error("Registration error:", error);
      toast.error("Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="min-vh-100 d-flex align-items-center justify-content-center"
      style={{ padding: "2rem" }}
    >
      <div style={{ width: "100%", maxWidth: "600px" }}>
        <div className="text-center mb-5 animate-fade-in-up">
          <h1
            className="text-gradient fw-bold mb-2"
            style={{ fontSize: "2.5rem" }}
          >
            VaultX
          </h1>
          <p className="text-secondary">Create Your Secure Account</p>
        </div>

        <GlassCard size="lg" className="animate-fade-in-up">
          <h4 className="text-center mb-4 fw-bold">Register</h4>

          <GlassForm onSubmit={handleSubmit}>
            <Row className="g-2">
              <Col md={6}>
                <GlassInput
                  label="First Name"
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                  placeholder="First name"
                />
              </Col>
              <Col md={6}>
                <GlassInput
                  label="Last Name"
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                  placeholder="Last name"
                />
              </Col>
            </Row>

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
              label="Phone Number"
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              required
              placeholder="+1 (555) 000-0000"
            />

            <GlassInput
              label="Address"
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
              placeholder="Your address"
            />

            <GlassInput
              label="Date of Birth"
              type="date"
              name="dateOfBirth"
              value={formData.dateOfBirth}
              onChange={handleChange}
              required
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

            <GlassInput
              label="Confirm Password"
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              placeholder="Confirm your password"
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
                  Registering...
                </>
              ) : (
                "Register"
              )}
            </GlassButton>
          </GlassForm>

          <div className="text-center">
            <p className="text-secondary small mb-0">
              Already have an account?{" "}
              <Link
                to="/login"
                className="text-white fw-bold text-decoration-none"
              >
                Login here
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

export default Register;
