package server;

import com.sun.net.httpserver.*;
import org.json.JSONObject;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import utils.Routes;
import utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        HttpsServer server = HttpsServer.create();
        server.bind(new InetSocketAddress(8765), 0);

        HttpContext context = server.createContext(Routes.PRODUCT_ROUTE, new ProductHandler());

        context.setAuthenticator(new Auth());

        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
    }

    static class ProductHandler implements HttpHandler {

        private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println(exchange.getRequestURI());
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
            }

            JSONObject object = new JSONObject();
            object.put("key", List.of("Val1", "Val2", "Val3"));
            object.put("loo", productRepository.getAll());

            sendResponse(exchange, object.toString().getBytes(),200);


        }

        private void sendResponse(HttpExchange exchange, byte[] bytes, int responseCode) throws IOException {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add("Content-Type","application/json");

            exchange.sendResponseHeaders(responseCode, bytes.length);
            OutputStream outputStream = exchange.getResponseBody();

            outputStream.write(bytes);
            outputStream.close();
        }


        private void handleGet(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().toString();
            if (Utils.exactURI(uri, Routes.PRODUCT_ROUTE)) {
                JSONObject object = new JSONObject();
                object.put("result", productRepository.getAll());
                sendResponse(exchange, object.toString().getBytes(), 200);
            } else {
                try {
                    long productId = Utils.getIdFromPath(uri, Routes.PRODUCT_ROUTE);
                    JSONObject object = new JSONObject();
                    object.put("result", productRepository.getById(productId));
                    sendResponse(exchange, object.toString().getBytes(), 200);
                } catch (Exception e) {

                }
            }
        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}
