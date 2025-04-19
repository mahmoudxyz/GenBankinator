package xyz.mahmoudahmed.feature;

/**
 * Default handler for unknown features
 */
public class DefaultFeatureHandler extends AbstractFeatureHandler {
    @Override
    public String getFeatureType() {
        return "misc_feature";
    }

    @Override
    public boolean canHandle(String featureName) {
        // This is the fallback handler - it handles anything
        return true;
    }
}