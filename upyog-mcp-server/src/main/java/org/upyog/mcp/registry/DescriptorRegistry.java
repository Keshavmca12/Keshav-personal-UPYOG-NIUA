package org.upyog.mcp.registry;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory catalog of descriptors loaded at startup. The assistant cannot change this registry.
 */
@Component
public class DescriptorRegistry {

    private final Map<String, ServiceDescriptor> services = new LinkedHashMap<>();

    public void replace(Collection<ServiceDescriptor> loaded) {
        services.clear();
        for (ServiceDescriptor service : loaded) {
            services.put(service.getId(), service);
        }
    }

    public Collection<ServiceDescriptor> all() {
        return services.values();
    }

    public Optional<ServiceDescriptor> find(String id) {
        return Optional.ofNullable(services.get(id));
    }

    public ServiceDescriptor require(String id) {
        ServiceDescriptor service = services.get(id);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service: " + id);
        }
        return service;
    }
}
