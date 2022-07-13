package server.http;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import model.User;
import repository.user.UserRepository;
import repository.user.UserRepositoryImpl;
import utils.JWTManager;
import utils.MD5Hasher;
import utils.Utils;

public class LoginHandler implements HttpHandler {
	
	private final UserRepository userRepo = UserRepositoryImpl.getInstance();
	
	private static final String JWT_HEADER_NAME = "Jwtoken";
	
	public LoginHandler() {
		System.out.println("Login handler created");
	}
	
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			switch(exchange.getRequestMethod()) {
				case "POST" :
					this.handlePost(exchange);
					break;
				case "OPTIONS" :
					this.handleOptions(exchange);
					break;
				default :
					System.out.println(exchange.getRequestMethod());
					System.out.println("Method not supported");
					throw new RuntimeException("Method not supported");
			}
		} catch(Exception e) {
			e.printStackTrace();
			byte[] bytes =  Utils.getResponseBytes("message", e.getMessage());
			Utils.sendResponse(exchange, bytes, 400);
		}
	}
	
	private void handleOptions(HttpExchange exchange) throws IOException {
        Utils.addCorsHeaders(exchange);
        exchange.sendResponseHeaders(204, -1);
    }
	
	private void handlePost(HttpExchange exchange) throws IOException {
		JSONObject object = Utils.getRequestBody(exchange);
		String username = object.getString("username");
		String password = object.getString("password");
		String passwordHash = MD5Hasher.getHash(password);
		Optional<User> opt = this.userRepo.getByUsername(username);
		if(opt.isPresent() && opt.get().getPassword().equals(passwordHash)) {
			Headers responseHeaders = exchange.getResponseHeaders();
	        responseHeaders.add("Access-Control-Allow-Origin", "*");
	        responseHeaders.add("Access-Control-Expose-Headers", "*");
	        responseHeaders.add(JWT_HEADER_NAME, JWTManager.createJWT(username));
	        exchange.sendResponseHeaders(200, 0);
	        exchange.close();
		} else {
			byte[] bytes =  Utils.getResponseBytes("message", "Access denied: invalid username or password");
            Utils.sendResponse(exchange, bytes, 403);
		}
		
	}

}
