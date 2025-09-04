import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Container, Badge } from 'react-bootstrap';
import { 
  PeopleFill, 
  Bank2, 
  CashStack, 
  GraphUp,
  PersonCheck,
  ExclamationTriangle
} from 'react-bootstrap-icons';
import axios from 'axios';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalCustomers: 0,
    activeCustomers: 0,
    totalAccounts: 0,
    activeAccounts: 0,
    totalTransactions: 0,
    totalBalance: 0
  });

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/dashboard/stats');
      setStats(response.data);
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
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

  return (
    <Container fluid>
      <h1 className="mb-4">Dashboard</h1>
      
      <Row className="mb-4">
        <Col>
          <Card className="text-center h-100">
            <Card.Body>
              <PeopleFill size={48} className="text-primary mb-3" />
              <Card.Title>Total Customers</Card.Title>
              <Card.Text className="h2 text-primary">
                {formatNumber(stats.totalCustomers)}
              </Card.Text>
              <Badge bg="success" className="mt-2">
                {stats.activeCustomers} Active
              </Badge>
            </Card.Body>
          </Card>
        </Col>
        
        <Col>
          <Card className="text-center h-100">
            <Card.Body>
              <Bank2 size={48} className="text-success mb-3" />
              <Card.Title>Total Accounts</Card.Title>
              <Card.Text className="h2 text-success">
                {formatNumber(stats.totalAccounts)}
              </Card.Text>
              <Badge bg="success" className="mt-2">
                {stats.activeAccounts} Active
              </Badge>
            </Card.Body>
          </Card>
        </Col>
        
        <Col>
          <Card className="text-center h-100">
            <Card.Body>
              <CashStack size={48} className="text-info mb-3" />
              <Card.Title>Total Balance</Card.Title>
              <Card.Text className="h2 text-info">
                {formatCurrency(stats.totalBalance)}
              </Card.Text>
              <Badge bg="info" className="mt-2">
                Across All Accounts
              </Badge>
            </Card.Body>
          </Card>
        </Col>
        
        <Col>
          <Card className="text-center h-100">
            <Card.Body>
              <GraphUp size={48} className="text-warning mb-3" />
              <Card.Title>Total Transactions</Card.Title>
              <Card.Text className="h2 text-warning">
                {formatNumber(stats.totalTransactions)}
              </Card.Text>
              <Badge bg="warning" className="mt-2">
                This Month
              </Badge>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col md={6}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">
                <PersonCheck className="me-2" />
                Recent Activity
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Total Customers</span>
                <Badge bg="success">{formatNumber(stats.totalCustomers)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Total Accounts</span>
                <Badge bg="info">{formatNumber(stats.totalAccounts)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Monthly Transactions</span>
                <Badge bg="primary">{formatNumber(stats.monthlyTransactions || 0)}</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center">
                <span>System Status</span>
                <Badge bg="success">Online</Badge>
              </div>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6}>
          <Card>
            <Card.Header>
              <h5 className="mb-0">
                <ExclamationTriangle className="me-2" />
                System Status
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Database</span>
                <Badge bg="success">Online</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>API Services</span>
                <Badge bg="success">Running</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <span>Security</span>
                <Badge bg="success">Active</Badge>
              </div>
              <div className="d-flex justify-content-between align-items-center">
                <span>Last Backup</span>
                <Badge bg="info">2 hours ago</Badge>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col>
          <Card>
            <Card.Header>
              <h5 className="mb-0">Quick Actions</h5>
            </Card.Header>
            <Card.Body>
              <div className="d-grid gap-2 d-md-flex">
                <button className="btn btn-primary me-md-2">
                  <PeopleFill className="me-2" />
                  Add New Customer
                </button>
                <button className="btn btn-success me-md-2">
                  <Bank2 className="me-2" />
                  Open New Account
                </button>
                <button className="btn btn-info me-md-2">
                  <CashStack className="me-2" />
                  Process Transaction
                </button>
                <button className="btn btn-warning">
                  <GraphUp className="me-2" />
                  Generate Report
                </button>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;
