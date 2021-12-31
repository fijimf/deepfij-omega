package com.fijimf.deepfijomega.ml.features;

import com.fijimf.deepfijomega.entity.schedule.Game;

public interface FeatureExtractor<K> {
    K getFeature(Game g);
}
