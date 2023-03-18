package com.visionrent.security.jwt;

import com.visionrent.exception.message.ErrorMessage;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 4. CLASS TO IMPLEMENT
 * JWT helper class.
 * Created to everything we need about the JWT token.
 * JwtUtils class (bean) will be used main security worker class (AuthTokenFilter) in every request.
 */

@Component
public class JwtUtils {
    private static final Logger LOGGER=LoggerFactory.getLogger(JwtUtils.class);

    //@Value("${visionrent.app.jwtExpirationMs}")
    public long jwtExprations=86400000;

   //@Value("${visionrent.app.jwtSecret}")
    public String jwtSecret="visionRent@!23";

    public String generateToken(UserDetails userDetails){
        return Jwts.builder().setSubject(userDetails.getUsername())
                //when issued?
                .setIssuedAt(new Date())
                //when it will be expired?
                .setExpiration(new Date(new Date().getTime()+jwtExprations))
                //which signature algorithm and my key
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }

    public String getEmailFromToken(String token){
        return  Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

    }

    public boolean validateJwtToken(String token){

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
            //TODO: check diffrence between | and ||
            // | is check all conditions but || brokes the condition if one of the condition is true or false does not look at the other conditions.
            //in here we need to check all exception so we use |

            //homework create custom messages for any possible jwt exceptions
        }catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |IllegalArgumentException e){
            LOGGER.error(ErrorMessage.JWT_TOKEN_MESSAGE);
        }

        /*
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            LOGGER.error(ErrorMessage.EXPIRED_JWT_MESSAGE);
        } catch (UnsupportedJwtException e) {
            LOGGER.error(ErrorMessage.JWT_TOKEN_MESSAGE);
        } catch (MalformedJwtException e) {
            LOGGER.error(ErrorMessage.JWT_TOKEN_MESSAGE);
        } catch (SignatureException e) {
            LOGGER.error(ErrorMessage.SIGNATURE_NOT_MATCH_MESSAGE);
        } catch (IllegalArgumentException e) {
            LOGGER.error(ErrorMessage.ILLEGAL_ARGUMENT_MESSAGE);
        }
*/
        return false;
    }
}
