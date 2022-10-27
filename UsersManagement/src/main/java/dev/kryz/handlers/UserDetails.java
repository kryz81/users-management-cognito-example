package dev.kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.kryz.services.UserDetailsService;
import dev.kryz.utils.ErrorResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class UserDetails implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final UserDetailsService service;

    private final String userPoolId;

    public UserDetails() {
        this.service = new UserDetailsService();
        this.userPoolId = System.getenv("COGNITO_USER_POOL_ID");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.withHeaders(headers);

        try {
            String username = input.getPathParameters().get("userName");

            if (username == null || username.equals("")) {
                return response.withStatusCode(404).withBody(new ErrorResponse("Invalid username").getAsString());
            }

            // log in user
            JsonObject authData = service.getDetailsByUsername(username, userPoolId);

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
