package com.savi.organic.service;

import com.savi.organic.data.User;
import com.savi.organic.exceptions.UnAuthorizedException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenGeneratorService {
    @Value("${token.generator.auth.key}")
    private String authorizationKey;

    @Value("${token.generator.auth.signature}")
    private String authorizationSignature;

    @Bean
    public TokenGeneratorService tokenService(){
        return new TokenGeneratorService();
    }

    private void validateJWTAndAuthGroup(String token, String authGroup) throws AuthenticationException {
        if (StringUtils.isEmpty(token)) {
            throw new UnAuthorizedException("Authorization header is missing");
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(authorizationKey))
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new UnAuthorizedException("Error while parsing token : " + e.getMessage());
        }

        if (claims.getExpiration().before(new Date()))
            throw new AuthenticationException("Token has been expired");

        if (!claims.get("authType").equals(authGroup))
            throw new AuthenticationException("Token is not of required auth group");
    }

    public String generateJWT(User user){
        JwtBuilder jwts = Jwts.builder();
        return jwts.setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(SignatureAlgorithm.forName(authorizationSignature), authorizationKey)
                .compressWith(CompressionCodecs.DEFLATE)
                .claim("username", user.getUsername())
                .claim("id", user.getId())
                .claim("authGroup", user.getAuthGroup())
                .compact();
    }

    private Date getExpirationDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 3);
        return calendar.getTime();
    }

}
