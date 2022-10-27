package dev.kryz.services;

import com.google.gson.JsonObject;
import dev.kryz.models.ClientCredentials;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    CognitoIdentityProviderClient client;

    @InjectMocks
    private CreateUserService service;

    @Test
    void testCorrectRequestToCognito() {
        // given
        JsonObject userPayload = new JsonObject();
        userPayload.addProperty("username", "test@test.com");
        userPayload.addProperty("password", "pass");
        userPayload.addProperty("age", 44);

        ClientCredentials clientCredentials = new ClientCredentials("client-id-1", "client-secret-2");

        SignUpResponse responseMock = mock(SignUpResponse.class);
        when(responseMock.sdkHttpResponse()).thenReturn(mock(SdkHttpFullResponse.class));

        when(client.signUp(any(SignUpRequest.class))).thenReturn(responseMock);

        // when
        service.createUser(userPayload, clientCredentials);

        // then
        final ArgumentCaptor<SignUpRequest> captor = ArgumentCaptor.forClass(SignUpRequest.class);
        verify(client).signUp(captor.capture());
        Assertions.assertEquals("test@test.com", captor.getValue().username());
        Assertions.assertEquals("pass", captor.getValue().password());
    }

    @Test
    void testCorrectSignUpResult() {
    }
}