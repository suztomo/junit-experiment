package com.example;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import com.google.cloud.storage.StorageOptions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class MySampleFileSystem extends FileSystem {

  static {
    System.out.println("******MY SAMPLE FILE SYSTEM******");
  }

  public static final String URI_SCHEME = "gs";
  private static final LoadingCache<ProviderCacheKey, MySampleFileSystemProvider>
      PROVIDER_CACHE_BY_CONFIG =
      CacheBuilder.newBuilder().build(
          new CacheLoader<ProviderCacheKey, MySampleFileSystemProvider>() {
            @Override
            public MySampleFileSystemProvider load(ProviderCacheKey key) throws Exception {
              CloudStorageConfiguration config = key.cloudStorageConfiguration;
              StorageOptions storageOptions = key.storageOptions;
              String userProject = config.userProject();
              return new MySampleFileSystemProvider();
            }
          });
  private final MySampleFileSystemProvider provider = new MySampleFileSystemProvider();
  private static CloudStorageConfiguration userSpecifiedDefault = CloudStorageConfiguration.DEFAULT;


  static void setDefaultCloudStorageConfiguration(CloudStorageConfiguration config) {
    if (null == config) {
      userSpecifiedDefault = CloudStorageConfiguration.DEFAULT;
    } else {
      userSpecifiedDefault = config;
    }
  }

  @CheckReturnValue
  public static MySampleFileSystem forBucket(
      String bucket, CloudStorageConfiguration config, @Nullable StorageOptions storageOptions) {
    checkArgument(
        !bucket.startsWith(URI_SCHEME + ":"), "Bucket name must not have schema: %s", bucket);
    checkNotNull(config);
    MySampleFileSystemProvider result;
    ProviderCacheKey providerCacheKey = new ProviderCacheKey(config, storageOptions);
    try {
      result = PROVIDER_CACHE_BY_CONFIG.get(providerCacheKey);
    } catch (ExecutionException | UncheckedExecutionException e) {
      throw new IllegalStateException(
          "Unable to resolve CloudStorageFileSystemProvider for the provided configuration", e);
    }
    return new MySampleFileSystem();
  }

  @Override
  public FileSystemProvider provider() {
    return null;
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public String getSeparator() {
    return null;
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    return null;
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    return null;
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    return null;
  }

  @Override
  public Path getPath(String first, String... more) {
    return null;
  }

  @Override
  public PathMatcher getPathMatcher(String syntaxAndPattern) {
    return null;
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    return null;
  }

  @Override
  public WatchService newWatchService() throws IOException {
    return null;
  }

  private static final class ProviderCacheKey {

    private final CloudStorageConfiguration cloudStorageConfiguration;
    @Nullable
    private final StorageOptions storageOptions;

    public ProviderCacheKey(
        CloudStorageConfiguration cloudStorageConfiguration,
        @Nullable StorageOptions storageOptions) {
      this.cloudStorageConfiguration =
          requireNonNull(cloudStorageConfiguration, "cloudStorageConfiguration must be non null");
      this.storageOptions = storageOptions;
    }
  }

}
