import React, { useState, useEffect } from "react";
import { Container, Row, Col, Table, Form, Spinner, Card, Button, Alert, Badge } from "react-bootstrap";
import {
  FileText,
  ArrowUpCircle,
  ArrowDownCircle,
  CheckCircle,
  Clock,
  XCircle,
  Download,
  BarChart,
  ArrowUp,
  ArrowDown,
} from "react-bootstrap-icons";
import { toast } from "react-toastify";
import axios from "axios";
import { API_BASE_URL } from "../config/api";

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
      loadTrades();
    }
  }, [userId]);

  useEffect(() => {
    filterTrades();
    calculateStats();
  }, [trades, filterType, filterStatus, dateRange]);

  const loadTrades = async () => {
    try {
      const authToken = localStorage.getItem("token");
      if (!authToken || !userId) {
        console.warn("Token or userId not available");
        return;
      }
      setLoading(true);
      const response = await axios.get(
        `${API_BASE_URL}/trading/user/${userId}`,
        {
          headers: { Authorization: `Bearer ${authToken}` },
        }
      );
      setTrades(response.data.trades || []);
    } catch (error) {
      console.error("Error loading trades:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Unauthorized. Please log in again.");
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
      } else {
        toast.error("Failed to load trade history");
      }
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
    <Container fluid className="py-4" style={{ maxWidth: "1400px" }}>
      {/* Header */}
      <Row className="mb-5">
        <Col>
          <div className="d-flex align-items-center gap-3 mb-2">
            <FileText size={32} color="var(--primary-blue)" />
            <h1 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
              Trade History & Ledger
            </h1>
          </div>
          <p className="text-muted mb-0" style={{ paddingLeft: "2.75rem", color: "var(--text-tertiary)" }}>
            View all your trading transactions and performance metrics
          </p>
        </Col>
      </Row>

      {/* Statistics Cards */}
      <Row className="mb-4 g-3">
        <Col md={2.4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4 text-center">
              <BarChart size={32} color="var(--primary-blue)" className="mb-2" style={{ opacity: 0.15 }} />
              <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                Total Trades
              </p>
              <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                {stats.totalTrades}
              </h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4 text-center">
              <ArrowUpCircle size={32} color="#28a745" className="mb-2" style={{ opacity: 0.15 }} />
              <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                Buy Orders
              </p>
              <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                {stats.totalBuys}
              </h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4 text-center">
              <ArrowDownCircle size={32} color="#dc3545" className="mb-2" style={{ opacity: 0.15 }} />
              <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                Sell Orders
              </p>
              <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                {stats.totalSells}
              </h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4 text-center">
              {parseFloat(stats.totalProfit) >= 0 ? (
                <ArrowUp size={32} color="#28a745" className="mb-2" style={{ opacity: 0.15 }} />
              ) : (
                <ArrowDown size={32} color="#dc3545" className="mb-2" style={{ opacity: 0.15 }} />
              )}
              <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                P/L
              </p>
              <h3
                className="mb-0"
                style={{
                  color: parseFloat(stats.totalProfit) >= 0 ? "#28a745" : "#dc3545",
                  fontWeight: 700,
                }}
              >
                ${Math.abs(stats.totalProfit)}
              </h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={2.4}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4 text-center">
              <FileText size={32} color="var(--primary-blue)" className="mb-2" style={{ opacity: 0.15 }} />
              <p className="mb-1 text-muted" style={{ fontSize: "0.85rem", fontWeight: 600, color: "var(--text-tertiary)" }}>
                Total Fees
              </p>
              <h3 className="mb-0" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                ${stats.totalCommission}
              </h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Filters */}
      <Row className="mb-4">
        <Col md={12}>
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Body className="p-4">
              <Form>
                <Row className="g-3">
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        Trade Type
                      </Form.Label>
                      <Form.Select
                        value={filterType}
                        onChange={(e) => setFilterType(e.target.value)}
                        style={{
                          borderRadius: "var(--radius-md)",
                          borderColor: "var(--border-medium)",
                          background: "var(--bg-input)",
                        }}
                      >
                        <option value="ALL">All Types</option>
                        <option value="BUY">Buy Orders</option>
                        <option value="SELL">Sell Orders</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        Status
                      </Form.Label>
                      <Form.Select
                        value={filterStatus}
                        onChange={(e) => setFilterStatus(e.target.value)}
                        style={{
                          borderRadius: "var(--radius-md)",
                          borderColor: "var(--border-medium)",
                          background: "var(--bg-input)",
                        }}
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
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        Date Range
                      </Form.Label>
                      <Form.Select
                        value={dateRange}
                        onChange={(e) => setDateRange(e.target.value)}
                        style={{
                          borderRadius: "var(--radius-md)",
                          borderColor: "var(--border-medium)",
                          background: "var(--bg-input)",
                        }}
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
                      <Form.Label style={{ fontWeight: 600, color: "var(--text-primary)", marginBottom: "0.75rem" }}>
                        &nbsp;
                      </Form.Label>
                      <Button
                        variant="outline-primary"
                        className="w-100 d-inline-flex align-items-center justify-content-center gap-2"
                        onClick={exportToCSV}
                        style={{
                          borderRadius: "var(--radius-full)",
                          fontWeight: 600,
                          padding: "0.65rem 1.5rem",
                          borderColor: "var(--border-medium)",
                        }}
                      >
                        <Download size={16} />
                        Export CSV
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
          <Card className="border-0 shadow-sm" style={{ background: "var(--bg-card)", borderRadius: "var(--radius-lg)" }}>
            <Card.Header className="border-0 pb-0 pt-4 px-4" style={{ background: "transparent" }}>
              <h5 className="mb-0 d-flex align-items-center gap-2" style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                <FileText size={20} color="var(--primary-blue)" />
                Transaction Ledger ({filteredTrades.length} trades)
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {loading ? (
                <div className="text-center py-5">
                  <Spinner animation="border" role="status" style={{ color: "var(--primary-blue)" }}>
                    <span className="visually-hidden">Loading...</span>
                  </Spinner>
                  <p className="mt-3 mb-0" style={{ color: "var(--text-tertiary)" }}>
                    Loading trade history...
                  </p>
                </div>
              ) : filteredTrades.length > 0 ? (
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
                          Price/Unit
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Amount
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Commission
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Total
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Date & Time
                        </th>
                        <th style={{ padding: "1rem", fontWeight: 700, color: "var(--text-primary)", fontSize: "0.85rem", textTransform: "uppercase", letterSpacing: "0.05em", borderBottom: "2px solid var(--border-light)" }}>
                          Status
                        </th>
                      </tr>
                    </thead>
                    <tbody>
                      {filteredTrades.map((trade, idx) => {
                        const amount = (
                          trade.quantity * trade.pricePerUnit
                        ).toFixed(2);
                        const total = (
                          parseFloat(amount) + parseFloat(trade.commission)
                        ).toFixed(2);
                        return (
                          <tr
                            key={trade.tradeId}
                            style={{
                              borderBottom: idx < filteredTrades.length - 1 ? "1px solid var(--border-light)" : "none",
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
                            <td style={{ padding: "1rem" }}>
                              <strong style={{ color: "var(--text-primary)", fontWeight: 700 }}>
                                {trade.asset?.symbol}
                              </strong>
                              <br />
                              <small style={{ color: "var(--text-tertiary)", fontSize: "0.8rem" }}>
                                {trade.asset?.assetName}
                              </small>
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
                                {trade.tradeType === "BUY" ? (
                                  <ArrowUpCircle size={12} className="me-1" />
                                ) : (
                                  <ArrowDownCircle size={12} className="me-1" />
                                )}
                                {trade.tradeType}
                              </Badge>
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>{trade.quantity}</td>
                            <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 600 }}>
                              ${trade.pricePerUnit.toFixed(2)}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 600 }}>
                              ${amount}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-secondary)" }}>
                              ${trade.commission.toFixed(2)}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-primary)", fontWeight: 700 }}>
                              ${total}
                            </td>
                            <td style={{ padding: "1rem", color: "var(--text-tertiary)", fontSize: "0.9rem" }}>
                              {new Date(trade.tradeDate).toLocaleString()}
                            </td>
                            <td style={{ padding: "1rem" }}>
                              <Badge
                                style={{
                                  background:
                                    trade.tradeStatus === "COMPLETED"
                                      ? "rgba(40, 167, 69, 0.15)"
                                      : trade.tradeStatus === "PENDING"
                                      ? "rgba(255, 193, 7, 0.15)"
                                      : "rgba(220, 53, 69, 0.15)",
                                  color:
                                    trade.tradeStatus === "COMPLETED"
                                      ? "#28a745"
                                      : trade.tradeStatus === "PENDING"
                                      ? "#ffc107"
                                      : "#dc3545",
                                  padding: "0.3rem 0.85rem",
                                  borderRadius: "var(--radius-full)",
                                  fontWeight: 600,
                                  fontSize: "0.75rem",
                                }}
                              >
                                {trade.tradeStatus === "COMPLETED" ? (
                                  <CheckCircle size={12} className="me-1" />
                                ) : trade.tradeStatus === "PENDING" ? (
                                  <Clock size={12} className="me-1" />
                                ) : (
                                  <XCircle size={12} className="me-1" />
                                )}
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
                <div className="text-center py-5">
                  <FileText size={48} className="mb-3" style={{ opacity: 0.2, color: "var(--primary-blue)" }} />
                  <p className="mb-0" style={{ color: "var(--text-tertiary)" }}>
                    No trades found. Start trading to build your transaction history!
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

export default TradeHistory;
