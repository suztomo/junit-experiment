package com.example;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.testing.RemoteStorageHelper;
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
}