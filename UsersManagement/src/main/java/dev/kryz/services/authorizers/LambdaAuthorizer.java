package dev.kryz.services.authorizers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.kryz.utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;

public class LambdaAuthorizer implements RequestHandler<APIGatewayProxyRequestEvent, Map<String, Object>> {
    @Override
    public Map<String, Object> handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        boolean isAuthorized = true;
        String username = "";

        try {
            String jwt = input.getHeaders().get("authorization");
            String region = System.getenv("AWS_REGION");
            String userPoolId = System.getenv("COGNITO_USER_POOL_ID");
            String audience = System.getenv("CLIENT_ID");

            DecodedJWT decodedJWT = JwtUtils.validateJWTForUser(jwt, region, userPoolId, audience);
            username = decodedJWT.getSubject();
        } catch (RuntimeException e) {
            context.getLogger().log(e.getMessage());
            isAuthorized = false;
        }

        Map<String, Object> ctx = new HashMap<>();
        ctx.put("authUser", username);
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthorized", isAuthorized);
        response.put("context", ctx);

       return response;
    }
}
