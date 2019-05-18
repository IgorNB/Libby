package com.lig.libby.security.jwt;

import com.lig.libby.security.config.AppPropertiesConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenProviderService {

    private static final Logger logger = LoggerFactory.getLogger(TokenProviderService.class);

    private static final String AUTHORITIES_KEY = "auth";

    private AppPropertiesConfig appPropertiesConfig;

    @Autowired
    public TokenProviderService(AppPropertiesConfig appPropertiesConfig) {
        this.appPropertiesConfig = appPropertiesConfig;
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date nowDate = new Date();
        long now = (nowDate).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + appPropertiesConfig.getAuth().getTokenExpirationMsecForRememberMe());
        } else {
            validity = new Date(now + appPropertiesConfig.getAuth().getTokenExpirationMsec());
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(nowDate)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, appPropertiesConfig.getAuth().getTokenSecret())
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appPropertiesConfig.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .filter(role -> !role.isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appPropertiesConfig.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

}
