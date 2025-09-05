import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Container, Badge, Button, Alert } from 'react-bootstrap';
import {
  PeopleFill,
  Bank2,
  CashStack,
  GraphUp,
  PersonCheck,
  ExclamationTriangle,
  Shield,
  ArrowRight,
  Activity,
  Database,
  Lock,
  Clock
} from 'react-bootstrap-icons';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Dashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalCustomers: 0,
    activeCustomers: 0,
    totalAccounts: 0,
    activeAccounts: 0,
    totalTransactions: 0,
    totalBalance: 0
  });
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check authentication status
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    setIsAuthenticated(true);
    fetchDashboardStats();
  }, [navigate]);

  const fetchDashboardStats = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${import.meta.env.VITE_API_URL}/api/dashboard/stats`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        navigate('/login');
        return;
      }
      // Set default values if API fails
      setStats({
        totalCustomers: 0,
        activeCustomers: 0,
        totalAccounts: 0,
        activeAccounts: 0,
        totalTransactions: 0,
        totalBalance: 0
      });
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const formatNumber = (num) => {
    return new Intl.NumberFormat('en-US').format(num);
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
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div>
              <h1 className="h3 fw-bold mb-1 text-dark">
                Welcome to VaultX
              </h1>
              <p className="text-muted small">Your secure financial management platform</p>
            </div>
            <div className="d-flex align-items-center">
              <Shield size={20} className="text-success me-2" />
              <Badge bg="success" className="fs-6">System Online</Badge>
            </div>
          </div>
      
      {/* Stats Cards */}
      <Row className="mb-3 g-2 stats-row">
        <Col xl={3} lg={6} md={6} sm={12}>
          <Card className="h-100 border shadow-sm">
            <Card.Body className="text-center p-3">
              <div className="d-flex justify-content-between align-items-start mb-2">
                <PeopleFill size={28} className="text-primary" />
                <Badge bg="primary" className="px-2 py-1 small">
                  {stats.activeCustomers} Active
                </Badge>
              </div>
              <h4 className="fw-bold mb-1 text-dark">{formatNumber(stats.totalCustomers)}</h4>
              <p className="mb-0 text-muted small">Total Customers</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col xl={3} lg={6} md={6} sm={12}>
          <Card className="h-100 border shadow-sm">
            <Card.Body className="text-center p-3">
              <div className="d-flex justify-content-between align-items-start mb-2">
                <Bank2 size={28} className="text-success" />
                <Badge bg="success" className="px-2 py-1 small">
                  {stats.activeAccounts} Active
                </Badge>
              </div>
              <h4 className="fw-bold mb-1 text-dark">{formatNumber(stats.totalAccounts)}</h4>
              <p className="mb-0 text-muted small">Total Accounts</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col xl={3} lg={6} md={6} sm={12}>
          <Card className="h-100 border shadow-sm">
            <Card.Body className="text-center p-3">
              <div className="d-flex justify-content-between align-items-start mb-2">
                <CashStack size={28} className="text-info" />
                <Badge bg="info" className="px-2 py-1 small">
                  Total Assets
                </Badge>
              </div>
              <h4 className="fw-bold mb-1 text-dark">{formatCurrency(stats.totalBalance)}</h4>
              <p className="mb-0 text-muted small">Total Balance</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col xl={3} lg={6} md={6} sm={12}>
          <Card className="h-100 border shadow-sm">
            <Card.Body className="text-center p-3">
              <div className="d-flex justify-content-between align-items-start mb-2">
                <GraphUp size={28} className="text-warning" />
                <Badge bg="warning" className="px-2 py-1 small">
                  This Month
                </Badge>
              </div>
              <h4 className="fw-bold mb-1 text-dark">{formatNumber(stats.totalTransactions)}</h4>
              <p className="mb-0 text-muted small">Transactions</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Activity and Status Section */}
      <Row className="mb-3 g-2 activity-row">
        <Col xl={6} lg={12}>
          <Card className="h-100 border-0 shadow">
            <Card.Header className="bg-primary text-white border-0 py-2">
              <h6 className="mb-0 d-flex align-items-center">
                <Activity className="me-2" size={16} />
                System Activity
              </h6>
            </Card.Header>
            <Card.Body className="p-3">
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <PeopleFill className="text-primary me-2" size={18} />
                  <span className="fw-semibold small">Total Customers</span>
                </div>
                <Badge bg="primary" className="small px-2 py-1">{formatNumber(stats.totalCustomers)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Bank2 className="text-success me-2" size={18} />
                  <span className="fw-semibold small">Total Accounts</span>
                </div>
                <Badge bg="success" className="small px-2 py-1">{formatNumber(stats.totalAccounts)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <GraphUp className="text-info me-2" size={18} />
                  <span className="fw-semibold small">Monthly Transactions</span>
                </div>
                <Badge bg="info" className="small px-2 py-1">{formatNumber(stats.monthlyTransactions || 0)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Shield className="text-success me-2" size={18} />
                  <span className="fw-semibold small">System Status</span>
                </div>
                <Badge bg="success" className="small px-2 py-1">Online</Badge>
              </div>
            </Card.Body>
          </Card>
        </Col>
        
        <Col xl={6} lg={12}>
          <Card className="h-100 border-0 shadow">
            <Card.Header className="bg-success text-white border-0 py-2">
              <h6 className="mb-0 d-flex align-items-center">
                <Database className="me-2" size={16} />
                System Health
              </h6>
            </Card.Header>
            <Card.Body className="p-3">
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Database className="text-success me-2" size={18} />
                  <span className="fw-semibold small">Database</span>
                </div>
                <Badge bg="success" className="small px-2 py-1">Online</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Activity className="text-success me-2" size={18} />
                  <span className="fw-semibold small">API Services</span>
                </div>
                <Badge bg="success" className="small px-2 py-1">Running</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Lock className="text-success me-2" size={18} />
                  <span className="fw-semibold small">Security</span>
                </div>
                <Badge bg="success" className="small px-2 py-1">Active</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center p-2 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex align-items-center">
                  <Clock className="text-info me-2" size={18} />
                  <span className="fw-semibold small">Last Backup</span>
                </div>
                <Badge bg="info" className="small px-2 py-1">2 hours ago</Badge>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

    </Container>
  );
};

export default Dashboard;
