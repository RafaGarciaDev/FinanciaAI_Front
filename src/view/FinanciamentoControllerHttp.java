package com.financiai.controller;

import com.financiai.dao.*;
import com.financiai.model.entities.*;
import com.financiai.model.enums.*;
import com.financiai.util.GeradorPDF;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FinanciamentoControllerHttp implements HttpHandler {
    private FinanciamentoController controller = new FinanciamentoController();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.startsWith("/api/clientes")) {
                handleClientes(exchange, method);
            } else if (path.startsWith("/api/imoveis")) {
                handleImoveis(exchange, method);
            } else if (path.startsWith("/api/financiamentos")) {
                handleFinanciamentos(exchange, method);
            } else if (path.equals("/api/simular")) {
                handleSimulacao(exchange);
            } else {
                sendResponse(exchange, 404, "Endpoint não encontrado");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Erro interno: " + e.getMessage());
        }
    }

    private void handleSimulacao(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Método não permitido");
            return;
        }

        // Parse dos parâmetros do formulário
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseFormData(requestBody);

        try {
            // Extrai os parâmetros e converte
            String clienteCpf = params.get("clienteCpf");
            TipoImovel tipoImovel = TipoImovel.valueOf(params.get("tipoImovel"));
            double valorTotalImovel = Double.parseDouble(params.get("valorTotalImovel"));
            double taxaJurosAnual = Double.parseDouble(params.get("taxaJurosAnual"));
            double valorEntrada = Double.parseDouble(params.get("valorEntrada"));
            int prazo = Integer.parseInt(params.get("prazo"));
            TipoAmortizacao tipoAmortizacao = TipoAmortizacao.valueOf(params.get("tipoAmortizacao"));

            // Chama a simulação
            controller.simularFinanciamento(
                    clienteCpf, tipoImovel, valorTotalImovel,
                    taxaJurosAnual, valorEntrada, prazo, tipoAmortizacao);

            sendResponse(exchange, 200, "Simulação realizada com sucesso!");
        } catch (Exception e) {
            sendResponse(exchange, 400, "Erro na simulação: " + e.getMessage());
        }
    }

    // Métodos auxiliares para parse e response
    private Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Outros métodos handle...
}
