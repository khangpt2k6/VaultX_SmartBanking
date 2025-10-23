import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Table,
  ProgressBar,
  Spinner,
} from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";
import { GlassCard } from "./ui/GlassCard";
import { GlassButton } from "./ui/GlassButton";

const Funding = () => {
  const [depositAmount, setDepositAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("CARD");
  const [userBalance, setUserBalance] = useState(0);
  const [deposits, setDeposits] = useState([]);
  const [loading, setLoading] = useState(false);
  const [totalDeposited, setTotalDeposited] = useState(0);

  const API_BASE_URL =
    import.meta.env.VITE_API_URL || "http://localhost:8080/api";
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    loadUserBalance();
    loadDepositHistory();
  }, []);

  const loadUserBalance = async () => {
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
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col md={12}>
          <h1 className="mb-2">💳 Instant Funding</h1>
          <p className="text-muted">
            Deposit funds into your trading account instantly
          </p>
        </Col>
      </Row>

      {/* Balance Card */}
      <Row className="mb-4">
        <Col md={6}>
          <Card className="bg-success text-white shadow-sm">
            <Card.Body className="text-center py-4">
              <h6 className="mb-2">Current Balance</h6>
              <h1>${userBalance.toFixed(2)}</h1>
              <p className="mb-0 mt-3">
                Total Deposited: ${totalDeposited.toFixed(2)}
              </p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={6}>
          <Card className="bg-info text-white shadow-sm">
            <Card.Body className="text-center py-4">
              <h6 className="mb-2">Recent Deposits</h6>
              <h1>{deposits.length}</h1>
              <p className="mb-0 mt-3">Total Transactions</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Deposit Form */}
      <Row className="mb-4">
        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">💰 Deposit Funds</h5>
            </Card.Header>
            <Card.Body>
              <Form onSubmit={processDeposit}>
                <Form.Group className="mb-3">
                  <Form.Label>Deposit Amount</Form.Label>
                  <Form.Control
                    type="number"
                    placeholder="Enter amount"
                    value={depositAmount}
                    onChange={(e) => setDepositAmount(e.target.value)}
                    step="0.01"
                    min="0"
                    className="form-control-lg"
                  />
                  <Form.Text className="text-muted">
                    Minimum: $10 | Maximum: $100,000
                  </Form.Text>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Payment Method</Form.Label>
                  <Form.Select
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  >
                    <option value="CARD">Credit/Debit Card</option>
                    <option value="BANK_TRANSFER">Bank Transfer</option>
                    <option value="WIRE">Wire Transfer</option>
                    <option value="CRYPTO">Cryptocurrency</option>
                  </Form.Select>
                </Form.Group>

                <Alert variant="info" className="mb-3">
                  <h6>⚡ Instant Processing</h6>
                  <p className="mb-0">
                    Funds are credited to your account immediately after
                    processing.
                  </p>
                </Alert>

                <Button
                  variant="success"
                  size="lg"
                  className="w-100"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? "Processing Deposit..." : "Deposit Now"}
                </Button>
              </Form>

              {/* Quick Deposit Amounts */}
              <div className="mt-4">
                <p className="mb-2">
                  <strong>Quick Deposit:</strong>
                </p>
                <div className="d-flex gap-2 flex-wrap">
                  {quickDepositAmounts.map((amount) => (
                    <Button
                      key={amount}
                      variant="outline-primary"
                      size="sm"
                      onClick={() => setDepositAmount(amount.toString())}
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
        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">💳 Payment Methods</h5>
            </Card.Header>
            <Card.Body>
              <div className="payment-method mb-3 pb-3 border-bottom">
                <h6>💳 Credit/Debit Card</h6>
                <p className="text-muted mb-1">Processing Time: Instant</p>
                <p className="text-muted mb-0">Fee: 0%</p>
              </div>
              <div className="payment-method mb-3 pb-3 border-bottom">
                <h6>🏦 Bank Transfer</h6>
                <p className="text-muted mb-1">
                  Processing Time: 1-2 Business Days
                </p>
                <p className="text-muted mb-0">Fee: 0%</p>
              </div>
              <div className="payment-method mb-3 pb-3 border-bottom">
                <h6>💸 Wire Transfer</h6>
                <p className="text-muted mb-1">Processing Time: 1-3 Hours</p>
                <p className="text-muted mb-0">Fee: $0 - $25</p>
              </div>
              <div className="payment-method">
                <h6>₿ Cryptocurrency</h6>
                <p className="text-muted mb-1">
                  Processing Time: Instant - 30 min
                </p>
                <p className="text-muted mb-0">Fee: 1%</p>
              </div>

              <Alert variant="warning" className="mt-4 mb-0">
                <h6>⚠️ Security Notice</h6>
                <p className="mb-0">
                  All deposits are secured with industry-standard encryption and
                  fraud protection.
                </p>
              </Alert>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Deposit History */}
      <Row>
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">📊 Deposit History</h5>
            </Card.Header>
            <Card.Body>
              {deposits.length > 0 ? (
                <Table striped hover responsive>
                  <thead>
                    <tr>
                      <th>Deposit ID</th>
                      <th>Amount</th>
                      <th>Payment Method</th>
                      <th>Date</th>
                      <th>Status</th>
                      <th>Reference</th>
                    </tr>
                  </thead>
                  <tbody>
                    {deposits.map((deposit) => (
                      <tr key={deposit.depositId}>
                        <td>#{deposit.depositId}</td>
                        <td>
                          <strong>${deposit.amount.toFixed(2)}</strong>
                        </td>
                        <td>
                          <Badge bg="info">{deposit.paymentMethod}</Badge>
                        </td>
                        <td>
                          {new Date(deposit.depositDate).toLocaleString()}
                        </td>
                        <td>
                          <Badge
                            bg={
                              deposit.depositStatus === "COMPLETED"
                                ? "success"
                                : "warning"
                            }
                          >
                            {deposit.depositStatus}
                          </Badge>
                        </td>
                        <td>{deposit.transactionReference}</td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              ) : (
                <Alert variant="info">
                  No deposits yet. Make your first deposit to get started!
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Funding;
