package com.example.todoapp.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error payload for API responses.
 */
public class ApiErrorResponse {

    private final Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
