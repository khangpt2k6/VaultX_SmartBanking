import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Shield,
  PeopleFill,
  CreditCard,
  ArrowLeftRight,
  BoxArrowRight,
  Person,
  PersonPlus,
  List,
  X,
} from "react-bootstrap-icons";
import "../styles/navigation.css";

const Navigation = ({ onToggleSidebar }) => {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  const isAuthenticated = localStorage.getItem("token");

  const toggleDropdown = (name) => {
    setDropdownOpen(dropdownOpen === name ? null : name);
  };

  return (
    <nav className="navbar-glass">
      <div className="navbar-container">
        {/* Logo */}
        <Link to="/" className="navbar-brand">
          <Shield size={32} />
          <span>VaultX</span>
          <span
            className="badge-glass"
            style={{ marginLeft: "0.5rem", fontSize: "0.65rem" }}
          >
            Pro
          </span>
        </Link>

        {/* Desktop Navigation */}
        <div className="navbar-menu-desktop">
          <Link to="/" className="nav-link">
            Dashboard
          </Link>

          <div className="nav-dropdown">
            <button className="nav-link dropdown-toggle">
              <PeopleFill size={18} />
              <span>Customers</span>
            </button>
            <div className="dropdown-menu">
              <Link to="/customers" className="dropdown-item">
                <PeopleFill size={16} />
                View All Customers
              </Link>
              <Link to="/customers/new" className="dropdown-item">
                <PersonPlus size={16} />
                Add New Customer
              </Link>
            </div>
          </div>

          <div className="nav-dropdown">
            <button className="nav-link dropdown-toggle">
              <CreditCard size={18} />
              <span>Accounts</span>
            </button>
            <div className="dropdown-menu">
              <Link to="/accounts" className="dropdown-item">
                <CreditCard size={16} />
                View All Accounts
              </Link>
              <Link to="/accounts/new" className="dropdown-item">
                <CreditCard size={16} />
                Open New Account
              </Link>
            </div>
          </div>

          <div className="nav-dropdown">
            <button className="nav-link dropdown-toggle">
              <ArrowLeftRight size={18} />
              <span>Transactions</span>
            </button>
            <div className="dropdown-menu">
              <Link to="/transactions" className="dropdown-item">
                <ArrowLeftRight size={16} />
                View All Transactions
              </Link>
              <Link to="/transactions/new" className="dropdown-item">
                <ArrowLeftRight size={16} />
                New Transaction
              </Link>
            </div>
          </div>
        </div>

        {/* Right side - Auth or Menu Toggle */}
        <div className="navbar-right">
          {isAuthenticated ? (
            <>
              {/* Desktop Auth */}
              <button onClick={handleLogout} className="nav-link logout-btn">
                <BoxArrowRight size={18} />
                <span>Logout</span>
              </button>

              {/* Mobile sidebar toggle */}
              <button
                className="menu-toggle d-lg-none"
                onClick={onToggleSidebar}
              >
                <List size={24} />
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">
                <Person size={18} />
                <span>Login</span>
              </Link>
              <Link to="/register" className="nav-link">
                <PersonPlus size={18} />
                <span>Register</span>
              </Link>
            </>
          )}

          {/* Mobile menu toggle */}
          <button
            className="mobile-menu-toggle"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? <X size={24} /> : <List size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile Navigation Menu */}
      {mobileMenuOpen && (
        <div className="mobile-menu">
          <Link
            to="/"
            className="mobile-nav-link"
            onClick={() => setMobileMenuOpen(false)}
          >
            Dashboard
          </Link>

          <div className="mobile-dropdown">
            <button
              className="mobile-dropdown-toggle"
              onClick={() => toggleDropdown("customers")}
            >
              <PeopleFill size={18} />
              <span>Customers</span>
            </button>
            {dropdownOpen === "customers" && (
              <div className="mobile-dropdown-menu">
                <Link
                  to="/customers"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  View All Customers
                </Link>
                <Link
                  to="/customers/new"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Add New Customer
                </Link>
              </div>
            )}
          </div>

          <div className="mobile-dropdown">
            <button
              className="mobile-dropdown-toggle"
              onClick={() => toggleDropdown("accounts")}
            >
              <CreditCard size={18} />
              <span>Accounts</span>
            </button>
            {dropdownOpen === "accounts" && (
              <div className="mobile-dropdown-menu">
                <Link
                  to="/accounts"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  View All Accounts
                </Link>
                <Link
                  to="/accounts/new"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Open New Account
                </Link>
              </div>
            )}
          </div>

          <div className="mobile-dropdown">
            <button
              className="mobile-dropdown-toggle"
              onClick={() => toggleDropdown("transactions")}
            >
              <ArrowLeftRight size={18} />
              <span>Transactions</span>
            </button>
            {dropdownOpen === "transactions" && (
              <div className="mobile-dropdown-menu">
                <Link
                  to="/transactions"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  View All Transactions
                </Link>
                <Link
                  to="/transactions/new"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  New Transaction
                </Link>
              </div>
            )}
          </div>

          {isAuthenticated && (
            <button
              onClick={() => {
                handleLogout();
                setMobileMenuOpen(false);
              }}
              className="mobile-nav-link logout-btn"
            >
              <BoxArrowRight size={18} />
              <span>Logout</span>
            </button>
          )}
        </div>
      )}
    </nav>
  );
};

export default Navigation;
