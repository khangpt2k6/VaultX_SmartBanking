import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Table,
  Modal,
  Form,
  Spinner,
} from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { GlassCard } from "./ui/GlassCard";
import { GlassButton } from "./ui/GlassButton";

const Portfolio = () => {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [positions, setPositions] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPortfolioName, setNewPortfolioName] = useState("");
  const [loading, setLoading] = useState(false);
  const [portfolioStats, setPortfolioStats] = useState({});

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    loadPortfolios();
  }, []);

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
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col md={8}>
          <h1 className="mb-2">💼 Portfolio Management</h1>
          <p className="text-muted">
            Track and manage your investment portfolios
          </p>
        </Col>
        <Col md={4} className="text-end">
          <Button
            variant="success"
            onClick={() => setShowCreateModal(true)}
            className="me-2"
          >
            + New Portfolio
          </Button>
        </Col>
      </Row>

      {/* Portfolio Selection */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">Your Portfolios</h5>
            </Card.Header>
            <Card.Body>
              {loading ? (
                <Spinner animation="border" role="status">
                  <span className="visually-hidden">Loading...</span>
                </Spinner>
              ) : portfolios.length > 0 ? (
                <div className="d-flex gap-2 flex-wrap">
                  {portfolios.map((portfolio) => (
                    <Button
                      key={portfolio.portfolioId}
                      variant={
                        selectedPortfolio === portfolio.portfolioId
                          ? "primary"
                          : "outline-primary"
                      }
                      onClick={() =>
                        setSelectedPortfolio(portfolio.portfolioId)
                      }
                      size="lg"
                    >
                      {portfolio.portfolioName}
                    </Button>
                  ))}
                </div>
              ) : (
                <Alert variant="info">
                  No portfolios yet. Create one to get started!
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Portfolio Statistics */}
      {selectedPortfolio && Object.keys(portfolioStats).length > 0 && (
        <Row className="mb-4">
          <Col md={3}>
            <Card className="bg-primary text-white">
              <Card.Body>
                <h6 className="mb-2">Portfolio Value</h6>
                <h3>${portfolioStats.totalValue?.toFixed(2) || "0.00"}</h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card className="bg-info text-white">
              <Card.Body>
                <h6 className="mb-2">Cost Basis</h6>
                <h3>${portfolioStats.costBasis?.toFixed(2) || "0.00"}</h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card
              className={
                portfolioStats.unrealizedGain >= 0 ? "bg-success" : "bg-danger"
              }
              style={{ color: "white" }}
            >
              <Card.Body>
                <h6 className="mb-2">Unrealized Gain/Loss</h6>
                <h3>${portfolioStats.unrealizedGain}</h3>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3}>
            <Card
              className={
                portfolioStats.unrealizedGainPercent >= 0
                  ? "bg-success"
                  : "bg-danger"
              }
              style={{ color: "white" }}
            >
              <Card.Body>
                <h6 className="mb-2">Return %</h6>
                <h3>{portfolioStats.unrealizedGainPercent}%</h3>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}

      {/* Positions Table */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">📊 Holdings</h5>
            </Card.Header>
            <Card.Body>
              {positions.length > 0 ? (
                <Table striped hover responsive>
                  <thead>
                    <tr>
                      <th>Symbol</th>
                      <th>Asset Type</th>
                      <th>Quantity</th>
                      <th>Avg Cost</th>
                      <th>Current Price</th>
                      <th>Position Value</th>
                      <th>Cost Basis</th>
                      <th>Gain/Loss</th>
                      <th>Return %</th>
                    </tr>
                  </thead>
                  <tbody>
                    {positions.map((position) => {
                      const { gain, gainPercent } = calculateGainLoss(position);
                      return (
                        <tr key={position.positionId}>
                          <td>
                            <strong>{position.asset?.symbol}</strong>
                          </td>
                          <td>
                            <Badge bg="info">{position.asset?.assetType}</Badge>
                          </td>
                          <td>{position.quantity}</td>
                          <td>${position.averageCost?.toFixed(4)}</td>
                          <td>${position.asset?.currentPrice?.toFixed(2)}</td>
                          <td>${position.currentValue?.toFixed(2)}</td>
                          <td>${position.costBasis?.toFixed(2)}</td>
                          <td>
                            <span
                              className={
                                gain >= 0 ? "text-success" : "text-danger"
                              }
                            >
                              {gain >= 0 ? "+" : ""}
                              {gain}
                            </span>
                          </td>
                          <td>
                            <span
                              className={
                                gainPercent >= 0
                                  ? "text-success"
                                  : "text-danger"
                              }
                            >
                              {gainPercent >= 0 ? "+" : ""}
                              {gainPercent}%
                            </span>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </Table>
              ) : (
                <Alert variant="secondary">
                  No positions in this portfolio yet
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Create Portfolio Modal */}
      <Modal show={showCreateModal} onHide={() => setShowCreateModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Create New Portfolio</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Portfolio Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="e.g., Growth Portfolio, Income Portfolio"
                value={newPortfolioName}
                onChange={(e) => setNewPortfolioName(e.target.value)}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowCreateModal(false)}>
            Cancel
          </Button>
          <Button
            variant="success"
            onClick={createPortfolio}
            disabled={loading}
          >
            {loading ? "Creating..." : "Create Portfolio"}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default Portfolio;
