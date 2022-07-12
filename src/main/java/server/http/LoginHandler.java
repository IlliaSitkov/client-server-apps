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
import utils.Utils;

public class LoginHandler implements HttpHandler {
	
	private final UserRepository userRepo = UserRepositoryImpl.getInstance();
	
	public LoginHandler() {
		System.out.println("Login handler created");
	}
	
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			if(exchange.getRequestMethod().equals("POST")) {
				this.handlePost(exchange);
			} else {
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
	
	private void handlePost(HttpExchange exchange) throws IOException {
		JSONObject object = Utils.getRequestBody(exchange);
		String username = object.getString("username");
		String password = object.getString("password");
		Optional<User> opt = this.userRepo.getByUsername(username);
		if(opt.isPresent() && opt.get().getPassword().equals(password)) {
			Headers responseHeaders = exchange.getResponseHeaders();
	        responseHeaders.add("Access-Control-Allow-Origin", "*");
	        responseHeaders.add("JW Token", JWTManager.createJWT(opt.get().getId().toString(), username));
	        exchange.sendResponseHeaders(200, 0);
	        exchange.close();
		} else {
			byte[] bytes =  Utils.getResponseBytes("message", "Access denied: invalid username or password");
            Utils.sendResponse(exchange, bytes, 403);
		}
		
	}

}
