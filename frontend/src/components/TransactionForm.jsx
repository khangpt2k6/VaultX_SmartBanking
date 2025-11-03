import React, { useState, useEffect } from "react";
import { Spinner, Row, Col, Container, Card, Button, Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { GlassForm, GlassInput, GlassSelect } from "./ui/GlassForm";
import { GlassButton } from "./ui/GlassButton";

const TransactionForm = () => {
  const [formData, setFormData] = useState({
    transactionType: "DEPOSIT",
    amount: 0,
    accountId: "",
    destinationAccountId: "",
    description: "",
  });
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);

  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    fetchAccounts();
    if (id) {
      setIsEdit(true);
      fetchTransaction(id);
    }
  }, [id]);

  const fetchAccounts = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`${API_BASE_URL}/accounts`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      // Handle both array and object responses
      let accountsData = [];
      if (Array.isArray(response.data)) {
        accountsData = response.data;
      } else if (response.data && response.data.accounts && Array.isArray(response.data.accounts)) {
        accountsData = response.data.accounts;
      }
      setAccounts(accountsData);
    } catch (error) {
      console.error("Error fetching accounts:", error);
      setAccounts([]);
    }
  };

  const fetchTransaction = async (transactionId) => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      const response = await axios.get(`${API_BASE_URL}/transactions/${transactionId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setFormData(response.data);
    } catch (error) {
      console.error("Error fetching transaction:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        navigate("/login");
      } else {
        toast.error("Failed to fetch transaction data");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => {
      const newFormData = {
        ...prev,
        [name]: value,
      };

      // Clear destination account if transaction type is not TRANSFER
      if (name === "transactionType" && value !== "TRANSFER") {
        newFormData.destinationAccountId = "";
      }

      return newFormData;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const token = localStorage.getItem("token");
      const headers = {
        Authorization: `Bearer ${token}`,
      };

      if (isEdit) {
        await axios.put(`${API_BASE_URL}/transactions/${id}`, formData, { headers });
        toast.success("Transaction updated successfully");
      } else {
        await axios.post(`${API_BASE_URL}/transactions`, formData, { headers });
        toast.success("Transaction created successfully");
      }
      navigate("/transactions");
    } catch (error) {
      console.error("Error saving transaction:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        navigate("/login");
      } else {
        toast.error(`Failed to ${isEdit ? "update" : "create"} transaction`);
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading && isEdit) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-8">
        <Card>
          <Card.Header>
            <h4>{isEdit ? "Edit Transaction" : "New Transaction"}</h4>
          </Card.Header>
          <Card.Body>
            <Form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Transaction Type *</Form.Label>
                    <Form.Select
                      name="transactionType"
                      value={formData.transactionType}
                      onChange={handleChange}
                      required
                    >
                      <option value="DEPOSIT">Deposit</option>
                      <option value="WITHDRAWAL">Withdrawal</option>
                      <option value="TRANSFER">Transfer</option>
                    </Form.Select>
                  </Form.Group>
                </div>
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Amount *</Form.Label>
                    <Form.Control
                      type="number"
                      step="0.01"
                      min="0.01"
                      name="amount"
                      value={formData.amount}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                </div>
              </div>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Account *</Form.Label>
                    <Form.Select
                      name="accountId"
                      value={formData.accountId}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Select an account</option>
                      {accounts.map((account) => (
                        <option
                          key={account.accountId}
                          value={account.accountId}
                        >
                          {account.accountNumber} - {account.customerName}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </div>
                {formData.transactionType === "TRANSFER" && (
                  <div className="col-md-6">
                    <Form.Group className="mb-3">
                      <Form.Label>Destination Account *</Form.Label>
                      <Form.Select
                        name="destinationAccountId"
                        value={formData.destinationAccountId}
                        onChange={handleChange}
                        required={formData.transactionType === "TRANSFER"}
                      >
                        <option value="">Select destination account</option>
                        {accounts
                          .filter(
                            (account) =>
                              account.accountId !== parseInt(formData.accountId)
                          )
                          .map((account) => (
                            <option
                              key={account.accountId}
                              value={account.accountId}
                            >
                              {account.accountNumber} - {account.customerName}
                            </option>
                          ))}
                      </Form.Select>
                    </Form.Group>
                  </div>
                )}
              </div>

              <Form.Group className="mb-3">
                <Form.Label>Description</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={3}
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Enter transaction description..."
                />
              </Form.Group>

              <div className="d-flex gap-2">
                <Button type="submit" variant="primary" disabled={loading}>
                  {loading ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      {isEdit ? "Updating..." : "Creating..."}
                    </>
                  ) : isEdit ? (
                    "Update Transaction"
                  ) : (
                    "Create Transaction"
                  )}
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => navigate("/transactions")}
                >
                  Cancel
                </Button>
              </div>
            </Form>
          </Card.Body>
        </Card>
      </div>
    </div>
  );
};

export default TransactionForm;
