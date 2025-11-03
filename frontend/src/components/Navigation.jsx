import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  GraphUp,
  Bank2,
  CreditCard,
  ArrowLeftRight,
  BoxArrowRight,
  Person,
  PersonPlus,
  List,
  X,
  ChevronDown,
} from "react-bootstrap-icons";
import "../styles/navigation.css";

const Navigation = () => {
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [desktopDropdownOpen, setDesktopDropdownOpen] = useState(null);
  const [mobileDropdownOpen, setMobileDropdownOpen] = useState(null);
  const [isClicking, setIsClicking] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/login");
  };

  const isAuthenticated = localStorage.getItem("token");

  const toggleDesktopDropdown = (name) => {
    setIsClicking(true);
    setDesktopDropdownOpen(desktopDropdownOpen === name ? null : name);
    // Reset clicking flag after a short delay
    setTimeout(() => setIsClicking(false), 200);
  };

  const toggleMobileDropdown = (name) => {
    setMobileDropdownOpen(mobileDropdownOpen === name ? null : name);
  };

  return (
    <nav className="navbar-glass">
      <div className="navbar-container">
        {/* Logo */}
        <Link to="/" className="navbar-brand">
          <div className="navbar-logo-icon">V</div>
          <span>VaultX</span>
        </Link>

        {/* Desktop Navigation */}
        <div className="navbar-menu-desktop">
          {/* Banking Dropdown */}
          <div
            className={`nav-dropdown ${
              desktopDropdownOpen === "banking" ? "open" : ""
            }`}
            onMouseLeave={() => {
              // Don't close if user just clicked (prevents immediate close on click)
              if (!isClicking) {
                setDesktopDropdownOpen(null);
              }
            }}
          >
            <button
              className="nav-link dropdown-toggle"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                toggleDesktopDropdown("banking");
              }}
              onMouseEnter={() => {
                if (desktopDropdownOpen !== "banking") {
                  setDesktopDropdownOpen("banking");
                }
              }}
            >
              <span>Banking</span>
              <ChevronDown size={12} className="chevron-icon" />
            </button>
            <div 
              className="dropdown-menu"
              onMouseEnter={() => setDesktopDropdownOpen("banking")}
              onMouseLeave={() => setDesktopDropdownOpen(null)}
            >
              {/* View Section */}
              <div className="dropdown-section-divider">
                <span className="dropdown-section-label">View</span>
              </div>
              <Link
                to="/accounts"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <CreditCard size={16} />
                Accounts
              </Link>
              <Link
                to="/customers"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <Bank2 size={16} />
                Customers
              </Link>
              <Link
                to="/transactions"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <ArrowLeftRight size={16} />
                Transactions
              </Link>
              {/* Actions Section */}
              <div className="dropdown-section-divider">
                <span className="dropdown-section-label">Actions</span>
              </div>
              <Link
                to="/customers/new"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <PersonPlus size={16} />
                Add Customer
              </Link>
              <Link
                to="/accounts/new"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <Bank2 size={16} />
                New Account
              </Link>
              <Link
                to="/transactions/new"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <ArrowLeftRight size={16} />
                New Transaction
              </Link>
            </div>
          </div>

          {/* Trading Dropdown */}
          <div
            className={`nav-dropdown ${
              desktopDropdownOpen === "trading" ? "open" : ""
            }`}
            onMouseLeave={() => {
              // Don't close if user just clicked (prevents immediate close on click)
              if (!isClicking) {
                setDesktopDropdownOpen(null);
              }
            }}
          >
            <button
              className="nav-link dropdown-toggle"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                toggleDesktopDropdown("trading");
              }}
              onMouseEnter={() => {
                if (desktopDropdownOpen !== "trading") {
                  setDesktopDropdownOpen("trading");
                }
              }}
            >
              <span>Trading</span>
              <ChevronDown size={12} className="chevron-icon" />
            </button>
            <div 
              className="dropdown-menu"
              onMouseEnter={() => setDesktopDropdownOpen("trading")}
              onMouseLeave={() => setDesktopDropdownOpen(null)}
            >
              <Link
                to="/trading"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <GraphUp size={16} />
                Trading
              </Link>
              <Link
                to="/portfolio"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <CreditCard size={16} />
                Portfolio
              </Link>
              <Link
                to="/funding"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <CreditCard size={16} />
                Deposit
              </Link>
              <Link
                to="/trade-history"
                className="dropdown-item"
                onClick={() => setDesktopDropdownOpen(null)}
              >
                <ArrowLeftRight size={16} />
                Trade History
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
                <BoxArrowRight size={16} />
                <span>Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="navbar-login-link">
                Login
              </Link>
              <Link to="/register" className="navbar-register-btn">
                Register
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
          {/* Banking Dropdown */}
          <div className="mobile-dropdown">
            <button
              className="mobile-dropdown-toggle"
              onClick={() => toggleMobileDropdown("banking")}
            >
              <Bank2 size={18} />
              <span>Banking</span>
              <ChevronDown
                size={14}
                className={`chevron-icon ${
                  mobileDropdownOpen === "banking" ? "open" : ""
                }`}
              />
            </button>
            {mobileDropdownOpen === "banking" && (
              <div className="mobile-dropdown-menu">
                <div className="mobile-dropdown-section-label">View</div>
                <Link
                  to="/accounts"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Accounts
                </Link>
                <Link
                  to="/customers"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Customers
                </Link>
                <Link
                  to="/transactions"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Transactions
                </Link>
                <div className="mobile-dropdown-section-label">Actions</div>
                <Link
                  to="/customers/new"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Add Customer
                </Link>
                <Link
                  to="/accounts/new"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  New Account
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

          {/* Trading Dropdown */}
          <div className="mobile-dropdown">
            <button
              className="mobile-dropdown-toggle"
              onClick={() => toggleMobileDropdown("trading")}
            >
              <GraphUp size={18} />
              <span>Trading</span>
              <ChevronDown
                size={14}
                className={`chevron-icon ${
                  mobileDropdownOpen === "trading" ? "open" : ""
                }`}
              />
            </button>
            {mobileDropdownOpen === "trading" && (
              <div className="mobile-dropdown-menu">
                <Link
                  to="/trading"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Trading
                </Link>
                <Link
                  to="/portfolio"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Portfolio
                </Link>
                <Link
                  to="/funding"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Deposit
                </Link>
                <Link
                  to="/trade-history"
                  className="mobile-dropdown-item"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Trade History
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
