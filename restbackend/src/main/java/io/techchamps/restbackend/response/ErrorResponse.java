package io.techchamps.restbackend.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response model")
public class ErrorResponse {

    @Schema(description = "Error message", required = true)
    private String message;
    @Schema(description = "HTTP status code", required = true)
    private int status;
    @Schema(description = "Error code", required = true)
    private String error;

    public ErrorResponse(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}

