package server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.*;
import model.Product;
import org.json.JSONException;
import org.json.JSONObject;
import service.product.ProductService;
import service.product.ProductServiceImpl;
import utils.Routes;
import utils.Utils;

import java.io.IOException;

public class ProductHandler implements HttpHandler {

    private final ProductService productService = ProductServiceImpl.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                case "PATCH":
                    handlePatch(exchange);
                    break;
                default:
                    throw new RuntimeException("Method not supported");
            }
        } catch (Exception e) {
            e.printStackTrace();
            byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
            Utils.sendResponse(exchange, bytes, 400);
        }
    }

    private void handlePatch(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        try {
            long productId = Utils.getIdFromPath(uri, Routes.PRODUCT_ROUTE);

            JSONObject object = Utils.getRequestBody(exchange);

            if (!object.isNull("add")) {
                productService.addProducts(productId, Integer.parseInt((String) object.get("add")));
            }
            else if (!object.isNull("take")) {
                productService.takeProducts(productId, Integer.parseInt((String) object.get("take")));
            }
            Utils.sendResponseNoContent(exchange, 204);
        } catch (InvalidNumberException e) {
            e.printStackTrace();
            byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
            Utils.sendResponse(exchange, bytes, 409);
        } catch (ProductNotFoundException | PathVariableNotFound e) {
            e.printStackTrace();
            byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
            Utils.sendResponse(exchange, bytes, 404);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        if (Utils.exactURI(uri, Routes.PRODUCT_ROUTE)) {
            productService.deleteAllProducts();
            Utils.sendResponseNoContent(exchange, 204);
        } else {
            try {
                long productId = Utils.getIdFromPath(uri, Routes.PRODUCT_ROUTE);
                productService.deleteProduct(productId);
                Utils.sendResponseNoContent(exchange, 204);
            } catch (ProductNotFoundException | PathVariableNotFound e) {
                e.printStackTrace();
                byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
                Utils.sendResponse(exchange, bytes, 404);
            }
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        try {
            long productId = Utils.getIdFromPath(uri, Routes.PRODUCT_ROUTE);

            JSONObject object = Utils.getRequestBody(exchange);

            productService.updateProduct(productId,
                    (String) object.get("name"),
                    (String) object.get("description"),
                    (String) object.get("producer"),
                    Double.parseDouble((String) object.get("price")),
                    Long.parseLong((String) object.get("groupId")));

            Utils.sendResponseNoContent(exchange, 204);
        } catch (JSONException | NameNotUniqueException | InvalidStringException | InvalidNumberException e) {
            e.printStackTrace();
            byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
            Utils.sendResponse(exchange, bytes, 409);
        } catch (ProductNotFoundException | PathVariableNotFound | GroupNotFoundException e) {
            e.printStackTrace();
            byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
            Utils.sendResponse(exchange, bytes, 404);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        if (Utils.exactURI(uri, Routes.PRODUCT_ROUTE)) {
            try {
                JSONObject object = Utils.getRequestBody(exchange);
                Product product = productService.createProduct(
                        (String) object.get("name"),
                        (String) object.get("description"),
                        (String) object.get("producer"),
                        Integer.parseInt((String) object.get("quantity")),
                        Double.parseDouble((String) object.get("price")),
                        Long.parseLong((String) object.get("groupId"))
                );

                byte[] bytes = Utils.getResponseBytes("result", product.getId());
                Utils.sendResponse(exchange, bytes, 201);
            } catch (JSONException | NameNotUniqueException | InvalidStringException | InvalidNumberException e) {
                e.printStackTrace();
                byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
                Utils.sendResponse(exchange, bytes, 409);
            } catch (GroupNotFoundException e) {
                e.printStackTrace();
                byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
                Utils.sendResponse(exchange, bytes, 404);
            }
        } else {
            throw new MethodNotSupported("POST", uri);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().toString();
        if (Utils.exactURI(uri, Routes.PRODUCT_ROUTE)) {
            byte[] bytes = Utils.getResponseBytes("result", productService.getFilteredProducts(exchange.getRequestURI().getQuery()));
            Utils.sendResponse(exchange, bytes, 200);
        } else {
            try {
                long productId = Utils.getIdFromPath(uri, Routes.PRODUCT_ROUTE);
                Product product = productService.getProductById(productId);
                byte[] bytes =  Utils.getResponseBytes("result", new JSONObject(product));
                Utils.sendResponse(exchange, bytes, 200);
            } catch (ProductNotFoundException | PathVariableNotFound e) {
                e.printStackTrace();
                byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
                Utils.sendResponse(exchange, bytes, 404);
            }
        }
    }
}