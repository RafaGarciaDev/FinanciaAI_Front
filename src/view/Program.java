package view;

import com.sun.net.httpserver.HttpServer;

import util.Conexao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;

public class Program {
    public static void main(String[] args) {
        iniciarServidor();
    }

    public static void iniciarServidor() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            FinanciamentoControllerHttp controller = new FinanciamentoControllerHttp();

            // Configura endpoints da API
            server.createContext("/api/clientes", controller);
            server.createContext("/api/imoveis", controller);
            server.createContext("/api/financiamentos", controller);
            server.createContext("/api/simular", controller);
            server.createContext("/api/gerar-pdf", controller);

            // Servir arquivos estáticos
            server.createContext("/", exchange -> {
                try {
                    String path = exchange.getRequestURI().getPath();
                    if (path.equals("/")) path = "/index.html";

                    InputStream is = Program.class.getResourceAsStream("/front" + path);
                    if (is == null) {
                        exchange.sendResponseHeaders(404, 0);
                        return;
                    }

                    String contentType = "text/html";
                    if (path.endsWith(".css")) contentType = "text/css";
                    else if (path.endsWith(".js")) contentType = "application/javascript";
                    else if (path.endsWith(".png")) contentType = "image/png";

                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, 0);

                    try (OutputStream os = exchange.getResponseBody()) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, 0);
                }
            });

            server.setExecutor(null);
            server.start();

            System.out.println("Servidor iniciado na porta 8080");
            System.out.println("Acesse: http://localhost:8080");
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}