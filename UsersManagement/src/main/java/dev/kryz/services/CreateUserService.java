package dev.kryz.services;

import com.google.gson.JsonObject;
import dev.kryz.models.ClientCredentials;
import dev.kryz.utils.HashGenerator;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateUserService {

    private final CognitoIdentityProviderClient client;

    public CreateUserService() {
        this.client = CognitoIdentityProviderClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .build();
    }

    public JsonObject createUser(JsonObject userData, ClientCredentials clientCredentials) {
        // get date from payload
        String username = userData.get("username").getAsString();
        String password = userData.get("password").getAsString();
        Integer age = userData.get("age").getAsInt();
        String userId = UUID.randomUUID().toString();

        // create attributes to store in cognito
        AttributeType emailAttr = AttributeType.builder().name("email").value(username).build();
        AttributeType userIdAttr = AttributeType.builder().name("custom:userId").value(userId).build();
        AttributeType ageAttr = AttributeType.builder().name("custom:age").value(userId).build();

        // create object of custom attributes
        List<AttributeType> customAttributes = new ArrayList<>();
        customAttributes.add(userIdAttr);
        customAttributes.add(emailAttr);

        // calculate secret hash
        String hash = HashGenerator.generateHash(clientCredentials, username);

        // create request to cognito
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(username)
                .password(password)
                .userAttributes(customAttributes)
                .clientId(clientCredentials.clientId)
                .secretHash(hash)
                .build();

        // send request
        SignUpResponse response = client.signUp(signUpRequest);

        JsonObject result = new JsonObject();
        result.addProperty("isSuccessful", response.sdkHttpResponse().isSuccessful());
        result.addProperty("statusCode", response.sdkHttpResponse().statusCode());
        result.addProperty("cognitoUserId", response.userSub());
        result.addProperty("isConfirmed", response.userConfirmed());

        return result;
    }
}
