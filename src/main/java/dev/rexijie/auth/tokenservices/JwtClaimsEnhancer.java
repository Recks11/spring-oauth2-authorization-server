package dev.rexijie.auth.tokenservices;

import java.util.Map;

/**
 * This class takes a map of claims in a jwt and returns a new map
 * with updated claims.
 */

public interface JwtClaimsEnhancer {
    Map<String, Object> enhance(Map<String, Object> originalClaims);
}
