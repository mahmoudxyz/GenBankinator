package xyz.mahmoudahmed.feature;


import java.util.ArrayList;
import java.util.List;

/**
 * Registry for all feature handlers
 * Follows the Registry Pattern
 */
public class FeatureHandlerRegistry {
    private static final List<FeatureHandler> handlers = new ArrayList<>();
    private static boolean initialized = false;

    // Register all built-in handlers
    public static synchronized void initialize() {
        if (initialized) return;

        // Register built-in handlers
        registerHandler(new TrnaFeatureHandler());
        registerHandler(new RrnaFeatureHandler());
        registerHandler(new ProteinCodingFeatureHandler());
        registerHandler(new OriginFeatureHandler());
        registerHandler(new DefaultFeatureHandler());

        initialized = true;
    }

    // Allow registering custom handlers
    public static void registerHandler(FeatureHandler handler) {
        handlers.add(handler);
    }

    // Get appropriate handler for a feature
    public static FeatureHandler getHandlerFor(String featureName) {
        initialize();
        return handlers.stream()
                .filter(h -> h.canHandle(featureName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No handler found for feature: " + featureName));
    }
}
