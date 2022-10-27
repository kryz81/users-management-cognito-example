package dev.kryz.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kryz.models.ClientCredentials;
import dev.kryz.services.AddUserToGroupService;
import dev.kryz.utils.ErrorResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class AddUserToGroup implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ClientCredentials clientCredentials;

    private final String userPoolId;

    private final AddUserToGroupService service;

    public AddUserToGroup() {
        this.clientCredentials = new ClientCredentials(System.getenv("CLIENT_ID"), System.getenv("CLIENT_SECRET"));
        this.userPoolId = System.getenv("COGNITO_USER_POOL_ID");
        this.service = new AddUserToGroupService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // convert request body to JSON
            JsonObject payload = JsonParser.parseString(input.getBody()).getAsJsonObject();

            // add user to group
            JsonObject authData = service.add(payload, clientCredentials, userPoolId);

            String responseBody = new Gson().toJson(authData, JsonObject.class);
            return response.withStatusCode(200).withBody(responseBody);
        } catch (AwsServiceException e) {
            context.getLogger().log(e.awsErrorDetails().errorMessage());
            return response
                    .withStatusCode(500)
                    .withBody(new ErrorResponse(e.awsErrorDetails().errorMessage()).getAsString());
        }
    }
}
