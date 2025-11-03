import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Table,
  ProgressBar,
  Spinner,
  Card,
  Alert,
  Button,
  Badge,
} from "react-bootstrap";
import {
  Wallet2,
  CreditCard,
  Bank,
  CurrencyBitcoin,
  ArrowRight,
  CheckCircle,
  Clock,
  FileEarmarkText,
} from "react-bootstrap-icons";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";

const Funding = () => {
  const [depositAmount, setDepositAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("CARD");
  const [userBalance, setUserBalance] = useState(0);
  const [deposits, setDeposits] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalDeposited, setTotalDeposited] = useState(0);
  const [userId, setUserId] = useState(null);

  const token = localStorage.getItem("token");

  // Get userId from localStorage (either directly or from user object)
  useEffect(() => {
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      setUserId(storedUserId);
    } else {
      const userStr = localStorage.getItem("user");
      if (userStr) {
        try {
          const user = JSON.parse(userStr);
          if (user.userId) {
            setUserId(user.userId);
            // Also store it separately for future use
            localStorage.setItem("userId", user.userId);
          }
        } catch (e) {
          console.error("Error parsing user object:", e);
        }
      }
    }
  }, []);

  useEffect(() => {
    if (userId && token) {
      loadUserBalance();
      loadDepositHistory();
    } else {
      console.warn("User ID or token not available");
    }
  }, [userId, token]);

  const loadUserBalance = async () => {
    if (!userId || !token) {
      console.warn("Cannot load balance: userId or token missing");
      return;
    }
    try {
      const response = await axios.get(
        `${API_BASE_URL}/accounts/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (response.data.accounts && response.data.accounts.length > 0) {
        setUserBalance(response.data.accounts[0].balance);
      }
    } catch (error) {
      console.error("Error loading balance:", error);
    }
  };

  const loadDepositHistory = async () => {
    if (!userId || !token) {
      console.warn("Cannot load deposit history: userId or token missing");
      return;
    }
    try {
      const response = await axios.get(
        `${API_BASE_URL}/deposit/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const depositList = response.data.deposits || [];
      setDeposits(depositList);
      const total = depositList.reduce(
        (sum, d) => sum + parseFloat(d.amount),
        0
      );
      setTotalDeposited(total);
    } catch (error) {
      console.error("Error loading deposit history:", error);
    }
  };

  const processDeposit = async (e) => {
    e.preventDefault();

    if (!userId || !token) {
      toast.error("Please log in again");
      return;
    }

    if (!depositAmount || parseFloat(depositAmount) <= 0) {
      toast.error("Please enter a valid deposit amount");
      return;
    }

    setLoading(true);
    try {
      const response = await axios.post(
        `${API_BASE_URL}/deposit/process`,
        null,
        {
          params: {
            userId,
            amount: parseFloat(depositAmount),
            paymentMethod,
          },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      toast.success(
        `✅ Deposit processed! Your account has been credited with $${depositAmount}`
      );
      setDepositAmount("");
      loadUserBalance();
      loadDepositHistory();
    } catch (error) {
      toast.error(error.response?.data?.message || "Failed to process deposit");
    } finally {
      setLoading(false);
    }
  };

  const quickDepositAmounts = [100, 500, 1000, 5000, 10000];

  return (
    <Container fluid className="py-4" style={{ maxWidth: "1400px" }}>
      {/* Header */}
      <Row className="mb-5">
        <Col>
          <div className="d-flex align-items-center gap-3 mb-2">
            <Wallet2 size={32} color="var(--primary-blue)" />
            <h1 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
              Instant Funding
            </h1>
          </div>
          <p className="text-muted mb-0" style={{ paddingLeft: "2.75rem", color: "var(--text-tertiary)" }}>
            Deposit funds into your trading account instantly
          </p>
        </Col>
      </Row>

      {/* Balance Cards */}
      <Row className="mb-4 g-3">
        <Col md={6}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div>
                  <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Current Balance
                  </p>
                  <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    ${userBalance.toFixed(2)}
                  </h3>
                  <p className="mb-0 mt-2" style={{ fontSize: "0.85rem", color: "var(--text-tertiary)" }}>
                    Total Deposited: ${totalDeposited.toFixed(2)}
                  </p>
                </div>
                <Wallet2 size={40} color="var(--primary-blue)" style={{ opacity: 0.15 }} />
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={6}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div>
                  <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Recent Deposits
                  </p>
                  <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    {deposits.length}
                  </h3>
                  <p className="mb-0 mt-2" style={{ fontSize: "0.85rem", color: "var(--text-tertiary)" }}>
                    Total Transactions
                  </p>
                </div>
                <FileEarmarkText size={40} color="var(--primary-blue)" style={{ opacity: 0.15 }} />
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Deposit Form */}
      <Row className="mb-4 g-4">
        <Col lg={7}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <Wallet2 size={20} color="var(--primary-blue)" />
                Deposit Funds
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              <Form onSubmit={processDeposit}>
                <Form.Group className="mb-4">
                  <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                    Deposit Amount
                  </Form.Label>
                  <Form.Control
                    type="number"
                    placeholder="Enter amount"
                    value={depositAmount}
                    onChange={(e) => setDepositAmount(e.target.value)}
                    step="0.01"
                    min="0"
                    style={{
                      borderRadius: "var(--radius-md)",
                      borderColor: "var(--border-medium)",
                      background: "var(--bg-input)",
                      padding: "0.85rem 1rem",
                      fontSize: "1rem",
                    }}
                  />
                  <Form.Text style={{ color: "var(--text-tertiary)", fontSize: "0.85rem", marginTop: "0.5rem", display: "block" }}>
                    Minimum: $10 | Maximum: $100,000
                  </Form.Text>
                </Form.Group>

                <Form.Group className="mb-4">
                  <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                    Payment Method
                  </Form.Label>
                  <Form.Select
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    style={{
                      borderRadius: "var(--radius-md)",
                      borderColor: "var(--border-medium)",
                      background: "var(--bg-input)",
                    }}
                  >
                    <option value="CARD">Credit/Debit Card</option>
                    <option value="BANK_TRANSFER">Bank Transfer</option>
                    <option value="WIRE">Wire Transfer</option>
                    <option value="CRYPTO">Cryptocurrency</option>
                  </Form.Select>
                </Form.Group>

                <div
                  className="mb-4 p-3"
                  style={{
                    background: "var(--bg-secondary)",
                    borderRadius: "var(--radius-md)",
                    border: "1px solid var(--border-light)",
                  }}
                >
                  <div className="d-flex align-items-center gap-2 mb-2">
                    <Clock size={16} color="var(--primary-blue)" />
                    <strong style={{ color: "var(--text-primary)", fontSize: "0.9rem" }}>Instant Processing</strong>
                  </div>
                  <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-tertiary)" }}>
                    Funds are credited to your account immediately after processing.
                  </p>
                </div>

                <Button
                  variant="primary"
                  size="lg"
                  className="w-100 d-inline-flex align-items-center justify-content-center gap-2"
                  type="submit"
                  disabled={loading}
                  style={{
                    borderRadius: "var(--radius-full)",
                    fontWeight: 600,
                    padding: "0.85rem",
                    border: "none",
                  }}
                >
                  {loading ? (
                    <>
                      <Spinner size="sm" />
                      Processing Deposit...
                    </>
                  ) : (
                    <>
                      <ArrowRight size={18} />
                      Deposit Now
                    </>
                  )}
                </Button>
              </Form>

              {/* Quick Deposit Amounts */}
              <div className="mt-4 pt-4 border-top" style={{ borderColor: "var(--border-light) !important" }}>
                <p className="mb-3" style={{ fontWeight: 600, color: "var(--text-primary)", fontSize: "0.9rem" }}>
                  Quick Deposit:
                </p>
                <div className="d-flex gap-2 flex-wrap">
                  {quickDepositAmounts.map((amount) => (
                    <Button
                      key={amount}
                      variant="outline-primary"
                      size="sm"
                      onClick={() => setDepositAmount(amount.toString())}
                      style={{
                        borderRadius: "var(--radius-md)",
                        borderColor: "var(--border-medium)",
                        fontWeight: 600,
                        padding: "0.5rem 1rem",
                      }}
                    >
                      ${amount}
                    </Button>
                  ))}
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* Payment Methods Info */}
        <Col lg={5}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <CreditCard size={20} color="var(--primary-blue)" />
                Payment Methods
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              <div className="payment-method mb-3 pb-3 border-bottom" style={{ borderColor: "var(--border-light) !important" }}>
                <div className="d-flex align-items-center gap-2 mb-2">
                  <CreditCard size={18} color="var(--primary-blue)" />
                  <h6 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    Credit/Debit Card
                  </h6>
                </div>
                <p className="mb-1" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Processing Time: <strong style={{ color: "var(--text-primary)" }}>Instant</strong>
                </p>
                <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Fee: <strong style={{ color: "var(--text-primary)" }}>0%</strong>
                </p>
              </div>
              <div className="payment-method mb-3 pb-3 border-bottom" style={{ borderColor: "var(--border-light) !important" }}>
                <div className="d-flex align-items-center gap-2 mb-2">
                  <Bank size={18} color="var(--primary-blue)" />
                  <h6 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    Bank Transfer
                  </h6>
                </div>
                <p className="mb-1" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Processing Time: <strong style={{ color: "var(--text-primary)" }}>1-2 Business Days</strong>
                </p>
                <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Fee: <strong style={{ color: "var(--text-primary)" }}>0%</strong>
                </p>
              </div>
              <div className="payment-method mb-3 pb-3 border-bottom" style={{ borderColor: "var(--border-light) !important" }}>
                <div className="d-flex align-items-center gap-2 mb-2">
                  <ArrowRight size={18} color="var(--primary-blue)" />
                  <h6 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    Wire Transfer
                  </h6>
                </div>
                <p className="mb-1" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Processing Time: <strong style={{ color: "var(--text-primary)" }}>1-3 Hours</strong>
                </p>
                <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Fee: <strong style={{ color: "var(--text-primary)" }}>$0 - $25</strong>
                </p>
              </div>
              <div className="payment-method mb-4">
                <div className="d-flex align-items-center gap-2 mb-2">
                  <CurrencyBitcoin size={18} color="var(--primary-blue)" />
                  <h6 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    Cryptocurrency
                  </h6>
                </div>
                <p className="mb-1" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Processing Time: <strong style={{ color: "var(--text-primary)" }}>Instant - 30 min</strong>
                </p>
                <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
                  Fee: <strong style={{ color: "var(--text-primary)" }}>1%</strong>
                </p>
              </div>

              <div
                className="p-3"
                style={{
                  background: "var(--bg-secondary)",
                  borderRadius: "var(--radius-md)",
                  border: "1px solid var(--border-light)",
                }}
              >
                <div className="d-flex align-items-center gap-2 mb-2">
                  <CheckCircle size={16} color="var(--primary-blue)" />
                  <strong style={{ color: "var(--text-primary)", fontSize: "0.9rem" }}>Security Notice</strong>
                </div>
                <p className="mb-0" style={{ fontSize: "0.85rem", color: "var(--text-tertiary)" }}>
                  All deposits are secured with industry-standard encryption and fraud protection.
                </p>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Deposit History */}
      <Row>
        <Col md={12}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <FileEarmarkText size={20} color="var(--primary-blue)" />
                Deposit History
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {deposits.length > 0 ? (
                <div className="table-responsive">
                  <Table hover responsive className="mb-0" style={{ borderCollapse: "separate", borderSpacing: 0 }}>
                    <thead>
                      <tr style={{ background: "var(--bg-secondary)" }}>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Deposit ID
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Amount
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Payment Method
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Date
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Status
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Reference
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {deposits.map((deposit, idx) => (
                        <tr
                          key={deposit.depositId}
                          style={{
                            borderBottom: idx < deposits.length - 1 ? "1px solid var(--border-light)" : "none",
                          }}
                          onMouseEnter={(e) => {
                            e.currentTarget.style.background = "var(--bg-hover)";
                          }}
                          onMouseLeave={(e) => {
                            e.currentTarget.style.background = "transparent";
                          }}
                        >
                          <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>
                            <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>#{deposit.depositId}</span>
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 700 }}>
                            ${deposit.amount.toFixed(2)}
                          </td>
                          <td style={{ padding: "1rem" }}>
                            <Badge
                              style={{
                                background: "var(--primary-blue-light)",
                                color: "var(--primary-blue)",
                                padding: "0.3rem 0.85rem",
                                borderRadius: "var(--radius-full)",
                                fontWeight: 600,
                                fontSize: "0.75rem",
                              }}
                            >
                              {deposit.paymentMethod}
                            </Badge>
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-tertiary)", fontSize: "0.9rem" }}>
                            {new Date(deposit.depositDate).toLocaleString()}
                          </td>
                          <td style={{ padding: "1rem" }}>
                            <Badge
                              style={{
                                background: deposit.depositStatus === "COMPLETED" ? "rgba(40, 167, 69, 0.15)" : "rgba(255, 193, 7, 0.15)",
                                color: deposit.depositStatus === "COMPLETED" ? "#28a745" : "#ffc107",
                                padding: "0.3rem 0.85rem",
                                borderRadius: "var(--radius-full)",
                                fontWeight: 600,
                                fontSize: "0.75rem",
                              }}
                            >
                              {deposit.depositStatus === "COMPLETED" ? (
                                <CheckCircle size={12} className="me-1" />
                              ) : (
                                <Clock size={12} className="me-1" />
                              )}
                              {deposit.depositStatus}
                            </Badge>
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-secondary)", fontSize: "0.85rem", fontFamily: "monospace" }}>
                            {deposit.transactionReference}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              ) : (
                <div className="text-center py-5">
                  <Wallet2 size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0" style={{ color: "var(--text-tertiary)" }}>
                    No deposits yet. Make your first deposit to get started!
                  </p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Funding;
