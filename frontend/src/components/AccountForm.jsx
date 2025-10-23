import React, { useState, useEffect } from "react";
import { Spinner, Row, Col, Container } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { GlassForm, GlassInput, GlassSelect } from "./ui/GlassForm";
import { GlassButton } from "./ui/GlassButton";

const AccountForm = () => {
  const [formData, setFormData] = useState({
    accountNumber: "",
    accountType: "SAVINGS",
    balance: 0,
    customerId: "",
    interestRate: 0.02,
    status: "ACTIVE",
  });
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);

  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    fetchCustomers();
    if (id) {
      setIsEdit(true);
      fetchAccount(id);
    }
  }, [id]);

  const fetchCustomers = async () => {
    try {
      const response = await axios.get(
        `${import.meta.env.VITE_API_URL}/api/customers`
      );
      setCustomers(response.data);
    } catch (error) {
      console.error("Error fetching customers:", error);
      setCustomers([]);
    }
  };

  const fetchAccount = async (accountId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/accounts/${accountId}`);
      setFormData(response.data);
    } catch (error) {
      console.error("Error fetching account:", error);
      toast.error("Failed to fetch account data");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Convert data to match backend expectations
      const accountData = {
        ...formData,
        balance: parseFloat(formData.balance),
        interestRate: parseFloat(formData.interestRate),
        customerId: parseInt(formData.customerId),
      };

      console.log("📤 Sending account data:", accountData);

      if (isEdit) {
        await axios.put(
          `${import.meta.env.VITE_API_URL}/api/accounts/${id}`,
          accountData
        );
        toast.success("Account updated successfully");
      } else {
        await axios.post(
          `${import.meta.env.VITE_API_URL}/api/accounts`,
          accountData
        );
        toast.success("Account created successfully");
      }
      navigate("/accounts");
    } catch (error) {
      console.error("Error saving account:", error);
      toast.error(`Failed to ${isEdit ? "update" : "create"} account`);
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
            <h4>{isEdit ? "Edit Account" : "Open New Account"}</h4>
          </Card.Header>
          <Card.Body>
            <Form onSubmit={handleSubmit}>
              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Account Number *</Form.Label>
                    <Form.Control
                      type="text"
                      name="accountNumber"
                      value={formData.accountNumber}
                      onChange={handleChange}
                      required
                      disabled={isEdit}
                    />
                  </Form.Group>
                </div>
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Account Type *</Form.Label>
                    <Form.Select
                      name="accountType"
                      value={formData.accountType}
                      onChange={handleChange}
                      required
                    >
                      <option value="SAVINGS">Savings</option>
                      <option value="CHECKING">Checking</option>
                      <option value="FIXED_DEPOSIT">Fixed Deposit</option>
                    </Form.Select>
                  </Form.Group>
                </div>
              </div>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Customer *</Form.Label>
                    <Form.Select
                      name="customerId"
                      value={formData.customerId}
                      onChange={handleChange}
                      required
                      disabled={isEdit}
                    >
                      <option value="">Select a customer</option>
                      {customers.map((customer) => (
                        <option
                          key={customer.customerId}
                          value={customer.customerId}
                        >
                          {customer.firstName} {customer.lastName}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </div>
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Initial Balance</Form.Label>
                    <Form.Control
                      type="number"
                      step="0.01"
                      min="0"
                      name="balance"
                      value={formData.balance}
                      onChange={handleChange}
                      disabled={isEdit}
                    />
                  </Form.Group>
                </div>
              </div>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Interest Rate (%)</Form.Label>
                    <Form.Control
                      type="number"
                      step="0.01"
                      min="0"
                      max="100"
                      name="interestRate"
                      value={formData.interestRate}
                      onChange={handleChange}
                    />
                  </Form.Group>
                </div>
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Account Status</Form.Label>
                    <Form.Select
                      name="status"
                      value={formData.status}
                      onChange={handleChange}
                    >
                      <option value="ACTIVE">Active</option>
                      <option value="INACTIVE">Inactive</option>
                      <option value="SUSPENDED">Suspended</option>
                    </Form.Select>
                  </Form.Group>
                </div>
              </div>

              <div className="d-flex gap-2">
                <Button type="submit" variant="primary" disabled={loading}>
                  {loading ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      {isEdit ? "Updating..." : "Creating..."}
                    </>
                  ) : isEdit ? (
                    "Update Account"
                  ) : (
                    "Create Account"
                  )}
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => navigate("/accounts")}
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

export default AccountForm;
