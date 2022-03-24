# junit-experiment
## child-project-2 : Testing with FileSystemProvider
To run the application, we call `mvn test -P native`.
To reproduce the issue, delete the contents of the `main/resources/META-INF/native-image/native-image.properties` file.

We will see the following error at image build time:
```
Error: Classes that should be initialized at run time got initialized during image building:
 com.example.MySampleFileSystemProvider was unintentionally initialized at build time. To see why com.example.MySampleFileSystemProvider got initialized use --trace-class-initialization=com.example.MySampleFileSystemProvider
```
Now, if we add --initialize-at-run-time=com.example.MySampleFileSystemProvider, we will see the following error:

```
Error: The class com.example.MySampleFileSystemProvider has already been initialized (from file:///usr/local/google/home/mpeddada/IdeaProjects/native-image-experiments/junit-experiment/child-project-2/target/classes/META-INF/native-image/native-image.properties with 'com.example.MySampleFileSystemProvider'); it is too late to register com.example.MySampleFileSystemProvider for build-time initialization. To see why com.example.MySampleFileSystemProvider got initialized use --trace-class-initialization=com.example.MySampleFileSystemProvider
```

Including `--initialize-build-time=com.example.MySampleFileSystemProvider` results in a **successful build**.


## child-project-3 : Testing with Category tag
Experiment with Junit annotations and tags for native image testing.

To run the application, we call `mvn test -P native`.

When the `Category` tag is used, it results in the following error:

```
org.junit.platform.engine.TestTag was unintentionally initialized at build time. org.junit.platform.engine.TestTag caused initialization of this class with the following trace: 
	at org.junit.platform.engine.TestTag.<clinit>(TestTag.java:54)
  at org.junit.platform.engine.TestTag.<clinit>(TestTag.java:54)
	at org.junit.vintage.engine.descriptor.VintageTestDescriptor$$Lambda$656/0x00000007c23d8440.apply(Unknown Source)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
  ...
at org.junit.platform.engine.TestDescriptor.lambda$accept$0(TestDescriptor.java:251)
	at org.junit.platform.engine.TestDescriptor$$Lambda$639/0x00000007c23d4040.accept(Unknown Source)
	at java.lang.Iterable.forEach(Iterable.java:75)
	at org.junit.platform.engine.TestDescriptor.accept(TestDescriptor.java:251)
	at org.junit.platform.launcher.TestPlan.lambda$from$1(TestPlan.java:95)
	at org.junit.platform.launcher.TestPlan$$Lambda$649/0x00000007c23d6840.accept(Unknown Source)
	at java.util.LinkedHashMap$LinkedValues.forEach(LinkedHashMap.java:608)
	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1085)
	at org.junit.platform.launcher.TestPlan.from(TestPlan.java:95)
	at org.junit.platform.launcher.core.InternalTestPlan.from(InternalTestPlan.java:32)
	at org.junit.platform.launcher.core.DefaultLauncher.discover(DefaultLauncher.java:78)
	at org.junit.platform.launcher.core.DefaultLauncherSession$DelegatingLauncher.discover(DefaultLauncherSession.java:81)
	at org.junit.platform.launcher.core.SessionPerRequestLauncher.discover(SessionPerRequestLauncher.java:46)
	at org.graalvm.junit.platform.JUnitPlatformFeature.discoverTestsAndRegisterTestClassesForReflection(JUnitPlatformFeature.java:135)
```
The failure seems to happening when `VintageTestDescriptor` calls the `TestTag` object which is not recognized by the native image builder. 

This error is resolved when the `--initialize-at-build-time=org.junit.platform.engine.TestTag` option is used. 

