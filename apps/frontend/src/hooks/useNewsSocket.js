import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "../context/AuthContext";

export default function useNewsSocket() {
  const [news, setNews] = useState([]);
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
      brokerURL: wsUrl, // Используем стандартный WebSocket URL
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("WebSocket connected via STOMP");

        stompClient.subscribe("/topic/news", (message) => {
          if (message.body) {
            try {
              const parsed = JSON.parse(message.body);
              console.log("Received news from Kafka:", parsed);

              if (parsed.articles && Array.isArray(parsed.articles)) {
                setNews(parsed.articles);
              }
            } catch (err) {
              console.error("Error parsing message:", err);
              setNews([message.body]);
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

  return news;
}
