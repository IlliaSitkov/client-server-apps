package server.https;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import io.jsonwebtoken.JwtException;
import utils.JWTManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Authenticator;


public class ServerAuth extends Authenticator {
	
	 @Override
     public Result authenticate(HttpExchange httpExchange) {
		 try {
			 List<String> headerList = httpExchange.getRequestHeaders().get("JWToken");
			 if(headerList == null || headerList.isEmpty())
				 return new Failure(403);
			 String jwt = headerList.get(0);
			 Map<String, Object> jwtMap = JWTManager.parseJWT(jwt);
			 String username = (String)jwtMap.get(JWTManager.JWT_USERNAME);
			 String issuer = (String)jwtMap.get(JWTManager.JWT_ISSUER);
			 Date expDate = (Date)jwtMap.get(JWTManager.JWT_EXP_DATE);
			 Date now = new Date(System.currentTimeMillis());
			 if(now.after(expDate) || !issuer.equals(JWTManager.ISSUER))
				 return new Failure(403);
			 return new Success(new HttpPrincipal(username, issuer));
		 } catch(JwtException e) {
			 System.out.println(e.getMessage());
			 return new Failure(403);
		 }
     }
	
}
