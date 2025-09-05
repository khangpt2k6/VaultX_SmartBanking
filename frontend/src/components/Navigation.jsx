import React, { useState } from 'react';
import { Navbar, Nav, Container, NavDropdown, Badge, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { Shield, PeopleFill, CreditCard, ArrowLeftRight, BoxArrowRight, Person, PersonPlus, List } from 'react-bootstrap-icons';

const Navigation = ({ onToggleSidebar }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const isAuthenticated = localStorage.getItem('token');

  return (
        <Navbar bg="dark" variant="dark" expand="lg" className="mb-4 shadow">
      <Container fluid>
        <Navbar.Brand as={Link} to="/" className="d-flex align-items-center fw-bold fs-3">
          <Shield className="me-2" />
          <span className="text-white">
            VaultX
          </span>
          <Badge bg="success" className="ms-2">Pro</Badge>
        </Navbar.Brand>
        
        {/* Mobile sidebar toggle */}
        {isAuthenticated && (
          <Button 
            variant="outline-light" 
            className="d-lg-none me-2"
            onClick={onToggleSidebar}
          >
            <List size={20} />
          </Button>
        )}
        
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/" className="fw-semibold px-3">
              <i className="bi bi-speedometer2 me-1"></i>
              Dashboard
            </Nav.Link>
            
            <NavDropdown 
              title={
                <span className="fw-semibold">
                  <PeopleFill className="me-1" />
                  Customers
                </span>
              } 
              id="customers-dropdown"
              className="px-2"
            >
              <NavDropdown.Item as={Link} to="/customers" className="d-flex align-items-center">
                <PeopleFill className="me-2" />
                View All Customers
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="/customers/new" className="d-flex align-items-center">
                <PersonPlus className="me-2" />
                Add New Customer
              </NavDropdown.Item>
            </NavDropdown>
            
            <NavDropdown 
              title={
                <span className="fw-semibold">
                  <CreditCard className="me-1" />
                  Accounts
                </span>
              } 
              id="accounts-dropdown"
              className="px-2"
            >
              <NavDropdown.Item as={Link} to="/accounts" className="d-flex align-items-center">
                <CreditCard className="me-2" />
                View All Accounts
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="/accounts/new" className="d-flex align-items-center">
                <CreditCard className="me-2" />
                Open New Account
              </NavDropdown.Item>
            </NavDropdown>
            
            <NavDropdown 
              title={
                <span className="fw-semibold">
                  <ArrowLeftRight className="me-1" />
                  Transactions
                </span>
              } 
              id="transactions-dropdown"
              className="px-2"
            >
              <NavDropdown.Item as={Link} to="/transactions" className="d-flex align-items-center">
                <ArrowLeftRight className="me-2" />
                View All Transactions
              </NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item as={Link} to="/transactions/new" className="d-flex align-items-center">
                <ArrowLeftRight className="me-2" />
                New Transaction
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>
          
          <Nav className="ms-auto">
            {isAuthenticated ? (
              <Nav.Link onClick={handleLogout} className="fw-semibold px-3 d-flex align-items-center">
                <BoxArrowRight className="me-1" />
                Logout
              </Nav.Link>
            ) : (
              <>
                <Nav.Link as={Link} to="/login" className="fw-semibold px-3 d-flex align-items-center">
                  <Person className="me-1" />
                  Login
                </Nav.Link>
                <Nav.Link as={Link} to="/register" className="fw-semibold px-3 d-flex align-items-center">
                  <PersonPlus className="me-1" />
                  Register
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Navigation;
