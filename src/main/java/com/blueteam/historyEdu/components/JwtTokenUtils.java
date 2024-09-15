package com.blueteam.historyEdu.components;

import com.blueteam.historyEdu.entities.Token;
import com.blueteam.historyEdu.entities.User;
import com.blueteam.historyEdu.exceptions.InvalidParamException;
import com.blueteam.historyEdu.utils.MessageKeys;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
//    private final LocalizationUtils localizationUtils;

    @Value("${jwt.expiration}")
    private int expiration; //save to an environment variable

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final com.blueteam.historyEdu.repositories.ITokenRepository ITokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user) throws InvalidParamException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().getRoleName());
        claims.put("email", user.getEmail());
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            claims.put("phoneNumber", user.getPhoneNumber());
        }

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new InvalidParamException(MessageKeys.TOKEN_GENERATION_FAILED, e);
        }
    }

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        return Encoders.BASE64.encode(keyBytes);
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI="));
        return Keys.hmacShaKeyFor(bytes);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> extractIdentifier(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.get("email", String.class);
        String phoneNumber = claims.get("phoneNumber", String.class);
        Map<String, String> identifiers = new HashMap<>();
        identifiers.put("email", email);
        identifiers.put("phoneNumber", phoneNumber);
        return identifiers;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || token.isEmpty()) {
            logger.error("Token is null or empty");
            return false;
        }
        try {
            Map<String, String> identifiers = extractIdentifier(token);
            Token existingToken = ITokenRepository.findByToken(token);
            if (existingToken == null || existingToken.isRevoked()) {
                return false;
            }
            String identifier = identifiers.get("email") != null ? identifiers.get("email") : identifiers.get("phoneNumber");
            return (identifier.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
