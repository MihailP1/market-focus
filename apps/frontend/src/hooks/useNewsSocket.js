import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../context/AuthContext";

export default function useMarketSocket() {
  const [news, setNews] = useState([]);
  const [quotes, setQuotes] = useState([]);
  const { isAuthenticated, token: authToken } = useAuth();

  useEffect(() => {
    if (!isAuthenticated) {
      console.warn("User is not authenticated. Skipping WebSocket connection.");
      return;
    }

    const token = authToken || localStorage.getItem("token");
    if (!token) {
      console.warn("Authorization token is missing. Skipping WebSocket connection.");
      return;
    }

    const wsUrl = `ws://localhost:8080/ws/market?token=${token}`;

    const stompClient = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("WebSocket connected via STOMP");

        // Подписка на новости
        stompClient.subscribe("/topic/news", (message) => {
          if (message.body) {
            try {
              const parsed = JSON.parse(message.body);
              console.log("Received news from Kafka:", parsed);

              if (parsed.articles && Array.isArray(parsed.articles)) {
                setNews(parsed.articles);
              }
            } catch (err) {
              console.error("Error parsing news message:", err);
              setNews([message.body]);
            }
          }
        });

        // Подписка на котировки
        stompClient.subscribe("/topic/quotes", (message) => {
          if (message.body) {
            try {
              const parsed = JSON.parse(message.body);
              console.log("Received quotes from Kafka:", parsed);

              // Если пришел массив котировок
              if (Array.isArray(parsed)) {
                const normalized = parsed.map(normalizeQuote);
                setQuotes(normalized);
              } else if (parsed && typeof parsed === "object") {
                const normalizedQuote = normalizeQuote(parsed);
                setQuotes(prev => [normalizedQuote, ...prev]);
              }
            } catch (err) {
              console.error("Error parsing quotes message:", err);
              setQuotes([message.body]);
            }
          }
        });
      },
      onStompError: (frame) => {
        console.error("STOMP Error:", frame);
      },
      onWebSocketError: (error) => {
        console.error("WebSocket Error:", error);
      },
      debug: (str) => console.log(`[STOMP DEBUG]: ${str}`),
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [isAuthenticated, authToken]);

  // Функция нормализации котировки под UI
  function normalizeQuote(quote) {
    return {
      symbol: quote.symbol || "UNKNOWN", // если нет symbol - подставляем UNKNOWN
      price: quote.c ?? quote.price ?? 0, // цена берём из поля c (close) или price, или 0
      timestamp: quote.t ? quote.t * 1000 : Date.now(), // из секунд в мс, или сейчас
      // можно добавить другие поля, если нужно
      change: quote.d ?? 0,       // изменение цены
      changePercent: quote.dp ?? 0, // изменение в %
      high: quote.h ?? 0,
      low: quote.l ?? 0,
      open: quote.o ?? 0,
      prevClose: quote.pc ?? 0,
    };
  }

  return { news, quotes };
}
