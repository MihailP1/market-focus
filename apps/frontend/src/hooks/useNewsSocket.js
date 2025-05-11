import { useEffect, useState } from "react";
import SockJS from "sockjs-client";
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

    const socket = new SockJS(`http://localhost:8080/ws/market?token=${token}`);

    const stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("WebSocket connected");

        stompClient.subscribe("/topic/news", (message) => {
          if (message.body) {
            try {
              const parsed = JSON.parse(message.body);
              console.log("Received news from Kafka:", parsed);  // Логируем полученные данные

              // Извлекаем новости из поля "articles"
              if (parsed.articles && Array.isArray(parsed.articles)) {
                // Полностью обновляем новости, заменяя старые на новые
                setNews(parsed.articles);
              }
            } catch (err) {
              console.error("Error parsing message:", err);
              setNews([message.body]); // Если произошла ошибка парсинга, сохраняем сообщение как есть
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
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, [isAuthenticated, authToken]);

  return news;
}
