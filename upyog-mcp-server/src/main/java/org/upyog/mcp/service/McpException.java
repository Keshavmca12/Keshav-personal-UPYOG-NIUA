package org.upyog.mcp.service;

/**
 * Business error returned to the assistant. The message is safe to show.
 * Stack traces, downstream bodies, and internal URLs stay on the server.
 */
public class McpException extends RuntimeException {

    private final String code;
    private final boolean retryable;
    private final String suggestedNextStep;

    public McpException(String code, String message, boolean retryable, String suggestedNextStep) {
        super(message);
        this.code = code;
        this.retryable = retryable;
        this.suggestedNextStep = suggestedNextStep;
    }

    public String getCode() {
        return code;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public String getSuggestedNextStep() {
        return suggestedNextStep;
    }
}
