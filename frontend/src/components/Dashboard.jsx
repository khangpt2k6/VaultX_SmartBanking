import React, { useState, useEffect } from "react";
import { Row, Col, Container } from "react-bootstrap";
import {
  PeopleFill,
  Bank2,
  CashStack,
  GraphUp,
  Shield,
} from "react-bootstrap-icons";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { StatCard } from "./ui/GlassCard";

const Dashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalCustomers: 0,
    activeCustomers: 0,
    totalAccounts: 0,
    activeAccounts: 0,
    totalTransactions: 0,
    totalBalance: 0,
  });
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check authentication status
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }
    setIsAuthenticated(true);
    fetchDashboardStats();
  }, [navigate]);

  const fetchDashboardStats = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(
        `${
          import.meta.env.VITE_API_URL || "http://localhost:8080"
        }/api/dashboard/stats`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setStats(response.data);
    } catch (error) {
      console.error("Error fetching dashboard stats:", error);
      if (error.response?.status === 401) {
        localStorage.removeItem("token");
        navigate("/login");
        return;
      }
      // Set default values if API fails
      setStats({
        totalCustomers: 0,
        activeCustomers: 0,
        totalAccounts: 0,
        activeAccounts: 0,
        totalTransactions: 0,
        totalBalance: 0,
      });
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  const formatNumber = (num) => {
    return new Intl.NumberFormat("en-US").format(num);
  };

  // Show loading or redirect if not authenticated
  if (!isAuthenticated) {
    return (
      <Container fluid className="px-4">
        <div className="text-center mt-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-3">Redirecting to login...</p>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid className="px-4">
      {/* Header Section */}
      <div className="welcome animate-fade-in-up d-flex justify-content-between align-items-center mb-5">
        <div>
          <h1 className="text-gradient fw-bold mb-2">Welcome to VaultX</h1>
          <p className="text-secondary">
            Your secure financial management platform
          </p>
        </div>
        <div className="d-flex align-items-center gap-2">
          <Shield size={20} color="white" />
          <span className="badge-glass">System Online</span>
        </div>
      </div>

      {/* Stats Cards */}
      <Row className="mb-5 g-3 stats-row">
        <Col xl={3} lg={6} md={6} sm={12}>
          <StatCard
            icon={PeopleFill}
            label="Total Customers"
            value={formatNumber(stats.totalCustomers)}
            badge={`${stats.activeCustomers} Active`}
          />
        </Col>

        <Col xl={3} lg={6} md={6} sm={12}>
          <StatCard
            icon={Bank2}
            label="Total Accounts"
            value={formatNumber(stats.totalAccounts)}
            badge={`${stats.activeAccounts} Active`}
          />
        </Col>

        <Col xl={3} lg={6} md={6} sm={12}>
          <StatCard
            icon={CashStack}
            label="Total Balance"
            value={formatCurrency(stats.totalBalance)}
            badge="Total Assets"
          />
        </Col>

        <Col xl={3} lg={6} md={6} sm={12}>
          <StatCard
            icon={GraphUp}
            label="Transactions"
            value={formatNumber(stats.totalTransactions)}
            badge="This Month"
          />
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
