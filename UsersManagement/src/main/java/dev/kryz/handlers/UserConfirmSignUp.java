package dev.kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kryz.models.ClientCredentials;
import dev.kryz.services.ConfirmUserService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class UserConfirmSignUp implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ClientCredentials clientCredentials;

    private final ConfirmUserService service;

    public UserConfirmSignUp() {
        this.clientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"), System.getenv("CLIENT_SECRET"));
        this.service = new ConfirmUserService();
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // convert request body to JSON
            JsonObject payload = JsonParser.parseString(input.getBody()).getAsJsonObject();

            // confirm user
            JsonObject createdUserData = service.confirmUser(payload, clientCredentials);

            // convert result of confirmation to string
            String responseBody = new Gson().toJson(createdUserData, JsonObject.class);

            // return confirmation details
            return response.withStatusCode(200).withBody(responseBody);
        } catch (AwsServiceException e) {
            context.getLogger().log(e.awsErrorDetails().errorMessage());
            return response.withStatusCode(500).withBody(e.awsErrorDetails().errorMessage());
        }
    }
}
