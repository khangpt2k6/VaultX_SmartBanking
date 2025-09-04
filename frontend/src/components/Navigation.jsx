import React from 'react';
import { Navbar, Nav, Container, NavDropdown } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';

const Navigation = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const isAuthenticated = localStorage.getItem('token');

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="mb-4">
      <Container>
        <Navbar.Brand as={Link} to="/">
          🏦 Bank Management System
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">
              Dashboard
            </Nav.Link>
            <NavDropdown title="Customers" id="customers-dropdown">
              <NavDropdown.Item as={Link} to="/customers">
                View All Customers
              </NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/customers/new">
                Add New Customer
              </NavDropdown.Item>
            </NavDropdown>
            <NavDropdown title="Accounts" id="accounts-dropdown">
              <NavDropdown.Item as={Link} to="/accounts">
                View All Accounts
              </NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/accounts/new">
                Open New Account
              </NavDropdown.Item>
            </NavDropdown>
            <NavDropdown title="Transactions" id="transactions-dropdown">
              <NavDropdown.Item as={Link} to="/transactions">
                View All Transactions
              </NavDropdown.Item>
              <NavDropdown.Item as={Link} to="/transactions/new">
                New Transaction
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>
          <Nav>
            {isAuthenticated ? (
              <>
                <Nav.Link onClick={handleLogout}>
                  Logout
                </Nav.Link>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login">
                  Login
                </Nav.Link>
                <Nav.Link as={Link} to="/register">
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
