import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "bootstrap/dist/css/bootstrap.min.css";

import Navigation from "./components/Navigation";
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./components/Dashboard";
import CustomerList from "./components/CustomerList";
import CustomerForm from "./components/CustomerForm";
import AccountList from "./components/AccountList";
import AccountForm from "./components/AccountForm";
import TransactionList from "./components/TransactionList";
import TransactionForm from "./components/TransactionForm";
import Login from "./components/Login";
import Register from "./components/Register";
import Trading from "./components/Trading";
import Portfolio from "./components/Portfolio";
import Funding from "./components/Funding";
import TradeHistory from "./components/TradeHistory";

function App() {
  const isAuthenticated = localStorage.getItem("token");

  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <div className="App">
        <Navigation />

        {/* Main Content */}
        <div className="main-content">
          <div className="container-fluid mt-4">
            <Routes>
              {/* Public routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Protected routes */}
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customers"
                element={
                  <ProtectedRoute>
                    <CustomerList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customers/new"
                element={
                  <ProtectedRoute>
                    <CustomerForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customers/edit/:id"
                element={
                  <ProtectedRoute>
                    <CustomerForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/accounts"
                element={
                  <ProtectedRoute>
                    <AccountList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/accounts/new"
                element={
                  <ProtectedRoute>
                    <AccountForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/accounts/edit/:id"
                element={
                  <ProtectedRoute>
                    <AccountForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/transactions"
                element={
                  <ProtectedRoute>
                    <TransactionList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/transactions/new"
                element={
                  <ProtectedRoute>
                    <TransactionForm />
                  </ProtectedRoute>
                }
              />

              {/* Trading Routes */}
              <Route
                path="/trading"
                element={
                  <ProtectedRoute>
                    <Trading />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/portfolio"
                element={
                  <ProtectedRoute>
                    <Portfolio />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/funding"
                element={
                  <ProtectedRoute>
                    <Funding />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/trade-history"
                element={
                  <ProtectedRoute>
                    <TradeHistory />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </div>
        </div>
        <ToastContainer position="top-right" autoClose={3000} />
      </div>
    </Router>
  );
}

export default App;
