import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Table,
  Modal,
  Spinner,
} from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";
import { GlassCard } from "./ui/GlassCard";
import { GlassButton } from "./ui/GlassButton";

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

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    loadAssets();
    loadUserPortfolios();
    loadUserTrades();
    loadUserBalance();
  }, []);

  const loadAssets = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/assets/all`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAssets(response.data.assets || []);
    } catch (error) {
      console.error("Error loading assets:", error);
      toast.error("Failed to load assets");
    }
  };

  const loadUserPortfolios = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/portfolio/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setPortfolios(response.data.portfolios || []);
      if (response.data.portfolios && response.data.portfolios.length > 0) {
        setSelectedPortfolio(response.data.portfolios[0].portfolioId);
      }
    } catch (error) {
      console.error("Error loading portfolios:", error);
    }
  };

  const loadUserTrades = async () => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/trading/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setUserTrades(response.data.trades || []);
    } catch (error) {
      console.error("Error loading trades:", error);
    }
  };

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

  const executeTrade = async () => {
    if (!selectedAsset || !quantity || !pricePerUnit) {
      toast.error("Please fill all fields");
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
        headers: { Authorization: `Bearer ${token}` },
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
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col md={12}>
          <h1 className="mb-2">📈 Trading Platform</h1>
          <p className="text-muted">
            Buy and sell stocks, crypto, and commodities
          </p>
        </Col>
      </Row>

      {/* User Balance Card */}
      <Row className="mb-4">
        <Col md={4}>
          <Card className="bg-primary text-white">
            <Card.Body>
              <h6 className="mb-2">Available Balance</h6>
              <h2>${userBalance.toFixed(2)}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="bg-success text-white">
            <Card.Body>
              <h6 className="mb-2">Total Trades</h6>
              <h2>{userTrades.length}</h2>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="bg-info text-white">
            <Card.Body>
              <h6 className="mb-2">Active Portfolios</h6>
              <h2>{portfolios.length}</h2>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Trading Section */}
      <Row className="mb-4">
        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">Execute Trade</h5>
            </Card.Header>
            <Card.Body>
              <Form>
                <Form.Group className="mb-3">
                  <Form.Label>Trade Type</Form.Label>
                  <Form.Check
                    type="radio"
                    label="Buy"
                    value="BUY"
                    checked={tradeType === "BUY"}
                    onChange={(e) => setTradeType(e.target.value)}
                    className="mb-2"
                  />
                  <Form.Check
                    type="radio"
                    label="Sell"
                    value="SELL"
                    checked={tradeType === "SELL"}
                    onChange={(e) => setTradeType(e.target.value)}
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Asset</Form.Label>
                  <Form.Select
                    value={selectedAsset?.assetId || ""}
                    onChange={(e) => {
                      const asset = assets.find(
                        (a) => a.assetId === parseInt(e.target.value)
                      );
                      setSelectedAsset(asset);
                      setPricePerUnit(asset?.currentPrice || "");
                    }}
                  >
                    <option value="">Select an asset...</option>
                    {assets.map((asset) => (
                      <option key={asset.assetId} value={asset.assetId}>
                        {asset.symbol} - {asset.assetName} ($
                        {asset.currentPrice})
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Quantity</Form.Label>
                  <Form.Control
                    type="number"
                    placeholder="Enter quantity"
                    value={quantity}
                    onChange={(e) => setQuantity(e.target.value)}
                    step="0.01"
                    min="0"
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Price Per Unit</Form.Label>
                  <Form.Control
                    type="number"
                    placeholder="Enter price"
                    value={pricePerUnit}
                    onChange={(e) => setPricePerUnit(e.target.value)}
                    step="0.01"
                    min="0"
                  />
                </Form.Group>

                {selectedPortfolio && (
                  <Form.Group className="mb-3">
                    <Form.Label>Portfolio</Form.Label>
                    <Form.Select
                      value={selectedPortfolio}
                      onChange={(e) => setSelectedPortfolio(e.target.value)}
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

                <Alert variant="info" className="mb-3">
                  <strong>Total Cost/Revenue:</strong> ${calculateTotalCost()}
                </Alert>

                <Button
                  variant={tradeType === "BUY" ? "success" : "danger"}
                  className="w-100"
                  onClick={executeTrade}
                  disabled={loading || !selectedAsset}
                >
                  {loading ? "Processing..." : `Execute ${tradeType}`}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        {/* Market Info */}
        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">📊 Market Overview</h5>
            </Card.Header>
            <Card.Body>
              {selectedAsset ? (
                <div>
                  <h4>{selectedAsset.assetName}</h4>
                  <p>
                    <strong>Symbol:</strong> {selectedAsset.symbol}
                  </p>
                  <p>
                    <strong>Type:</strong>{" "}
                    <Badge bg="info">{selectedAsset.assetType}</Badge>
                  </p>
                  <p>
                    <strong>Current Price:</strong> $
                    {selectedAsset.currentPrice}
                  </p>
                  <p>
                    <strong>24h High:</strong> $
                    {selectedAsset.priceHigh || "N/A"}
                  </p>
                  <p>
                    <strong>24h Low:</strong> ${selectedAsset.priceLow || "N/A"}
                  </p>
                  <Alert variant="success">
                    Last updated:{" "}
                    {new Date(selectedAsset.lastUpdated).toLocaleString()}
                  </Alert>
                </div>
              ) : (
                <Alert variant="secondary">
                  Select an asset to view details
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Trades */}
      <Row>
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">📋 Recent Trades</h5>
            </Card.Header>
            <Card.Body>
              {userTrades.length > 0 ? (
                <Table striped hover responsive>
                  <thead>
                    <tr>
                      <th>Trade ID</th>
                      <th>Asset</th>
                      <th>Type</th>
                      <th>Quantity</th>
                      <th>Price</th>
                      <th>Commission</th>
                      <th>Total</th>
                      <th>Date</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {userTrades.slice(0, 10).map((trade) => (
                      <tr key={trade.tradeId}>
                        <td>#{trade.tradeId}</td>
                        <td>{trade.asset?.symbol}</td>
                        <td>
                          <Badge
                            bg={
                              trade.tradeType === "BUY" ? "success" : "danger"
                            }
                          >
                            {trade.tradeType}
                          </Badge>
                        </td>
                        <td>{trade.quantity}</td>
                        <td>${trade.pricePerUnit}</td>
                        <td>${trade.commission}</td>
                        <td>
                          $
                          {(
                            trade.quantity * trade.pricePerUnit +
                            trade.commission
                          ).toFixed(2)}
                        </td>
                        <td>
                          {new Date(trade.tradeDate).toLocaleDateString()}
                        </td>
                        <td>
                          <Badge
                            bg={
                              trade.tradeStatus === "COMPLETED"
                                ? "success"
                                : "warning"
                            }
                          >
                            {trade.tradeStatus}
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              ) : (
                <Alert variant="info">
                  No trades yet. Start trading to see your history!
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Trading;
