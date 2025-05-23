import React from "react";
import useNewsSocket from "../hooks/useNewsSocket";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from "chart.js";
import { Line } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export default function MarketPage() {
  const { news, quotes } = useNewsSocket();

  return (
    <div className="market-page">
      {/* News */}
      <div className="news">
        <h2>Financial News</h2>
        <ul>
          {news.length > 0 ? (
            news.map((newsItem, index) => (
              <li key={index}>
                <h3>{newsItem.title}</h3>
                <a
                  href={newsItem.url}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Read Article
                </a>
              </li>
            ))
          ) : (
            <li>No news available</li>
          )}
        </ul>
      </div>

      {/* Quotes and charts */}
      <div className="charts-container">
        <h2>Quotes</h2>
        <div className="charts">
          {Object.keys(quotes).length > 0 ? (
            Object.entries(quotes).map(([symbol, data]) => {
              const sortedData = [...data].sort(
                (a, b) => a.timestamp - b.timestamp
              );

              const labels = sortedData.map((point) =>
                new Date(point.timestamp).toLocaleTimeString([], {
                  hour: "2-digit",
                  minute: "2-digit",
                })
              );

              const chartData = {
                labels,
                datasets: [
                  {
                    label: `${symbol} — Closing Price`,
                    data: sortedData.map((point) => point.close),
                    borderColor: "rgba(75,192,192,1)",
                    backgroundColor: "rgba(75,192,192,0.2)",
                    fill: true,
                    tension: 0.3,
                  },
                ],
              };

              const options = {
                responsive: true,
                plugins: {
                  legend: { position: "top" },
                  title: {
                    display: true,
                    text: `Closing Price Chart: ${symbol}`,
                  },
                },
                scales: {
                  x: {
                    display: true,
                    title: { display: true, text: "Time" },
                  },
                  y: {
                    display: true,
                    title: { display: true, text: "Price" },
                  },
                },
              };

              return (
                <div key={symbol} className="chart-wrapper">
                  <Line data={chartData} options={options} />
                </div>
              );
            })
          ) : (
            <div>No quotes available</div>
          )}
        </div>
      </div>
    </div>
  );
}
