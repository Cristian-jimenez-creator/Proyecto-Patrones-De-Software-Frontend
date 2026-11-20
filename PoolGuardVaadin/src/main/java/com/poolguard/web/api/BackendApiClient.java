package com.poolguard.web.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

public final class BackendApiClient {
    private final URI baseUri;
    private final HttpClient client = HttpClient.newHttpClient();
    public BackendApiClient(URI baseUri) { this.baseUri = baseUri; }
    public ApiResponse get(String path, String accessToken) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(path)).timeout(Duration.ofSeconds(10)).header("X-Correlation-ID", UUID.randomUUID().toString()).GET();
            if (accessToken != null && !accessToken.isBlank()) builder.header("Authorization", "Bearer " + accessToken);
            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(response.statusCode(), response.body());
        } catch (Exception exception) { return new ApiResponse(503, ""); }
    }
    public record ApiResponse(int status, String body) { }
}
