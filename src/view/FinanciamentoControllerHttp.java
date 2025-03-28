package view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.FinanciamentoController;
import dao.ClienteDAO;
import dao.FinanciamentoDAO;
import dao.ImovelDAO;
import entities.Cliente;
import entities.Imovel;
import enums.TipoAmortizacao;
import enums.TipoImovel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class FinanciamentoControllerHttp implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ImovelDAO imovelDAO = new ImovelDAO();
    private FinanciamentoDAO financiamentoDAO = new FinanciamentoDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            if (path.startsWith("/api/clientes")) {
                handleClientes(exchange, method);
            } else if (path.startsWith("/api/imoveis")) {
                handleImoveis(exchange, method);
            } else if (path.equals("/api/simular")) {
                handleSimulacao(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Endpoint não encontrado\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private void handleClientes(HttpExchange exchange, String method) throws IOException {
        if (method.equalsIgnoreCase("GET")) {
            listarClientes(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            criarCliente(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
        }
    }

    private void listarClientes(HttpExchange exchange) throws IOException {
        try {
            List<Cliente> clientes = clienteDAO.listarClientes();
            String json = gson.toJson(clientes);
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Erro ao listar clientes: " + e.getMessage() + "\"}");
        }
    }

    private void criarCliente(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Cliente cliente = gson.fromJson(isr, Cliente.class);
            clienteDAO.adicionarCliente(cliente);
            sendResponse(exchange, 201, "{\"message\":\"Cliente criado com sucesso\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private void handleImoveis(HttpExchange exchange, String method) throws IOException {
        if (method.equalsIgnoreCase("GET")) {
            listarImoveis(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            criarImovel(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
        }
    }

    private void listarImoveis(HttpExchange exchange) throws IOException {
        try {
            List<Imovel> imoveis = imovelDAO.listarImoveis();
            String json = gson.toJson(imoveis);
            sendResponse(exchange, 200, json);
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Erro ao listar imóveis\"}");
        }
    }

    private void criarImovel(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Imovel imovel = gson.fromJson(isr, Imovel.class);
            imovelDAO.adicionarImovel(imovel);
            sendResponse(exchange, 201, "{\"message\":\"Imóvel criado com sucesso\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private void handleSimulacao(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "{\"error\":\"Método não permitido\"}");
            return;
        }

        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            Map<?, ?> params = gson.fromJson(isr, Map.class);

            FinanciamentoController controller = new FinanciamentoController();
            controller.simularFinanciamento(
                    params.get("clienteCpf").toString(),
                    TipoImovel.valueOf(params.get("tipoImovel").toString()),
                    Double.parseDouble(params.get("valorImovel").toString()),
                    Double.parseDouble(params.get("taxaJurosAnual").toString()),
                    Double.parseDouble(params.get("valorEntrada").toString()),
                    Integer.parseInt(params.get("prazo").toString()),
                    TipoAmortizacao.valueOf(params.get("tipoAmortizacao").toString())
            );

            sendResponse(exchange, 200, "{\"message\":\"Simulação realizada com sucesso\"}");
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}