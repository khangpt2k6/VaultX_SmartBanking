import React, { useState, useEffect } from "react";
import { Row, Col, Container } from "react-bootstrap";
import {
  PeopleFill,
  Bank2,
  CashStack,
  GraphUp,
  Shield,
  Activity,
  Database,
  Lock,
  Clock,
} from "react-bootstrap-icons";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { StatCard, GlassCard } from "./ui/GlassCard";

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
          <Shield size={20} />
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

      {/* Activity and Status Section */}
      <Row className="mb-5 g-3 activity-row">
        <Col xl={6} lg={12}>
          <GlassCard size="lg">
            <div className="d-flex align-items-center gap-2 mb-4 pb-3 border-bottom border-secondary">
              <Activity size={18} />
              <h5 className="mb-0 text-white">System Activity</h5>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <PeopleFill size={16} />
                <span className="small text-white">Total Customers</span>
              </div>
              <span className="badge-glass">
                {formatNumber(stats.totalCustomers)}
              </span>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <Bank2 size={16} />
                <span className="small text-white">Total Accounts</span>
              </div>
              <span className="badge-glass">
                {formatNumber(stats.totalAccounts)}
              </span>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <GraphUp size={16} />
                <span className="small text-white">Monthly Transactions</span>
              </div>
              <span className="badge-glass">
                {formatNumber(stats.monthlyTransactions || 0)}
              </span>
            </div>
            <div className="d-flex justify-content-between align-items-center p-2">
              <div className="d-flex align-items-center gap-2">
                <Shield size={16} />
                <span className="small text-white">System Status</span>
              </div>
              <span className="badge-glass">Online</span>
            </div>
          </GlassCard>
        </Col>

        <Col xl={6} lg={12}>
          <GlassCard size="lg">
            <div className="d-flex align-items-center gap-2 mb-4 pb-3 border-bottom border-secondary">
              <Database size={18} />
              <h5 className="mb-0 text-white">System Health</h5>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <Database size={16} />
                <span className="small text-white">Database</span>
              </div>
              <span className="badge-glass">Online</span>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <Activity size={16} />
                <span className="small text-white">API Services</span>
              </div>
              <span className="badge-glass">Running</span>
            </div>
            <div className="d-flex justify-content-between align-items-center mb-3 p-2">
              <div className="d-flex align-items-center gap-2">
                <Lock size={16} />
                <span className="small text-white">Security</span>
              </div>
              <span className="badge-glass">Active</span>
            </div>
            <div className="d-flex justify-content-between align-items-center p-2">
              <div className="d-flex align-items-center gap-2">
                <Clock size={16} />
                <span className="small text-white">Last Backup</span>
              </div>
              <span className="badge-glass">2 hours ago</span>
            </div>
          </GlassCard>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
