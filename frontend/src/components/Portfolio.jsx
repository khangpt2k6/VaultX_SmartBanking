import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Table,
  Modal,
  Form,
  Spinner,
  Card,
  Button,
  Alert,
  Badge,
} from "react-bootstrap";
import {
  PieChart,
  ArrowUp,
  ArrowDown,
  Plus,
  FileEarmark,
  CheckCircle,
  XCircle,
} from "react-bootstrap-icons";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";

const Portfolio = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [positions, setPositions] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPortfolioName, setNewPortfolioName] = useState("");
  const [loading, setLoading] = useState(false);
  const [portfolioStats, setPortfolioStats] = useState({});
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
      loadPortfolios();
    }
  }, [userId, token]);

  useEffect(() => {
    if (selectedPortfolio) {
      loadPortfolioPositions();
      updatePortfolioValues();
    }
  }, [selectedPortfolio]);

  const loadPortfolios = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/portfolio/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const portfolios = response.data.portfolios || [];
      setPortfolios(portfolios);
      if (portfolios.length > 0 && !selectedPortfolio) {
        setSelectedPortfolio(portfolios[0].portfolioId);
      }
    } catch (error) {
      console.error("Error loading portfolios:", error);
      toast.error("Failed to load portfolios");
    } finally {
      setLoading(false);
    }
  };

  const loadPortfolioPositions = async () => {
    if (!selectedPortfolio) return;
    try {
      const response = await axios.get(
        `${API_BASE_URL}/portfolio/${selectedPortfolio}/positions`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setPositions(response.data.positions || []);
    } catch (error) {
      console.error("Error loading positions:", error);
      toast.error("Failed to load positions");
    }
  };

  const updatePortfolioValues = async () => {
    if (!selectedPortfolio) return;
    try {
      const response = await axios.post(
        `${API_BASE_URL}/portfolio/${selectedPortfolio}/update-values`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const portfolio = response.data.portfolio;
      setPortfolioStats({
        totalValue: portfolio.totalValue,
        costBasis: portfolio.costBasis,
        unrealizedGain: (portfolio.totalValue - portfolio.costBasis).toFixed(2),
        unrealizedGainPercent: (
          ((portfolio.totalValue - portfolio.costBasis) / portfolio.costBasis) *
            100 || 0
        ).toFixed(2),
      });
    } catch (error) {
      console.error("Error updating portfolio:", error);
    }
  };

  const createPortfolio = async () => {
    if (!newPortfolioName.trim()) {
      toast.error("Please enter a portfolio name");
      return;
    }

    try {
      setLoading(true);
      await axios.post(`${API_BASE_URL}/portfolio/create`, null, {
        params: {
          userId,
          portfolioName: newPortfolioName,
        },
        headers: { Authorization: `Bearer ${token}` },
      });

      toast.success("Portfolio created successfully!");
      setNewPortfolioName("");
      setShowCreateModal(false);
      loadPortfolios();
    } catch (error) {
      toast.error(
        error.response?.data?.message || "Failed to create portfolio"
      );
    } finally {
      setLoading(false);
    }
  };

  const deletePortfolio = async (portfolioId) => {
    if (!window.confirm("Are you sure you want to delete this portfolio?"))
      return;

    try {
      await axios.delete(`${API_BASE_URL}/portfolio/${portfolioId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success("Portfolio deleted successfully");
      setSelectedPortfolio(null);
      loadPortfolios();
    } catch (error) {
      toast.error("Failed to delete portfolio");
    }
  };

  const calculateGainLoss = (position) => {
    const gain = position.currentValue - position.costBasis;
    const gainPercent =
      position.costBasis > 0 ? (gain / position.costBasis) * 100 : 0;
    return { gain: gain.toFixed(2), gainPercent: gainPercent.toFixed(2) };
  };

  return (
    <Container fluid className="py-4" style={{ maxWidth: "1400px" }}>
      {/* Header */}
      <Row className="mb-5">
        <Col md={8}>
          <div className="d-flex align-items-center gap-3 mb-2">
            <PieChart size={32} color="var(--primary-blue)" />
            <h1 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
              Portfolio Management
            </h1>
          </div>
          <p className="text-muted mb-0" style={{ paddingLeft: "2.75rem", color: "var(--text-tertiary)" }}>
            Track and manage your investment portfolios
          </p>
        </Col>
        <Col md={4} className="text-end">
          <Button
            variant="primary"
            onClick={() => setShowCreateModal(true)}
            className="d-inline-flex align-items-center gap-2"
            style={{
              borderRadius: "var(--radius-full)",
              fontWeight: 600,
              padding: "0.75rem 1.5rem",
            }}
          >
            <Plus size={18} />
            New Portfolio
          </Button>
        </Col>
      </Row>

      {/* Portfolio Selection */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                Your Portfolios
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {loading ? (
                <div className="text-center py-4">
                  <Spinner animation="border" role="status" style={{ color: "var(--primary-blue)" }}>
                    <span className="visually-hidden">Loading...</span>
                  </Spinner>
                </div>
              ) : portfolios.length > 0 ? (
                <div className="d-flex gap-2 flex-wrap">
                  {portfolios.map((portfolio) => (
                    <Button
                      key={portfolio.portfolioId}
                      variant={selectedPortfolio === portfolio.portfolioId ? "primary" : "outline-primary"}
                      onClick={() => setSelectedPortfolio(portfolio.portfolioId)}
                      className="d-inline-flex align-items-center gap-2"
                      style={{
                        borderRadius: "var(--radius-md)",
                        fontWeight: 600,
                        padding: "0.65rem 1.5rem",
                        borderColor: selectedPortfolio === portfolio.portfolioId ? "transparent" : "var(--border-medium)",
                      }}
                    >
                      <FileEarmark size={16} />
                      {portfolio.portfolioName}
                    </Button>
                  ))}
                </div>
              ) : (
                <div className="text-center py-5">
                  <PieChart size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0" style={{ color: "var(--text-tertiary)" }}>
                    No portfolios yet. Create one to get started!
                  </p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Portfolio Statistics */}
      {selectedPortfolio && Object.keys(portfolioStats).length > 0 && (
        <Row className="mb-4 g-3">
          <Col md={3}>
            <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
              <Card.Body className="p-4">
                <p className="mb-2 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                  Portfolio Value
                </p>
                <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                  ${portfolioStats.totalValue?.toFixed(2) || "0.00"}
                </h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
              <Card.Body className="p-4">
                <p className="mb-2 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                  Cost Basis
                </p>
                <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                  ${portfolioStats.costBasis?.toFixed(2) || "0.00"}
                </h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
              <Card.Body className="p-4">
                <div className="d-flex align-items-center justify-content-between mb-2">
                  <p className="mb-0 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Unrealized Gain/Loss
                  </p>
                  {parseFloat(portfolioStats.unrealizedGain) >= 0 ? (
                    <ArrowUp size={20} color="#28a745" />
                  ) : (
                    <ArrowDown size={20} color="#dc3545" />
                  )}
                </div>
                <h3
                  className="mb-0"
                  style={{
                    color: parseFloat(portfolioStats.unrealizedGain) >= 0 ? "#28a745" : "#dc3545",
                    fontWeight: 700,
                  }}
                >
                  ${portfolioStats.unrealizedGain}
                </h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
              <Card.Body className="p-4">
                <div className="d-flex align-items-center justify-content-between mb-2">
                  <p className="mb-0 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Return %
                  </p>
                  {parseFloat(portfolioStats.unrealizedGainPercent) >= 0 ? (
                    <ArrowUp size={20} color="#28a745" />
                  ) : (
                    <ArrowDown size={20} color="#dc3545" />
                  )}
                </div>
                <h3
                  className="mb-0"
                  style={{
                    color: parseFloat(portfolioStats.unrealizedGainPercent) >= 0 ? "#28a745" : "#dc3545",
                    fontWeight: 700,
                  }}
                >
                  {portfolioStats.unrealizedGainPercent}%
                </h3>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}

      {/* Positions Table */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <PieChart size={20} color="var(--primary-blue)" />
                Holdings
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {positions.length > 0 ? (
                <div className="table-responsive">
                  <Table hover responsive className="mb-0" style={{ borderCollapse: "separate", borderSpacing: 0 }}>
                    <thead>
                      <tr style={{ background: "var(--bg-secondary)" }}>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Symbol
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Type
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Quantity
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Avg Cost
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Current Price
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Position Value
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Cost Basis
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Gain/Loss
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Return %
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {positions.map((position, idx) => {
                        const { gain, gainPercent } = calculateGainLoss(position);
                        const isGain = parseFloat(gain) >= 0;
                        return (
                          <tr
                            key={position.positionId}
                            style={{
                              borderBottom: idx < positions.length - 1 ? "1px solid var(--border-light)" : "none",
                            }}
                            onMouseEnter={(e) => {
                              e.currentTarget.style.background = "var(--bg-hover)";
                            }}
                            onMouseLeave={(e) => {
                              e.currentTarget.style.background = "transparent";
                            }}
                          >
                            <td style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)" }}>
                              {position.asset?.symbol}
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
                                {position.asset?.assetType}
                              </Badge>
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>{position.quantity}</td>
                            <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>
                              ${position.averageCost?.toFixed(4)}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 600 }}>
                              ${position.asset?.currentPrice?.toFixed(2)}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 700 }}>
                              ${position.currentValue?.toFixed(2)}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>
                              ${position.costBasis?.toFixed(2)}
                            </td>
                            <td style={{ padding: "1rem" }}>
                              <span
                                className="d-inline-flex align-items-center gap-1"
                                style={{
                                  color: isGain ? "#28a745" : "#dc3545",
                                  fontWeight: 700,
                                }}
                              >
                                {isGain ? <ArrowUp size={14} /> : <ArrowDown size={14} />}
                                ${gain}
                              </span>
                            </td>
                            <td style={{ padding: "1rem" }}>
                              <span
                                className="d-inline-flex align-items-center gap-1"
                                style={{
                                  color: isGain ? "#28a745" : "#dc3545",
                                  fontWeight: 700,
                                }}
                              >
                                {isGain ? <ArrowUp size={14} /> : <ArrowDown size={14} />}
                                {gainPercent >= 0 ? "+" : ""}
                                {gainPercent}%
                              </span>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </div>
              ) : (
                <div className="text-center py-5">
                  <PieChart size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0" style={{ color: "var(--text-tertiary)" }}>
                    No positions in this portfolio yet
                  </p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Create Portfolio Modal */}
      <Modal show={showCreateModal} onHide={() => setShowCreateModal(false)} centered>
        <Modal.Header
          closeButton
          className="border-0"
          style={{ padding: "1.5rem 1.5rem 1rem" }}
        >
          <Modal.Title style={{ color: "var(--text-primary)", fontWeight: 700 }}>
            Create New Portfolio
          </Modal.Title>
        </Modal.Header>
        <Modal.Body style={{ padding: "1rem 1.5rem" }}>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                Portfolio Name
              </Form.Label>
              <Form.Control
                type="text"
                placeholder="e.g., Growth Portfolio, Income Portfolio"
                value={newPortfolioName}
                onChange={(e) => setNewPortfolioName(e.target.value)}
                style={{
                  borderRadius: "var(--radius-md)",
                  borderColor: "var(--border-medium)",
                  background: "var(--bg-input)",
                }}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer className="border-0" style={{ padding: "1rem 1.5rem 1.5rem" }}>
          <Button
            variant="outline-secondary"
            onClick={() => setShowCreateModal(false)}
            style={{
              borderRadius: "var(--radius-full)",
              fontWeight: 600,
              padding: "0.65rem 1.5rem",
              borderColor: "var(--border-medium)",
            }}
          >
            Cancel
          </Button>
          <Button
            variant="primary"
            onClick={createPortfolio}
            disabled={loading}
            className="d-inline-flex align-items-center gap-2"
            style={{
              borderRadius: "var(--radius-full)",
              fontWeight: 600,
              padding: "0.65rem 1.5rem",
            }}
          >
            {loading ? (
              <>
                <Spinner size="sm" />
                Creating...
              </>
            ) : (
              <>
                <Plus size={16} />
                Create Portfolio
              </>
            )}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default Portfolio;
