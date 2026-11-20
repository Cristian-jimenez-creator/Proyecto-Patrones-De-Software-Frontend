package com.poolguard.web.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Component
public final class CameraFeedClient {
    private static final Duration SUCCESS_CACHE = Duration.ofSeconds(3);
    private static final Duration DEFAULT_RATE_LIMIT_BACKOFF = Duration.ofSeconds(15);
    private final String backendUrl;
    private final String token;
    private final String cameraId;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private FrameResult cachedResult;
    private Instant cacheExpiresAt = Instant.MIN;

    public CameraFeedClient(@Value("${poolguard.backend.url:}") String backendUrl,
                            @Value("${poolguard.backend.token:}") String token,
                            @Value("${poolguard.camera.id:phone-camera}") String cameraId) {
        this.backendUrl = backendUrl;
        this.token = token;
        this.cameraId = cameraId;
    }

    public synchronized FrameResult latestFrame() {
        Instant now = Instant.now();
        if (cachedResult != null && now.isBefore(cacheExpiresAt)) {
            return cachedResult;
        }
        return fetchLatestFrame();
    }

    private FrameResult fetchLatestFrame() {
        if (backendUrl.isBlank() || token.isBlank()) {
            return cache(new FrameResult(null, "Set POOLGUARD_BACKEND_URL and POOLGUARD_INGEST_TOKEN on the web service."), Duration.ofSeconds(10));
        }
        try {
            URI baseUri = URI.create(backendUrl.replaceAll("/+$", ""));
            if (!"https".equalsIgnoreCase(baseUri.getScheme())
                    && !("http".equalsIgnoreCase(baseUri.getScheme())
                        && ("localhost".equalsIgnoreCase(baseUri.getHost())
                            || "127.0.0.1".equals(baseUri.getHost())
                            || "::1".equals(baseUri.getHost())))) {
                return cache(new FrameResult(null, "POOLGUARD_BACKEND_URL must use HTTPS."), Duration.ofSeconds(10));
            }
            if (!cameraId.matches("[A-Za-z0-9._-]{1,80}")) {
                return cache(new FrameResult(null, "POOLGUARD_CAMERA_ID has an invalid value."), Duration.ofSeconds(10));
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
                return cache(new FrameResult(null, "Waiting for the local camera client to send its first frame."), Duration.ofSeconds(5));
            }
            if (response.statusCode() == 401) {
                return cache(new FrameResult(null, "The web and API services have different ingest tokens."), Duration.ofSeconds(10));
            }
            if (response.statusCode() == 429) {
                Duration backoff = response.headers().firstValue("Retry-After")
                        .map(CameraFeedClient::parseRetryAfter)
                        .orElse(DEFAULT_RATE_LIMIT_BACKOFF);
                return cache(new FrameResult(null, "Camera API is rate limiting requests. Retrying shortly."), backoff);
            }
            if (response.statusCode() / 100 != 2) {
                return cache(new FrameResult(null, "Camera API returned HTTP " + response.statusCode() + "."), Duration.ofSeconds(5));
            }
            String online = response.headers().firstValue("X-Camera-Online").orElse("false");
            String freshness = Boolean.parseBoolean(online) ? "Live camera frame" : "Last frame is stale; check the local client.";
            return cache(new FrameResult(response.body(), freshness), SUCCESS_CACHE);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return cache(new FrameResult(null, "Camera API request was interrupted."), Duration.ofSeconds(5));
        } catch (Exception exception) {
            return cache(new FrameResult(null, "Camera API is unavailable. Check the Render service URL."), Duration.ofSeconds(5));
        }
    }

    private FrameResult cache(FrameResult result, Duration ttl) {
        cachedResult = result;
        cacheExpiresAt = Instant.now().plus(ttl);
        return result;
    }

    private static Duration parseRetryAfter(String value) {
        try {
            long seconds = Long.parseLong(value.trim());
            return Duration.ofSeconds(Math.max(1, Math.min(seconds, 300)));
        } catch (NumberFormatException ignored) {
            try {
                Instant retryAt = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                Duration delay = Duration.between(Instant.now(), retryAt);
                if (delay.isNegative() || delay.isZero()) {
                    return Duration.ofSeconds(1);
                }
                return delay.compareTo(Duration.ofSeconds(300)) > 0 ? Duration.ofSeconds(300) : delay;
            } catch (Exception ignoredDate) {
                return DEFAULT_RATE_LIMIT_BACKOFF;
            }
        }
    }

    public record FrameResult(byte[] jpeg, String status) {
        public String dataUri() {
            return jpeg == null ? "" : "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(jpeg);
        }
    }
}
