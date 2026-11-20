package com.poolguard.web.api;

public final class ApiErrorMessageMapper {
    private ApiErrorMessageMapper() { }
    public static String messageFor(int status) {
        return switch (status) {
            case 400 -> "The request could not be processed.";
            case 401 -> "Your session has expired. Please sign in again.";
            case 403 -> "You do not have permission to perform this action.";
            case 404 -> "The requested resource was not found.";
            case 409 -> "The data changed on the server. Reload and try again.";
            case 422 -> "Please correct the highlighted fields.";
            case 429 -> "Too many requests. Wait a moment and try again.";
            case 500 -> "The service encountered an internal error.";
            case 502, 503, 504 -> "The camera or backend service is unavailable. Try again.";
            default -> "The service could not complete the request.";
        };
    }
}
