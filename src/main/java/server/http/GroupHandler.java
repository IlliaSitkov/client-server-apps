package server.http;

import java.io.IOException;

import org.json.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import exceptions.GroupNotFoundException;
import exceptions.MethodNotSupported;
import exceptions.NameNotUniqueException;
import model.Group;
import service.group.GroupService;
import service.group.GroupServiceImpl;
import utils.Routes;
import utils.Utils;

public class GroupHandler implements HttpHandler {

	private final GroupService groupService = GroupServiceImpl.getInstance();
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			switch(exchange.getRequestMethod()) {
			case "GET" :
				this.handleGet(exchange);
				break;
			case "POST" :
				this.handlePost(exchange);
				break;
			case "PUT" :
				this.handlePut(exchange);
				break;
			case "DELETE" :
				this.handleDelete(exchange);
				break;
			case "OPTIONS" :
				this.handleOptions(exchange);
				break;
			default:
                System.out.println(exchange.getRequestMethod());
                System.out.println("Method not supported");
                throw new RuntimeException("Method not supported");
			}
		} catch(Exception e) {
			Utils.processException(exchange, e.getMessage(), 400);
		}
	}
	
	private void handleGet(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		if(Utils.exactURI(uri, Routes.GROUP_ROUTE)) {
			byte[] bytes = Utils.getResponseBytes("result", this.groupService.getAllGroups());
            Utils.sendResponse(exchange, bytes, 200);
		} else {
			try {
				long groupId = Utils.getIdFromPath(uri, Routes.GROUP_ROUTE);
				Group group = this.groupService.getGroupById(groupId);
				byte[] bytes =  Utils.getResponseBytes("result", new JSONObject(group));
				Utils.sendResponse(exchange, bytes, 200);
			} catch(GroupNotFoundException e) {
				Utils.processException(exchange, e.getMessage(), 404);
			}
		}
	}
	
	private void handlePost(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		if (Utils.exactURI(uri, Routes.GROUP_ROUTE)) {
			try {
				JSONObject object = Utils.getRequestBody(exchange);
				Group group = this.groupService.createGroup(
						(String)object.getString("name"), 
						(String)object.getString("description")
					);
				byte[] bytes = Utils.getResponseBytes("result", group.getId());
				System.out.println("Group created");
	            Utils.sendResponse(exchange, bytes, 201);
			} catch(NameNotUniqueException e) {
				Utils.processException(exchange, e.getMessage(), 409);
			}
		} else
			throw new MethodNotSupported("POST", uri);
	}
	
	private void handlePut(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		try {
			long id = Utils.getIdFromPath(uri, Routes.GROUP_ROUTE);
			JSONObject object = Utils.getRequestBody(exchange);
			this.groupService.updateGroup(id, (String)object.get("name"), (String)object.get("description"));
			System.out.println("Group updated");
			Utils.sendResponseNoContent(exchange, 204);
		} catch(NameNotUniqueException e) {
			Utils.processException(exchange, e.getMessage(), 409);
		} catch(GroupNotFoundException e) {
			Utils.processException(exchange, e.getMessage(), 404);
		}
	}
	
	private void handleDelete(HttpExchange exchange) throws IOException {
		String uri = exchange.getRequestURI().toString();
		try {
			long groupId = Utils.getIdFromPath(uri, Routes.GROUP_ROUTE);
			this.groupService.deleteGroup(groupId);
			System.out.println("Group deleted");
			Utils.sendResponseNoContent(exchange, 204);
		} catch(GroupNotFoundException e) {
			Utils.processException(exchange, e.getMessage(), 404);
		}
	}
	
	private void handleOptions(HttpExchange exchange) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, DELETE, POST, PUT, OPTIONS");
        responseHeaders.add("Access-Control-Allow-Headers", "Content-Type, JWToken");
        exchange.sendResponseHeaders(204, -1);
	}
	
	

}
