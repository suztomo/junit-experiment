package com.example.features;

import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

@AutomaticFeature
final class MySampleFileSystemProviderFeature implements Feature {
  private static final String clazz = "com.example.MySampleFileSystemProvider";

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    RuntimeClassInitialization.initializeAtBuildTime(clazz);
  }
}
