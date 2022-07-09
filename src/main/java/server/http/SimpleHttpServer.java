package server.http;

import com.sun.net.httpserver.*;
import utils.Routes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SimpleHttpServer {

    private final HttpServer server;

    public SimpleHttpServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8765), 0);

        HttpContext context = server.createContext(Routes.PRODUCT_ROUTE, new ProductHandler());

        context.setAuthenticator(new Auth());

        server.setExecutor(Executors.newFixedThreadPool(5));
        server.start();
    }

    public void stop() {
        server.stop(2000);
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


    public static void main(String[] args) throws Exception {
        SimpleHttpServer httpServer = new SimpleHttpServer();
    }

}
