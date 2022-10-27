package dev.kryz.services;

import com.google.gson.JsonObject;
import dev.kryz.models.ClientCredentials;
import dev.kryz.utils.HashGenerator;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpResponse;

public class ConfirmUserService {
    private final CognitoIdentityProviderClient client;

    public ConfirmUserService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public ConfirmUserService(CognitoIdentityProviderClient client) {
        this.client = client;
    }

    public JsonObject confirmUser(JsonObject confirmationData, ClientCredentials clientCredentials) {
        String username = confirmationData.get("email").getAsString();
        String confirmationCode = confirmationData.get("code").getAsString();

        // create secret hash
        String hash = HashGenerator.generateHash(clientCredentials, username);

        // create sign-up request
        ConfirmSignUpRequest request = ConfirmSignUpRequest
                .builder()
                .username(username)
                .confirmationCode(confirmationCode)
                .clientId(clientCredentials.clientId)
                .secretHash(hash)
                .build();

        ConfirmSignUpResponse response = client.confirmSignUp(request);

        // get and return result
        JsonObject result = new JsonObject();
        result.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        result.addProperty("statusCode", response.sdkHttpResponse().statusCode());

        return result;
    }
}
