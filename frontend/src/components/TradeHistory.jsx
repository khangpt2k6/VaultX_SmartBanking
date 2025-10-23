import React, { useState, useEffect } from "react";
import { Container, Row, Col, Table, Form, Spinner } from "react-bootstrap";
import { toast } from "react-toastify";
import axios from "axios";
import { GlassCard, GlassButton } from "./ui/GlassCard";

const TradeHistory = () => {
  const [trades, setTrades] = useState([]);
  const [filteredTrades, setFilteredTrades] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterType, setFilterType] = useState("ALL");
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [dateRange, setDateRange] = useState("30days");
  const [stats, setStats] = useState({
    totalTrades: 0,
    totalBuys: 0,
    totalSells: 0,
    totalProfit: 0,
    totalCommission: 0,
  });

  const API_BASE_URL =
    import.meta.env.VITE_API_URL || "http://localhost:8080/api";
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    loadTrades();
  }, []);

  useEffect(() => {
    filterTrades();
    calculateStats();
  }, [trades, filterType, filterStatus]);

  const loadTrades = async () => {
    try {
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/trading/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setTrades(response.data.trades || []);
    } catch (error) {
      console.error("Error loading trades:", error);
      toast.error("Failed to load trade history");
    } finally {
      setLoading(false);
    }
  };

  const filterTrades = () => {
    let filtered = trades;

    // Filter by type
    if (filterType !== "ALL") {
      filtered = filtered.filter((trade) => trade.tradeType === filterType);
    }

    // Filter by status
    if (filterStatus !== "ALL") {
      filtered = filtered.filter((trade) => trade.tradeStatus === filterStatus);
    }

    // Filter by date range
    const now = new Date();
    let startDate = new Date();

    switch (dateRange) {
      case "7days":
        startDate.setDate(now.getDate() - 7);
        break;
      case "30days":
        startDate.setDate(now.getDate() - 30);
        break;
      case "90days":
        startDate.setDate(now.getDate() - 90);
        break;
      case "1year":
        startDate.setFullYear(now.getFullYear() - 1);
        break;
      default:
        return filtered;
    }

    filtered = filtered.filter(
      (trade) => new Date(trade.tradeDate) >= startDate
    );
    setFilteredTrades(filtered);
  };

  const calculateStats = () => {
    const totalTrades = trades.length;
    const buyTrades = trades.filter((t) => t.tradeType === "BUY");
    const sellTrades = trades.filter((t) => t.tradeType === "SELL");
    const totalCommission = trades.reduce(
      (sum, t) => sum + (t.commission || 0),
      0
    );

    // Calculate profit/loss (simplified)
    let totalProfit = 0;
    sellTrades.forEach((sellTrade) => {
      totalProfit += sellTrade.quantity * sellTrade.pricePerUnit;
    });
    buyTrades.forEach((buyTrade) => {
      totalProfit -= buyTrade.quantity * buyTrade.pricePerUnit;
    });

    setStats({
      totalTrades,
      totalBuys: buyTrades.length,
      totalSells: sellTrades.length,
      totalProfit: totalProfit.toFixed(2),
      totalCommission: totalCommission.toFixed(2),
    });
  };

  const exportToCSV = () => {
    const csv = [
      [
        "Trade ID",
        "Asset",
        "Type",
        "Quantity",
        "Price",
        "Total",
        "Commission",
        "Date",
        "Status",
      ],
      ...filteredTrades.map((trade) => [
        trade.tradeId,
        trade.asset?.symbol || "N/A",
        trade.tradeType,
        trade.quantity,
        trade.pricePerUnit,
        (trade.quantity * trade.pricePerUnit).toFixed(2),
        trade.commission,
        new Date(trade.tradeDate).toLocaleDateString(),
        trade.tradeStatus,
      ]),
    ];

    const csvContent = csv.map((row) => row.join(",")).join("\n");
    const blob = new Blob([csvContent], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `trades-${new Date().toISOString().split("T")[0]}.csv`;
    a.click();
  };

  return (
    <Container fluid className="py-4">
      <Row className="mb-4">
        <Col md={12}>
          <h1 className="mb-2">📋 Trade History & Ledger</h1>
          <p className="text-muted">
            View all your trading transactions and performance metrics
          </p>
        </Col>
      </Row>

      {/* Statistics Cards */}
      <Row className="mb-4">
        <Col md={2.4}>
          <Card className="bg-primary text-white text-center shadow-sm">
            <Card.Body className="py-3">
              <h6 className="mb-2">Total Trades</h6>
              <h3>{stats.totalTrades}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="bg-success text-white text-center shadow-sm">
            <Card.Body className="py-3">
              <h6 className="mb-2">Buy Orders</h6>
              <h3>{stats.totalBuys}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="bg-danger text-white text-center shadow-sm">
            <Card.Body className="py-3">
              <h6 className="mb-2">Sell Orders</h6>
              <h3>{stats.totalSells}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card
            className={`${
              stats.totalProfit >= 0 ? "bg-success" : "bg-danger"
            } text-white text-center shadow-sm`}
            style={{ color: "white" }}
          >
            <Card.Body className="py-3">
              <h6 className="mb-2">P/L</h6>
              <h3>${Math.abs(stats.totalProfit)}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="bg-warning text-white text-center shadow-sm">
            <Card.Body className="py-3">
              <h6 className="mb-2">Total Fees</h6>
              <h3>${stats.totalCommission}</h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Filters */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Body>
              <Form>
                <Row>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Trade Type</Form.Label>
                      <Form.Select
                        value={filterType}
                        onChange={(e) => setFilterType(e.target.value)}
                      >
                        <option value="ALL">All Types</option>
                        <option value="BUY">Buy Orders</option>
                        <option value="SELL">Sell Orders</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Status</Form.Label>
                      <Form.Select
                        value={filterStatus}
                        onChange={(e) => setFilterStatus(e.target.value)}
                      >
                        <option value="ALL">All Status</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="PENDING">Pending</option>
                        <option value="CANCELLED">Cancelled</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Date Range</Form.Label>
                      <Form.Select
                        value={dateRange}
                        onChange={(e) => setDateRange(e.target.value)}
                      >
                        <option value="7days">Last 7 Days</option>
                        <option value="30days">Last 30 Days</option>
                        <option value="90days">Last 90 Days</option>
                        <option value="1year">Last Year</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>&nbsp;</Form.Label>
                      <Button
                        variant="success"
                        className="w-100"
                        onClick={exportToCSV}
                      >
                        📥 Export CSV
                      </Button>
                    </Form.Group>
                  </Col>
                </Row>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Trades Table */}
      <Row>
        <Col md={12}>
          <Card className="shadow-sm">
            <Card.Header className="bg-dark text-white">
              <h5 className="mb-0">
                Transaction Ledger ({filteredTrades.length} trades)
              </h5>
            </Card.Header>
            <Card.Body>
              {loading ? (
                <Alert variant="info">Loading trade history...</Alert>
              ) : filteredTrades.length > 0 ? (
                <div className="table-responsive">
                  <Table striped hover>
                    <thead>
                      <tr>
                        <th>Trade ID</th>
                        <th>Asset</th>
                        <th>Type</th>
                        <th>Quantity</th>
                        <th>Price/Unit</th>
                        <th>Amount</th>
                        <th>Commission</th>
                        <th>Total</th>
                        <th>Date & Time</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredTrades.map((trade) => {
                        const amount = (
                          trade.quantity * trade.pricePerUnit
                        ).toFixed(2);
                        const total = (
                          parseFloat(amount) + parseFloat(trade.commission)
                        ).toFixed(2);
                        return (
                          <tr key={trade.tradeId}>
                            <td>#{trade.tradeId}</td>
                            <td>
                              <strong>{trade.asset?.symbol}</strong>
                              <br />
                              <small className="text-muted">
                                {trade.asset?.assetName}
                              </small>
                            </td>
                            <td>
                              <Badge
                                bg={
                                  trade.tradeType === "BUY"
                                    ? "success"
                                    : "danger"
                                }
                              >
                                {trade.tradeType}
                              </Badge>
                            </td>
                            <td>{trade.quantity}</td>
                            <td>${trade.pricePerUnit.toFixed(2)}</td>
                            <td>${amount}</td>
                            <td>${trade.commission.toFixed(2)}</td>
                            <td>
                              <strong>${total}</strong>
                            </td>
                            <td>
                              {new Date(trade.tradeDate).toLocaleString()}
                            </td>
                            <td>
                              <Badge
                                bg={
                                  trade.tradeStatus === "COMPLETED"
                                    ? "success"
                                    : trade.tradeStatus === "PENDING"
                                    ? "warning"
                                    : "danger"
                                }
                              >
                                {trade.tradeStatus}
                              </Badge>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </div>
              ) : (
                <Alert variant="info">
                  No trades found. Start trading to build your transaction
                  history!
                </Alert>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default TradeHistory;
