import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Server {

    private static Estacionamento estacionamento;
    private static boolean isConfigured = false;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/isconfigured", new IsConfiguredHandler());
        server.createContext("/configurar", new ConfigurarHandler());
        server.createContext("/registroentrada", new RegistroEntradaHandler());
        server.createContext("/registrosaida", new RegistroSaidaHandler());
        server.createContext("/vagas", new VagasHandler());
        server.createContext("/veiculos", new VeiculosHandler());
        server.createContext("/buscar", new BuscarHandler());
        server.createContext("/faturamento", new FaturamentoHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class RegistroEntradaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            if ("POST".equals(t.getRequestMethod())) {
                // Simples, assume body is JSON {"placa":"XXX", "hora":"yyyy-MM-ddTHH:mm"} or current time
                // For simplicity, use current time, placa from query param
                String query = t.getRequestURI().getQuery();
                String placa = query.split("=")[1]; // assume placa=X
                boolean success = estacionamento.registrarEntrada(placa, LocalDateTime.now());
                String response = String.format("{\"success\":%b}", success);
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                t.sendResponseHeaders(405, -1);
            }
        }
    }

    static class RegistroSaidaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            if ("POST".equals(t.getRequestMethod())) {
                String query = t.getRequestURI().getQuery();
                String placa = query.split("=")[1];
                double valor = estacionamento.registrarSaida(placa, LocalDateTime.now());
                String response = String.format("{\"valor\":%.2f}", valor);
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                t.sendResponseHeaders(405, -1);
            }
        }
    }

    static class VagasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            int ocupadas = estacionamento.getVagasOcupadas();
            int disponiveis = estacionamento.getVagasDisponiveis();
            String response = String.format("{\"ocupadas\":%d, \"disponiveis\":%d}", ocupadas, disponiveis);
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class VeiculosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Veiculo v : estacionamento.getVeiculos()) { // Need to add getVeiculos method
                if (!first) sb.append(",");
                sb.append(String.format("{\"placa\":\"%s\", \"horaEntrada\":\"%s\"}", v.getPlaca(), v.getHoraEntrada()));
                first = false;
            }
            sb.append("]");
            String response = sb.toString();
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class BuscarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            String query = t.getRequestURI().getQuery();
            String placa = query.split("=")[1];
            Veiculo v = estacionamento.buscarVeiculo(placa);
            String response;
            if (v == null) {
                response = "{\"found\":false}";
            } else {
                response = String.format("{\"found\":true, \"placa\":\"%s\", \"horaEntrada\":\"%s\"}", v.getPlaca(), v.getHoraEntrada());
            }
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class FaturamentoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (!isConfigured) {
                String response = "{\"error\":\"Capacidade não configurada\"}";
                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(400, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
            double total = estacionamento.getTotalArrecadado();
            String response = String.format("{\"total\":%.2f}", total);
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class IsConfiguredHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = String.format("{\"configured\":%b}", isConfigured);
            t.getResponseHeaders().set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class ConfigurarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if ("POST".equals(t.getRequestMethod())) {
                // Read body for capacity
                java.io.InputStreamReader isr = new java.io.InputStreamReader(t.getRequestBody(), "utf-8");
                java.io.BufferedReader br = new java.io.BufferedReader(isr);
                String body = br.readLine(); // assume simple json like {"capacidade":10}
                if (body != null && body.contains("capacidade")) {
                    int capacidade = 0;
                    try {
                        // simple parse
                        String[] parts = body.replaceAll("[{}\"]", "").split(":");
                        capacidade = Integer.parseInt(parts[1]);
                    } catch (Exception e) {
                        String response = "{\"error\":\"Valor inválido\"}";
                        t.getResponseHeaders().set("Content-Type", "application/json");
                        t.sendResponseHeaders(400, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        return;
                    }
                    if (capacidade > 0) {
                        estacionamento = new Estacionamento(capacidade);
                        isConfigured = true;
                        String response = "{\"success\":true}";
                        t.getResponseHeaders().set("Content-Type", "application/json");
                        t.sendResponseHeaders(200, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        String response = "{\"error\":\"Capacidade deve ser positiva\"}";
                        t.getResponseHeaders().set("Content-Type", "application/json");
                        t.sendResponseHeaders(400, response.length());
                        OutputStream os = t.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                } else {
                    t.sendResponseHeaders(400, -1);
                }
            } else {
                t.sendResponseHeaders(405, -1);
            }
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            URI uri = t.getRequestURI();
            String path = uri.getPath();
            if (path.equals("/") || path.equals("") || path.equals("/index.html")) {
                // Serve page1/index.html
                Path filePath = Paths.get("page1", "index.html");
                if (Files.exists(filePath)) {
                    byte[] fileContent = Files.readAllBytes(filePath);
                    t.getResponseHeaders().set("Content-Type", "text/html");
                    t.sendResponseHeaders(200, fileContent.length);
                    OutputStream os = t.getResponseBody();
                    os.write(fileContent);
                    os.close();
                } else {
                    t.sendResponseHeaders(404, -1);
                }
            } else {
                // Serve other files with basic content-type
                Path filePath = Paths.get("page1" + path);
                if (Files.exists(filePath)) {
                    byte[] fileContent = Files.readAllBytes(filePath);
                    String contentType = getContentType(path);
                    t.getResponseHeaders().set("Content-Type", contentType);
                    t.sendResponseHeaders(200, fileContent.length);
                    OutputStream os = t.getResponseBody();
                    os.write(fileContent);
                    os.close();
                } else {
                    t.sendResponseHeaders(404, -1);
                }
            }
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            if (path.endsWith(".png")) return "image/png";
            return "text/plain";
        }
    }
}
