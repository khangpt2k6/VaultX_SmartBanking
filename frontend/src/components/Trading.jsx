import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Table,
  Modal,
  Spinner,
  Card,
  Button,
  Alert,
  Badge,
} from "react-bootstrap";
import {
  GraphUpArrow,
  Wallet2,
  ArrowUpCircle,
  ArrowDownCircle,
  BarChart,
  CheckCircle,
  Clock,
  XCircle,
} from "react-bootstrap-icons";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";

const Trading = () => {
  const [assets, setAssets] = useState([]);
  const [selectedAsset, setSelectedAsset] = useState(null);
  const [quantity, setQuantity] = useState("");
  const [pricePerUnit, setPricePerUnit] = useState("");
  const [tradeType, setTradeType] = useState("BUY");
  const [userTrades, setUserTrades] = useState([]);
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [loading, setLoading] = useState(false);
  const [userBalance, setUserBalance] = useState(0);
  const [showTradeModal, setShowTradeModal] = useState(false);
  const [userId, setUserId] = useState(null);

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
    const authToken = localStorage.getItem("token");
    if (userId && authToken) {
      loadAssets();
      loadUserPortfolios();
      loadUserTrades();
      loadUserBalance();
    }
  }, [userId]);

  const loadAssets = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (!authToken) {
        toast.error("Please log in again");
        return;
      }
      const response = await axios.get(`${API_BASE_URL}/assets/all`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });
      setAssets(response.data.assets || []);
    } catch (error) {
      console.error("Error loading assets:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      } else {
        toast.error("Failed to load assets");
      }
    }
  };

  const loadUserPortfolios = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (!authToken || !userId) {
        return;
      }
      const response = await axios.get(
        `${API_BASE_URL}/portfolio/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${authToken}` },
        }
      );
      setPortfolios(response.data.portfolios || []);
      if (response.data.portfolios && response.data.portfolios.length > 0) {
        setSelectedPortfolio(response.data.portfolios[0].portfolioId);
      }
    } catch (error) {
      console.error("Error loading portfolios:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      }
    }
  };

  const loadUserTrades = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (!authToken || !userId) {
        return;
      }
      const response = await axios.get(
        `${API_BASE_URL}/trading/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${authToken}` },
        }
      );
      setUserTrades(response.data.trades || []);
    } catch (error) {
      console.error("Error loading trades:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      }
    }
  };

  const loadUserBalance = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (!authToken || !userId) {
        return;
      }
      const response = await axios.get(
        `${API_BASE_URL}/accounts/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${authToken}` },
        }
      );
      if (response.data.accounts && response.data.accounts.length > 0) {
        setUserBalance(response.data.accounts[0].balance);
      }
    } catch (error) {
      console.error("Error loading balance:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      }
    }
  };

  const executeTrade = async () => {
    if (!selectedAsset || !quantity || !pricePerUnit) {
      toast.error("Please fill all fields");
      return;
    }

    const authToken = localStorage.getItem("token");
    if (!authToken || !userId) {
      toast.error("Please log in again");
      return;
    }

    setLoading(true);
    try {
      const endpoint = tradeType === "BUY" ? "/trading/buy" : "/trading/sell";
      const response = await axios.post(`${API_BASE_URL}${endpoint}`, null, {
        params: {
          userId,
          assetId: selectedAsset.assetId,
          quantity: parseFloat(quantity),
          pricePerUnit: parseFloat(pricePerUnit),
          portfolioId: selectedPortfolio,
        },
        headers: { Authorization: `Bearer ${authToken}` },
      });

      toast.success(
        `${tradeType} trade executed successfully! Order ID: ${response.data.trade.tradeId}`
      );

      // Reset form
      setQuantity("");
      setPricePerUnit("");
      setSelectedAsset(null);
      setShowTradeModal(false);

      // Reload data
      loadUserTrades();
      loadUserBalance();
      loadUserPortfolios();
    } catch (error) {
      toast.error(error.response?.data?.message || "Failed to execute trade");
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalCost = () => {
    if (quantity && pricePerUnit) {
      return (parseFloat(quantity) * parseFloat(pricePerUnit)).toFixed(2);
    }
    return "0.00";
  };

  return (
    <Container fluid className="py-4" style={{ maxWidth: "1400px" }}>
      {/* Header */}
      <Row className="mb-5">
        <Col>
          <div className="d-flex align-items-center gap-3 mb-2">
            <GraphUpArrow size={32} color="var(--primary-blue)" />
            <h1 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
              Trading Platform
            </h1>
          </div>
          <p className="text-muted mb-0" style={{ paddingLeft: "2.75rem", color: "var(--text-tertiary)" }}>
            Execute trades across stocks, crypto, and commodities
          </p>
        </Col>
      </Row>

      {/* Stats Cards */}
      <Row className="mb-4 g-3">
        <Col md={4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div>
                  <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Available Balance
                  </p>
                  <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    ${userBalance.toFixed(2)}
                  </h3>
                </div>
                <Wallet2 size={40} color="var(--primary-blue)" style={{ opacity: 0.15 }} />
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div>
                  <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Total Trades
                  </p>
                  <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    {userTrades.length}
                  </h3>
                </div>
                <BarChart size={40} color="var(--primary-blue)" style={{ opacity: 0.15 }} />
              </div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div>
                  <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                    Active Portfolios
                  </p>
                  <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    {portfolios.length}
                  </h3>
                </div>
                <GraphUpArrow size={40} color="var(--primary-blue)" style={{ opacity: 0.15 }} />
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Trading Section */}
      <Row className="mb-4 g-4">
        <Col lg={7}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                Execute Trade
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              <Form>
                <Form.Group className="mb-4">
                  <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                    Trade Type
                  </Form.Label>
                  <div className="d-flex gap-3">
                    <Button
                      variant={tradeType === "BUY" ? "primary" : "outline-primary"}
                      onClick={(e) => {
                        e.preventDefault();
                        setTradeType("BUY");
                      }}
                      className="flex-fill d-flex align-items-center justify-content-center gap-2"
                      style={{
                        borderRadius: "var(--radius-md)",
                        borderColor: tradeType === "BUY" ? "transparent" : "var(--border-medium)",
                        fontWeight: 600,
                        padding: "0.75rem 1.5rem",
                      }}
                    >
                      <ArrowUpCircle size={18} />
                      Buy
                    </Button>
                    <Button
                      variant={tradeType === "SELL" ? "danger" : "outline-danger"}
                      onClick={(e) => {
                        e.preventDefault();
                        setTradeType("SELL");
                      }}
                      className="flex-fill d-flex align-items-center justify-content-center gap-2"
                      style={{
                        borderRadius: "var(--radius-md)",
                        borderColor: tradeType === "SELL" ? "transparent" : "var(--border-medium)",
                        fontWeight: 600,
                        padding: "0.75rem 1.5rem",
                      }}
                    >
                      <ArrowDownCircle size={18} />
                      Sell
                    </Button>
                  </div>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                    Asset
                  </Form.Label>
                  <Form.Select
                    value={selectedAsset?.assetId || ""}
                    onChange={(e) => {
                      const asset = assets.find(
                        (a) => a.assetId === parseInt(e.target.value)
                      );
                      setSelectedAsset(asset);
                      setPricePerUnit(asset?.currentPrice || "");
                    }}
                    style={{
                      borderRadius: "var(--radius-md)",
                      borderColor: "var(--border-medium)",
                      background: "var(--bg-input)",
                    }}
                  >
                    <option value="">Select an asset...</option>
                    {assets.map((asset) => (
                      <option key={asset.assetId} value={asset.assetId}>
                        {asset.symbol} - {asset.assetName} (${asset.currentPrice})
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>

                <Row className="g-3">
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        Quantity
                      </Form.Label>
                      <Form.Control
                        type="number"
                        placeholder="Enter quantity"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        step="0.01"
                        min="0"
                        style={{
                          borderRadius: "var(--radius-md)",
                          borderColor: "var(--border-medium)",
                          background: "var(--bg-input)",
                        }}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        Price Per Unit
                      </Form.Label>
                      <Form.Control
                        type="number"
                        placeholder="Enter price"
                        value={pricePerUnit}
                        onChange={(e) => setPricePerUnit(e.target.value)}
                        step="0.01"
                        min="0"
                        style={{
                          borderRadius: "var(--radius-md)",
                          borderColor: "var(--border-medium)",
                          background: "var(--bg-input)",
                        }}
                      />
                    </Form.Group>
                  </Col>
                </Row>

                {selectedPortfolio && (
                  <Form.Group className="mb-4">
                    <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                      Portfolio
                    </Form.Label>
                    <Form.Select
                      value={selectedPortfolio}
                      onChange={(e) => setSelectedPortfolio(e.target.value)}
                      style={{
                        borderRadius: "var(--radius-md)",
                        borderColor: "var(--border-medium)",
                        background: "var(--bg-input)",
                      }}
                    >
                      {portfolios.map((portfolio) => (
                        <option
                          key={portfolio.portfolioId}
                          value={portfolio.portfolioId}
                        >
                          {portfolio.portfolioName}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                )}

                <div
                  className="mb-4 p-3"
                  style={{
                    background: "var(--bg-secondary)",
                    borderRadius: "var(--radius-md)",
                    border: "1px solid var(--border-light)",
                  }}
                >
                  <div className="d-flex align-items-center justify-content-between">
                    <span style={{ fontWeight: 600, color: "var(--text-secondary)" }}>
                      Total {tradeType === "BUY" ? "Cost" : "Revenue"}
                    </span>
                    <span style={{ fontSize: "1.25rem", fontWeight: 700, color: "var(--primary-blue)" }}>
                      ${calculateTotalCost()}
                    </span>
                  </div>
                </div>

                <Button
                  variant={tradeType === "BUY" ? "primary" : "danger"}
                  className="w-100"
                  onClick={executeTrade}
                  disabled={loading || !selectedAsset}
                  style={{
                    borderRadius: "var(--radius-full)",
                    fontWeight: 600,
                    padding: "0.85rem",
                    border: "none",
                  }}
                >
                  {loading ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      Processing...
                    </>
                  ) : (
                    <>
                      {tradeType === "BUY" ? <ArrowUpCircle className="me-2" size={18} /> : <ArrowDownCircle className="me-2" size={18} />}
                      Execute {tradeType}
                    </>
                  )}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        {/* Market Info */}
        <Col lg={5}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <BarChart size={20} color="var(--primary-blue)" />
                Market Overview
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {selectedAsset ? (
                <div>
                  <h4 className="mb-4" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                    {selectedAsset.assetName}
                  </h4>
                  <div className="mb-3 pb-3 border-bottom" style={{ borderColor: "var(--border-light) !important" }}>
                    <div className="d-flex align-items-center justify-content-between mb-2">
                      <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>Symbol</span>
                      <strong style={{ color: "var(--text-primary)" }}>{selectedAsset.symbol}</strong>
                    </div>
                    <div className="d-flex align-items-center justify-content-between">
                      <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>Type</span>
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
                        {selectedAsset.assetType}
                      </Badge>
                    </div>
                  </div>
                  <div className="mb-3">
                    <div className="d-flex align-items-center justify-content-between mb-2">
                      <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>Current Price</span>
                      <strong style={{ color: "var(--primary-blue)", fontSize: "1.1rem" }}>
                        ${selectedAsset.currentPrice}
                      </strong>
                    </div>
                    <div className="d-flex align-items-center justify-content-between mb-2">
                      <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>24h High</span>
                      <span style={{ color: "var(--text-primary)", fontWeight: 600 }}>
                        ${selectedAsset.priceHigh || "N/A"}
                      </span>
                    </div>
                    <div className="d-flex align-items-center justify-content-between">
                      <span style={{ color: "var(--text-secondary)", fontSize: "0.9rem" }}>24h Low</span>
                      <span style={{ color: "var(--text-primary)", fontWeight: 600 }}>
                        ${selectedAsset.priceLow || "N/A"}
                      </span>
                    </div>
                  </div>
                  <div
                    className="mt-3 p-2"
                    style={{
                      background: "var(--bg-secondary)",
                      borderRadius: "var(--radius-md)",
                      fontSize: "0.85rem",
                      color: "var(--text-tertiary)",
                    }}
                  >
                    Last updated: {new Date(selectedAsset.lastUpdated).toLocaleString()}
                  </div>
                </div>
              ) : (
                <div
                  className="text-center py-4"
                  style={{
                    color: "var(--text-tertiary)",
                    fontSize: "0.95rem",
                  }}
                >
                  <BarChart size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0">Select an asset to view details</p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Trades */}
      <Row>
        <Col md={12}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <BarChart size={20} color="var(--primary-blue)" />
                Recent Trades
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {userTrades.length > 0 ? (
                <div className="table-responsive">
                  <Table hover responsive className="mb-0" style={{ borderCollapse: "separate", borderSpacing: 0 }}>
                    <thead>
                      <tr style={{ background: "var(--bg-secondary)" }}>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Trade ID
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Asset
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Type
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Quantity
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Price
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Commission
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Total
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Date
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Status
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {userTrades.slice(0, 10).map((trade, idx) => (
                        <tr
                          key={trade.tradeId}
                          style={{
                            borderBottom: idx < userTrades.slice(0, 10).length - 1 ? "1px solid var(--border-light)" : "none",
                          }}
                          onMouseEnter={(e) => {
                            e.currentTarget.style.background = "var(--bg-hover)";
                          }}
                          onMouseLeave={(e) => {
                            e.currentTarget.style.background = "transparent";
                          }}
                        >
                          <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>
                            <span style={{ fontWeight: 600, color: "var(--text-primary)" }}>#{trade.tradeId}</span>
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 600 }}>
                            {trade.asset?.symbol}
                          </td>
                          <td style={{ padding: "1rem" }}>
                            <Badge
                              style={{
                                background: trade.tradeType === "BUY" ? "rgba(40, 167, 69, 0.15)" : "rgba(220, 53, 69, 0.15)",
                                color: trade.tradeType === "BUY" ? "#28a745" : "#dc3545",
                                padding: "0.3rem 0.85rem",
                                borderRadius: "var(--radius-full)",
                                fontWeight: 600,
                                fontSize: "0.75rem",
                              }}
                            >
                              {trade.tradeType === "BUY" ? <ArrowUpCircle size={12} className="me-1" /> : <ArrowDownCircle size={12} className="me-1" />}
                              {trade.tradeType}
                            </Badge>
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>{trade.quantity}</td>
                          <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 600 }}>
                            ${trade.pricePerUnit}
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>${trade.commission}</td>
                          <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 700 }}>
                            $
                            {(
                              trade.quantity * trade.pricePerUnit +
                              trade.commission
                            ).toFixed(2)}
                          </td>
                          <td style={{ padding: "1rem", color: "var(--text-tertiary)", fontSize: "0.9rem" }}>
                            {new Date(trade.tradeDate).toLocaleDateString()}
                          </td>
                          <td style={{ padding: "1rem" }}>
                            <Badge
                              style={{
                                background: trade.tradeStatus === "COMPLETED" ? "rgba(40, 167, 69, 0.15)" : "rgba(255, 193, 7, 0.15)",
                                color: trade.tradeStatus === "COMPLETED" ? "#28a745" : "#ffc107",
                                padding: "0.3rem 0.85rem",
                                borderRadius: "var(--radius-full)",
                                fontWeight: 600,
                                fontSize: "0.75rem",
                              }}
                            >
                              {trade.tradeStatus === "COMPLETED" ? (
                                <CheckCircle size={12} className="me-1" />
                              ) : (
                                <Clock size={12} className="me-1" />
                              )}
                              {trade.tradeStatus}
                            </Badge>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              ) : (
                <div
                  className="text-center py-5"
                  style={{
                    color: "var(--text-tertiary)",
                  }}
                >
                  <BarChart size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0">No trades yet. Start trading to see your history!</p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Trading;
