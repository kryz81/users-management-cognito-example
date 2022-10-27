package dev.kryz.services;

import com.google.gson.JsonObject;
import dev.kryz.models.ClientCredentials;
import dev.kryz.utils.HashGenerator;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

import java.util.HashMap;
import java.util.Map;

public class LoginUserService {
    private final CognitoIdentityProviderClient client;

    public LoginUserService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public LoginUserService(CognitoIdentityProviderClient client) {
        this.client = client;
    }

    public JsonObject login(JsonObject loginData, ClientCredentials clientCredentials) {
        // get login data from request
        String email = loginData.get("email").getAsString();
        String password = loginData.get("password").getAsString();


        // create secret hash
        String hash = HashGenerator.generateHash(clientCredentials, email);

        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);
        authParams.put("SECRET_HASH", hash);

        InitiateAuthRequest request = InitiateAuthRequest
                .builder()
                .clientId(clientCredentials.clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        InitiateAuthResponse response = client.initiateAuth(request);
        AuthenticationResultType resultType = response.authenticationResult();

        JsonObject result = new JsonObject();
        result.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        result.addProperty("statusCode", response.sdkHttpResponse().statusCode());
        result.addProperty("idToken", resultType.idToken());
        result.addProperty("accessToken", resultType.accessToken());
        result.addProperty("refreshToken", resultType.refreshToken());

        return result;
    }
}
