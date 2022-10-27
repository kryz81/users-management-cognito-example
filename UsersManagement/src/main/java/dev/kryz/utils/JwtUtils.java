package dev.kryz.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;

public class JwtUtils {
    public static DecodedJWT validateJWTForUser(String jwt, String region, String userPoolId, String audience) {
        RSAKeyProvider keyProvider = new CognitoRSAKeyProvider(region, userPoolId);
        Algorithm algorithm = Algorithm.RSA256(keyProvider);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withClaim("token_use", "id")
                .withAudience(audience)
                .build();

        return jwtVerifier.verify(jwt);
    }
}
