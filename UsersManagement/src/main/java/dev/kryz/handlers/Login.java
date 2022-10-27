package dev.kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kryz.models.ClientCredentials;
import dev.kryz.services.LoginUserService;
import dev.kryz.utils.ErrorResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class Login implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ClientCredentials clientCredentials;

    private final LoginUserService service;

    public Login() {
        this.clientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"), System.getenv("CLIENT_SECRET"));
        this.service = new LoginUserService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        response.withHeaders(headers);

        try {
            // convert request body to JSON
            JsonObject payload = JsonParser.parseString(input.getBody()).getAsJsonObject();

            // log in user
            JsonObject authData = service.login(payload, clientCredentials);

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
