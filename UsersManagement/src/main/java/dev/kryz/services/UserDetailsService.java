package dev.kryz.services;

import com.google.gson.JsonObject;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;

public class UserDetailsService {

    private final CognitoIdentityProviderClient client;

    public UserDetailsService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public UserDetailsService(CognitoIdentityProviderClient client) {
        this.client = client;
    }

    public JsonObject getDetailsByUsername(String username, String userPoolId) {
        AdminGetUserRequest request = AdminGetUserRequest
                .builder()
                .username(username)
                .userPoolId(userPoolId)
                .build();

        AdminGetUserResponse response = client.adminGetUser(request);

        if (!response.sdkHttpResponse().isSuccessful()) {
            throw new IllegalArgumentException("Cannot get user");
        }

        JsonObject result = new JsonObject();
        response.userAttributes().forEach(item -> {
            result.addProperty(item.name(), item.value());
        });

        return result;
    }
}
