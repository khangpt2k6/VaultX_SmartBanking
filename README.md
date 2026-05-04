# TradeFlow — Trading Terminal Simulator

A **Spring Boot + React** paper-trading platform modeled after a real trading terminal. Streams live synthetic ticks across **28 symbols** in parallel — mega-cap tech, finance, energy, consumer, crypto, and commodities — routes orders through an in-memory matching engine with retry and anti-abuse controls, and renders a dense Bloomberg-style workspace.

<img width="1749" height="996" alt="image" src="https://github.com/user-attachments/assets/5dcb7859-4d56-4406-95ad-e9dd4d7d8f62" />

---

## Tech Stack

### Backend
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP_over_WebSocket-000000?style=for-the-badge&logo=socket.io&logoColor=white)
![H2](https://img.shields.io/badge/H2-in--memory-4479A1?style=for-the-badge&logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![JMH](https://img.shields.io/badge/JMH-benchmarks-F80000?style=for-the-badge&logo=openjdk&logoColor=white)

### Frontend
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-7-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Tailwind](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![lightweight-charts](https://img.shields.io/badge/lightweight--charts-TradingView-2962FF?style=for-the-badge&logo=tradingview&logoColor=white)

### Load Testing
![k6](https://img.shields.io/badge/k6-load_testing-7D64FF?style=for-the-badge&logo=k6&logoColor=white)

---

## What's Inside

### Matching Engine
- **In-memory order book** — market and limit orders, resting working-orders book, 8-level synthetic depth ladder
- **Retry on transient faults** — `executeWithRetry()` retries up to 3× with exponential backoff (40 ms × attempt)
- **Rate limiting + fraud alerts** — notional cap enforcement, rate-limit triggers, alert history (last 100 entries)
- **Event sourcing foundation** — `EventLog` interface with `snapshot()` / `append()`, `replayFrom()` for state rebuild

### Tick Engine
- **Parallel computation** — `MarketDataService` fans tick calculation across cores via `parallelStream` + `ThreadLocalRandom`
- **Dedicated thread pools** — `AsyncConfig` wires separate `ThreadPoolTaskExecutor` beans for tick computation and order matching; `ThreadPoolTaskScheduler` prevents `@Scheduled` jobs from serializing
- **Live metrics** — publishes `ticksPerSecond`, `advancers`, `decliners`, `symbolCount` on every batch

### Real-Time Feed
- **STOMP over SockJS** — WebSocket endpoint at `/ws-market`, topic `/topic/market`, SockJS fallback for restricted networks
- **2 Hz broadcast** — market updates pushed to all connected clients on each scheduler tick
- **Connection state tracking** — client cycles through `connecting → live / offline / error` with auto-reconnect (1 s delay)

### UI — Bloomberg-Style Workspace

| Panel | Implementation |
|---|---|
| Candlestick chart | **TradingView lightweight-charts** — OHLCV candles, EMA(9/20), VWAP overlay, volume histogram; intervals: 1 s / 5 s / 15 s / 1 m / 5 m |
| Order book | Bid/ask depth ladder with CSS-based cumulative-size bars (emerald / red), 8 levels each side |
| Watchlist | 28 symbols, live price + dollar/percent change, 550 ms direction-flash animation on tick |
| Market breadth strip | Heatmap across all 28 symbols — color intensity scales with % move magnitude |
| Scanner widget | Top gainers, top losers, most active — derived from latest tick batch |
| Bottom rail tabs | Positions · Working Orders · Orders · Time & Sales tape · Fraud Alerts |
| Layout | **react-grid-layout** — all panels are draggable and resizable |

---

## Architecture

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/5d92a908-8f10-4b66-b4d1-c57ca8b5294d" />

---

## Run Locally

**Backend** (requires Java 17):
```bash
cd backend
mvn spring-boot:run
# API available at http://localhost:8080
```

**Frontend** (requires Node 18+):
```bash
cd frontend
npm install
npm run dev
# Open the URL shown, e.g. http://localhost:5173
```

No database setup or login required — cash, positions, and trades are held in-memory for the session.

---

## API Reference

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/trading/assets` | 28-symbol universe with live prices |
| `GET` | `/api/trading/assets/{symbol}/history` | Rolling price history for chart |
| `GET` | `/api/trading/candles/{symbol}?interval=` | Server-aggregated OHLCV candles + EMA/VWAP |
| `GET` | `/api/trading/order-book/{symbol}` | Synthetic 8-level depth |
| `POST` | `/api/trading/orders` | Place market or limit order |
| `GET` | `/api/trading/working-orders` | Resting limit book |
| `GET` | `/api/trading/portfolio` | Cash, equity, positions, session P&L |
| `GET` | `/api/trading/tape` | Time & sales |
| `GET` | `/api/trading/metrics` | Engine counters + tick stats (TPS, total ticks) |
| `GET` | `/api/trading/fraud-alerts` | Rate-limit / risk alerts |
| `WS` | `/ws-market → /topic/market` | Live tick stream |

---

## Load Testing (k6)

Install [k6](https://k6.io/docs/get-started/installation/), start the backend, then:

```bash
# Sanity check — 2 VUs for 15 s
k6 run scripts/k6/trading-smoke.js --env BASE_URL=http://localhost:8080

# Sustained load — ramps to 15 VUs over 85 s, mixed GET + POST
k6 run scripts/k6/trading-load.js  --env BASE_URL=http://localhost:8080
```

HTTP 400 on random sells is expected when there is no inventory. See `scripts/k6/README.md` for full details.

---

## Benchmarks (JMH)

Matching-engine microbenchmarks live under `backend/src/jmh/` and run via the Maven JMH profile:

```bash
cd backend
mvn verify -P jmh
```
