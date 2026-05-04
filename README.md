# TradeFlow — Trading Terminal Simulator

Most trading simulators are too simplified to be useful for learning how real systems work. TradeFlow is a full-stack paper-trading platform built to replicate the architecture and feel of a professional terminal, with real-time price feeds, an order matching engine, and a Bloomberg-style UI, all running locally with no external dependencies.

<br />

<img width="1749" height="996" alt="TradeFlow Terminal" src="https://github.com/user-attachments/assets/5dcb7859-4d56-4406-95ad-e9dd4d7d8f62" />

<br />

## What it does

📡 &nbsp;**Streams live market data** across 28 symbols in parallel, pushing price updates to all connected clients at 2 Hz via WebSocket

⚡ &nbsp;**Matches orders in real time** through an in-memory engine supporting market and limit orders, with automatic retry, rate limiting, and notional cap enforcement

📊 &nbsp;**Renders a dense trading workspace** with a candlestick chart, order book, watchlist, market breadth heatmap, and scanner. All panels are draggable and resizable.

🛡️ &nbsp;**Detects and flags suspicious activity.** Oversized orders and high-frequency bursts trigger fraud alerts surfaced directly in the UI.

## Tech Stack

<table>
  <tr>
    <td align="center" width="110"><strong>Backend</strong></td>
    <td>
      <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" />
      <img src="https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=spring-boot&logoColor=white" />
      <img src="https://img.shields.io/badge/WebSocket-STOMP-000000?style=flat-square&logo=socket.io&logoColor=white" />
    </td>
  </tr>
  <tr>
    <td align="center"><strong>Frontend</strong></td>
    <td>
      <img src="https://img.shields.io/badge/React-18-61DAFB?style=flat-square&logo=react&logoColor=black" />
      <img src="https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white" />
      <img src="https://img.shields.io/badge/TradingView-Charts-2962FF?style=flat-square&logo=tradingview&logoColor=white" />
    </td>
  </tr>
</table>

<br />

## Architecture

<img width="1536" height="1024" alt="TradeFlow Architecture" src="https://github.com/user-attachments/assets/5d92a908-8f10-4b66-b4d1-c57ca8b5294d" />

<br />

## Getting Started

Requires Java 17 and Node 18+. No database or external services needed.

```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend (separate terminal)
cd frontend && npm install && npm run dev
```

Open `http://localhost:5173`. Cash, positions, and trade history are held in-memory for the session.
