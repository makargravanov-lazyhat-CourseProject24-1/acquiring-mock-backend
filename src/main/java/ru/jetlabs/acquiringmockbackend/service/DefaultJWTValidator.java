package ru.jetlabs.acquiringmockbackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import ru.jetlabs.acquiringmockbackend.util.ConcurrentJWTKeys;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DefaultJWTValidator implements JWTValidator{
    private final ConcurrentJWTKeys jwtKeys;

    public DefaultJWTValidator(ConcurrentJWTKeys jwtKeys) {
        this.jwtKeys = jwtKeys;
    }

    private DecodedJWT secondInternal(String jwt){
        try {
            DecodedJWT decodedJWT = JWT
                    .require(Algorithm.HMAC256(jwtKeys.getOldKey()))
                    .build()
                    .verify(jwt);
            LocalDateTime expirationDateTime = LocalDateTime
                    .ofInstant(decodedJWT
                                    .getExpiresAt()
                                    .toInstant(),
                            ZoneId
                                    .systemDefault());
            if (jwtKeys.getTimeOfOld().isAfter(expirationDateTime)) {
                return decodedJWT;
            } else {
                return null;
            }
        } catch (JWTVerificationException | NullPointerException | NumberFormatException e) {
            return null;
        }
    }
    private DecodedJWT internal(String jwt) {
        try {
            return JWT
                    .require(Algorithm
                            .HMAC256(jwtKeys
                                    .getActualKey()))
                    .build()
                    .verify(jwt);
        } catch (JWTVerificationException | NullPointerException | NumberFormatException e) {
            return secondInternal(jwt);
        }
    }

    @Override
    public DecodedJWT validate(String token) throws NullPointerException{
        System.out.println(this.getClass());
        DecodedJWT decodedJWT = internal(token);
        if(decodedJWT==null){
            throw new NullPointerException("Invalid token");
        }
        try {
            getIdFromToken(decodedJWT);
        }catch (NullPointerException e){
            throw new NullPointerException("Invalid token");
        }
        return decodedJWT;
    }

    @Override
    public Long getIdFromToken(DecodedJWT token) throws NullPointerException{
        String l = token.getSubject();
        if(l==null||l.isEmpty()){
            throw new NullPointerException("Invalid token with no Id");
        }else {
            try {
                return Long.valueOf(l);
            }catch (NumberFormatException e){
                throw new NullPointerException("Invalid token with invalid Id");
            }
        }
    }

}
