package org.upyog.mcp.guard;

import java.time.Duration;
import java.util.Optional;

/**
 * Shared store for one-time confirmation ids and idempotent write results.
 * Production uses Redis. A process-local map is not a valid implementation of this contract.
 */
public interface SingleUseStore {

    boolean consumeOnce(String key, Duration ttl);

    void saveResult(String key, String json, Duration ttl);

    Optional<String> findResult(String key);

    boolean writesEnabled();
}
