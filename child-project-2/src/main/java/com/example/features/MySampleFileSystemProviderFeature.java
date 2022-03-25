package com.example.features;

import com.oracle.svm.core.annotate.AutomaticFeature;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;

@AutomaticFeature
final class MySampleFileSystemProviderFeature implements Feature {
  private static final String clazz = "com.example.MySampleFileSystemProvider";

  @Override
  public void beforeAnalysis(BeforeAnalysisAccess access) {
    // For MySampleFileSystemProvider, this feature does not tell native-image to initialize the
    // class at build time. Rather, this feature tells the analysis phase ("[2/7] Performing
    // analysis... ") not to complain when the class is already initialized. It's because
    // MySampleFileSystemProvider is already initialized before the initializing phase ("[1/7]
    // Initializing... "), in which native-image reads user-provided feature(s).
    RuntimeClassInitialization.initializeAtBuildTime(clazz);

    // On the other hand, this tells the native-image to initialize com.example.FooClass at build
    // time, because when native-image reads this feature, FooClass has not been initialized.
    RuntimeClassInitialization.initializeAtBuildTime("com.example.FooClass");
  }
}
