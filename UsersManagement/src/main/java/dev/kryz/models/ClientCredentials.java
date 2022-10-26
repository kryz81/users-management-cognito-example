package dev.kryz.models;

public class ClientCredentials {
    public String clientId;
    public String clientSecret;

    public ClientCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
