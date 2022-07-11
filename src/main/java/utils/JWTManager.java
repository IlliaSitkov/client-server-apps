package utils;

import java.security.Key;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTManager {

	private static final String SECRET_KEY = "ATBVDWEXFZH2J3M5N6P8R9SAUCVDWFYGZH3K4M5P7Q8RATBUCVEXFYG2J3";
	
	private static final long JWT_TTL_MILLIS = 300000;
	
	public static final String JWT_USER_ID = "userId";
	
	public static final String JWT_USERNAME = "username";
	
	public static String createJWT(String userId, String username) {
		 //The JWT signature algorithm we will be using to sign the token
	    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	 
	    long nowMillis = System.currentTimeMillis();
	    Date now = new Date(nowMillis);
	 
	    //We will sign our JWT with our ApiKey secret
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	 
	    //Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder()
									.claim(JWT_USERNAME, username)
									.claim(JWT_USER_ID, userId)
	                                .setIssuedAt(now)
	                                .signWith(signingKey, signatureAlgorithm);
	 
    	long expMillis = nowMillis + JWT_TTL_MILLIS;
        builder.setExpiration(new Date(expMillis));
	    return builder.compact();
	}
	
	
	@SuppressWarnings("deprecation")
	public static Set<Entry<String, Object>> parseJWT(String jwt) throws UnsupportedJwtException {
	    Claims claims = Jwts
	    			.parser()         
	    			.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
	    			.parseClaimsJws(jwt).getBody();
	    return claims.entrySet();
	}
	
}
