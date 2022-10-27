package dev.kryz.services;

import com.google.gson.JsonObject;
import dev.kryz.models.ClientCredentials;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;

public class AddUserToGroupService {
    private final CognitoIdentityProviderClient client;

    public AddUserToGroupService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public AddUserToGroupService(CognitoIdentityProviderClient client) {
        this.client = client;
    }

    public JsonObject add(JsonObject data, ClientCredentials clientCredentials, String userPoolId) {
        String email = data.get("email").getAsString();
        String groupName = data.get("groupName").getAsString();

        AdminAddUserToGroupRequest request = AdminAddUserToGroupRequest
                .builder()
                .groupName(groupName)
                .username(email)
                .userPoolId(userPoolId)
                .build();

        AdminAddUserToGroupResponse response = client.adminAddUserToGroup(request);

        JsonObject result = new JsonObject();
        result.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        result.addProperty("statusCode", response.sdkHttpResponse().statusCode());

        return result;
    }
}

