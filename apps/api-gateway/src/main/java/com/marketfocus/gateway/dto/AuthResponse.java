package com.marketfocus.gateway.dto;

public class AuthResponse {
    private String token;

    // Конструктор с параметром
    public AuthResponse(String token) {
        this.token = token;
    }

    // Геттер для token
    public String getToken() {
        return token;
    }

    // Сеттер для token
    public void setToken(String token) {
        this.token = token;
    }

    // Переопределенный метод toString для удобства
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                '}';
    }
}
