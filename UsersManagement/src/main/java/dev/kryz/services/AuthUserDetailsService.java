package dev.kryz.services;

import com.google.gson.JsonObject;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import java.util.List;

public class AuthUserDetailsService {

    private final CognitoIdentityProviderClient client;

    public AuthUserDetailsService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public AuthUserDetailsService(CognitoIdentityProviderClient client) {
        this.client = client;
    }

    public JsonObject getDetails(String accessToken) {
        GetUserRequest request = GetUserRequest
                .builder()
                .accessToken(accessToken)
                .build();

        GetUserResponse response = client.getUser(request);

        JsonObject result = new JsonObject();
        result.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        result.addProperty("statusCode", response.sdkHttpResponse().statusCode());
        response.userAttributes().forEach(item -> {
            result.addProperty(item.name(), item.value());
        });

        return result;
    }
}
