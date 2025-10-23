import React, { useState, useEffect } from "react";
import { Spinner, Row, Col, Container } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import axios from "axios";
import { GlassForm, GlassInput } from "./ui/GlassForm";
import { GlassButton } from "./ui/GlassButton";

const CustomerForm = () => {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    address: "",
    dateOfBirth: "",
    isActive: true,
  });
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);

  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    if (id) {
      setIsEdit(true);
      fetchCustomer(id);
    }
  }, [id]);

  const fetchCustomer = async (customerId) => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/customers/${customerId}`);
      setFormData(response.data);
    } catch (error) {
      console.error("Error fetching customer:", error);
      toast.error("Failed to fetch customer data");
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
      if (isEdit) {
        await axios.put(
          `${import.meta.env.VITE_API_URL}/api/customers/${id}`,
          formData
        );
        toast.success("Customer updated successfully");
      } else {
        await axios.post(
          `${import.meta.env.VITE_API_URL}/api/customers`,
          formData
        );
        toast.success("Customer created successfully");
      }
      navigate("/customers");
    } catch (error) {
      console.error("Error saving customer:", error);
      toast.error(`Failed to ${isEdit ? "update" : "create"} customer`);
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
    <Container fluid className="px-4 py-5">
      <div
        style={{ maxWidth: "700px", margin: "0 auto" }}
        className="animate-fade-in-up"
      >
        <h2 className="text-gradient fw-bold mb-4">
          {isEdit ? "Edit Customer" : "Add New Customer"}
        </h2>

        <GlassForm onSubmit={handleSubmit}>
          <Row className="g-2">
            <Col md={6}>
              <GlassInput
                label="First Name"
                type="text"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                required
              />
            </Col>
            <Col md={6}>
              <GlassInput
                label="Last Name"
                type="text"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                required
              />
            </Col>
          </Row>

          <Row className="g-2">
            <Col md={6}>
              <GlassInput
                label="Email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </Col>
            <Col md={6}>
              <GlassInput
                label="Phone"
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </Col>
          </Row>

          <GlassInput
            label="Address"
            type="text"
            name="address"
            value={formData.address}
            onChange={handleChange}
            required
          />

          <Row className="g-2">
            <Col md={6}>
              <GlassInput
                label="Date of Birth"
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleChange}
                required
              />
            </Col>
            <Col md={6} className="d-flex align-items-end">
              <div className="mb-4 w-100">
                <label
                  className="form-check-label cursor-pointer"
                  style={{ color: "rgba(255,255,255,0.8)" }}
                >
                  <input
                    type="checkbox"
                    name="isActive"
                    checked={formData.isActive}
                    onChange={handleChange}
                    className="form-check-input me-2"
                  />
                  Active Customer
                </label>
              </div>
            </Col>
          </Row>

          <div className="d-flex gap-2 mt-4">
            <GlassButton type="submit" variant="primary" disabled={loading}>
              {loading ? (
                <>
                  <Spinner size="sm" className="me-2" />
                  {isEdit ? "Updating..." : "Creating..."}
                </>
              ) : isEdit ? (
                "Update Customer"
              ) : (
                "Create Customer"
              )}
            </GlassButton>
            <GlassButton
              type="button"
              variant="default"
              onClick={() => navigate("/customers")}
            >
              Cancel
            </GlassButton>
          </div>
        </GlassForm>
      </div>
    </Container>
  );
};

export default CustomerForm;
