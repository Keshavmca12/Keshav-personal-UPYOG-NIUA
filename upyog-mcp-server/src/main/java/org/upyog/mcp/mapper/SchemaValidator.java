package org.upyog.mcp.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates a tool payload against the descriptor JSON Schema.
 * Schemas set {@code additionalProperties: false}, so unknown fields fail here.
 */
@Component
public class SchemaValidator {

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

    public SchemaValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param schema  descriptor {@code inputSchema}
     * @param payload MCP tool arguments after forbidden-field stripping
     */
    public void validate(JsonNode schema, JsonNode payload) {
        JsonSchema compiled = factory.getSchema(schema);
        Set<ValidationMessage> messages = compiled.validate(payload);
        if (!messages.isEmpty()) {
            String text = messages.stream().map(ValidationMessage::getMessage).collect(Collectors.joining("; "));
            throw new IllegalArgumentException(text);
        }
    }

    /** Deep-copies an object node or returns an empty object when the input is not an object. */
    public ObjectNode copyObject(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            return objectNode.deepCopy();
        }
        return objectMapper.createObjectNode();
    }
}
