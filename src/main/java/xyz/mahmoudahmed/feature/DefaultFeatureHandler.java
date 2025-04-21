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
        return true;
    }
}