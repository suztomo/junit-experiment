package com.example;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
import com.google.common.truth.Correspondence;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.ServiceLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleTest {

  private static StorageOptions storageOptions;

  @Before
  public void setUp(){
    RemoteStorageHelper gcsHelper = RemoteStorageHelper.create();
    storageOptions = gcsHelper.getOptions();
  }

  @Test
  public void testMessage(){
    MySampleFileSystemProvider mySampleFileSystemProvider = new MySampleFileSystemProvider();
    mySampleFileSystemProvider.doInitStorage();
    CloudStorageConfiguration config =
        CloudStorageConfiguration.builder()
            .autoDetectRequesterPays(true)
            .build();
    RemoteStorageHelper gcsHelper = RemoteStorageHelper.create();
    MySampleFileSystem.forBucket("bucket",config, gcsHelper.getOptions());
    assertThat("hello1").isEqualTo("hello1");
  }

  @Test
  public void testServiceLoader() {
    ServiceLoader<FileSystemProvider> fileSystemProviders =
        ServiceLoader.load(FileSystemProvider.class);
    for (FileSystemProvider fileSystemProvider : fileSystemProviders) {
      System.out.println("FileSystemProvider: " + fileSystemProvider.getClass().getName());
    }
    assertThat(fileSystemProviders)
        .comparingElementsUsing(
            Correspondence.transforming(
                provider -> provider.getClass().getName(), "with its name == "))
        .contains("com.example.MySampleFileSystemProvider");
    assertThat(fileSystemProviders)
        .comparingElementsUsing(
            Correspondence.transforming(
                (FileSystemProvider provider) -> provider.getScheme(), "with its scheme equals "))
        .contains("gs");
  }
}