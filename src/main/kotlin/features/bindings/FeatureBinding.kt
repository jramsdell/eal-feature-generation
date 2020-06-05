package features.bindings
import features.containers.FeatureContainer

open class FeatureBinding(val binding: (FeatureContainer) -> List<Double>)
