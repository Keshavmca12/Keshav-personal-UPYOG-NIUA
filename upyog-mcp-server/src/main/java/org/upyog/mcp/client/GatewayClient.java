package org.upyog.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * HTTP client that talks only to the UPYOG API gateway.
 * Callers pass a path from a descriptor or a fixed server constant, never a caller-supplied URL.
 */
public interface GatewayClient {

    JsonNode postJson(String gatewayPath, Map<String, List<String>> query, JsonNode body, boolean read);

    JsonNode postFile(String gatewayPath, String tenantId, String module, String fileName,
                      String contentType, byte[] content);
}
