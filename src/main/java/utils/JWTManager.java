package utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTManager {

	private static final String SECRET_KEY = "ATBVDWEXFZH2J3M5N6P8R9SAUCVDWFYGZH3K4M5P7Q8RATBUCVEXFYG2J3";
	
	private static final long JWT_TTL_MILLIS = 300000;
	
	public static final String JWT_USERNAME = "username";
	
	public static final String JWT_ISSUER = "issuer";
	
	public static final String JWT_EXP_DATE = "expDate";
	
	public static final String ISSUER = "ua.com.client-server-app";
	
	public static String createJWT(String username) {
	    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	 
	    long nowMillis = System.currentTimeMillis();
	    Date now = new Date(nowMillis);
	 
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	 
		JwtBuilder builder = Jwts.builder()
									.claim(JWT_USERNAME, username)
	                                .setIssuer(ISSUER)
									.setIssuedAt(now)
	                                .signWith(signingKey, signatureAlgorithm);
	 
    	long expMillis = nowMillis + JWT_TTL_MILLIS;
        builder.setExpiration(new Date(expMillis));
	    return builder.compact();
	}
	
	
	@SuppressWarnings("deprecation")
	public static Map<String, Object> parseJWT(String jwt) throws JwtException {
	    Claims claims = Jwts
	    			.parser()         
	    			.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
	    			.parseClaimsJws(jwt).getBody();
	    Map<String, Object> map = new HashMap<>();
	    map.put(JWT_ISSUER, claims.getIssuer());
	    map.put(JWT_USERNAME, claims.get(JWT_USERNAME));
	    map.put(JWT_EXP_DATE, claims.getExpiration());
	    return map;
	}
	
}
