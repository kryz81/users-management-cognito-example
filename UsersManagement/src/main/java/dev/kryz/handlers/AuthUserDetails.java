package dev.kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kryz.services.AuthUserDetailsService;
import dev.kryz.utils.ErrorResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class AuthUserDetails implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AuthUserDetailsService service;

    public AuthUserDetails() {
        this.service = new AuthUserDetailsService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.withHeaders(headers);

        try {
            String accessToken = input.getHeaders().get("authorization");

            if (accessToken == null || accessToken.equals("")) {
                return response.withStatusCode(401).withBody(new ErrorResponse("No access token found").getAsString());
            }

            // log in user
            JsonObject authData = service.getDetails(accessToken);

            // convert auth data to string
            String responseBody = new Gson().toJson(authData, JsonObject.class);

            // return auth data
            return response.withStatusCode(200).withBody(responseBody);
        } catch (AwsServiceException e) {
            context.getLogger().log(e.awsErrorDetails().errorMessage());
            return response
                    .withStatusCode(500)
                    .withBody(new ErrorResponse(e.awsErrorDetails().errorMessage()).getAsString());
        }
    }
}
