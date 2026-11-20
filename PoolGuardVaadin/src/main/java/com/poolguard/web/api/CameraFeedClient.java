package com.poolguard.web.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Component
public final class CameraFeedClient {
    private final String backendUrl;
    private final String token;
    private final String cameraId;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public CameraFeedClient(@Value("${poolguard.backend.url:}") String backendUrl,
                            @Value("${poolguard.backend.token:}") String token,
                            @Value("${poolguard.camera.id:phone-camera}") String cameraId) {
        this.backendUrl = backendUrl;
        this.token = token;
        this.cameraId = cameraId;
    }

    public FrameResult latestFrame() {
        if (backendUrl.isBlank() || token.isBlank()) {
            return new FrameResult(null, "Set POOLGUARD_BACKEND_URL and POOLGUARD_INGEST_TOKEN on the web service.");
        }
        try {
            URI baseUri = URI.create(backendUrl.replaceAll("/+$", ""));
            if (!"https".equalsIgnoreCase(baseUri.getScheme())
                    && !("http".equalsIgnoreCase(baseUri.getScheme())
                        && ("localhost".equalsIgnoreCase(baseUri.getHost())
                            || "127.0.0.1".equals(baseUri.getHost())
                            || "::1".equals(baseUri.getHost())))) {
                return new FrameResult(null, "POOLGUARD_BACKEND_URL must use HTTPS.");
            }
            if (!cameraId.matches("[A-Za-z0-9._-]{1,80}")) {
                return new FrameResult(null, "POOLGUARD_CAMERA_ID has an invalid value.");
            }
            URI endpoint = baseUri
                    .resolve("/api/cameras/" + cameraId + "/frames/latest.jpg");
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(Duration.ofSeconds(4))
                    .header("Authorization", "Bearer " + token)
                    .header("Accept", "image/jpeg")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 404) {
                return new FrameResult(null, "Waiting for the local camera client to send its first frame.");
            }
            if (response.statusCode() == 401) {
                return new FrameResult(null, "The web and API services have different ingest tokens.");
            }
            if (response.statusCode() / 100 != 2) {
                return new FrameResult(null, "Camera API returned HTTP " + response.statusCode() + ".");
            }
            String online = response.headers().firstValue("X-Camera-Online").orElse("false");
            String freshness = Boolean.parseBoolean(online) ? "Live camera frame" : "Last frame is stale; check the local client.";
            return new FrameResult(response.body(), freshness);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return new FrameResult(null, "Camera API request was interrupted.");
        } catch (Exception exception) {
            return new FrameResult(null, "Camera API is unavailable. Check the Render service URL.");
        }
    }

    public record FrameResult(byte[] jpeg, String status) {
        public String dataUri() {
            return jpeg == null ? "" : "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(jpeg);
        }
    }
}
