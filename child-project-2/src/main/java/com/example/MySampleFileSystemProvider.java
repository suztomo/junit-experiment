package com.example;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.annotation.concurrent.ThreadSafe;

@Singleton
@ThreadSafe
public class MySampleFileSystemProvider extends FileSystemProvider {

  static {
    System.out.println("**** MY SAMPLE FILE SYSTEM PROVIDER ****");
  }

  private static StorageOptions storageOptions = null;
  private Storage storage;

  private static class LazyPathIterator extends AbstractIterator<Path> {

    static{
      System.out.println("LAZY PATH ");
    }

    private final Iterator<Blob> blobIterator;
    private final Filter<? super Path> filter;
    private final MySampleFileSystem fileSystem;
    private final String prefix;
    // whether to make the paths absolute before returning them.
    private final boolean absolutePaths;

    LazyPathIterator(
        MySampleFileSystem fileSystem,
        String prefix,
        Iterator<Blob> blobIterator,
        Filter<? super Path> filter,
        boolean absolutePaths) {
      this.prefix = prefix;
      this.blobIterator = blobIterator;
      this.filter = filter;
      this.fileSystem = fileSystem;
      this.absolutePaths = absolutePaths;
    }
    @Override
    public Path computeNext() {
      while (blobIterator.hasNext()) {
        Path path = fileSystem.getPath(blobIterator.next().getName());
        try {
          if (path.toString().equals(prefix)) {
            // do not return ourselves, because that confuses recursive descents.
            continue;
          }
          if (filter.accept(path)) {
            if (absolutePaths) {
              return path.toAbsolutePath();
            }
            return path;
          }
        } catch (IOException ex) {
          throw new DirectoryIteratorException(ex);
        }
      }
      return endOfData();
    }
  }

  /**
   * Sets options that are only used by the constructor.
   *
   * <p>Instead of calling this, when possible use CloudStorageFileSystem.forBucket and pass
   * StorageOptions as an argument.
   */
  @VisibleForTesting
  public static void setStorageOptions(@Nullable StorageOptions newStorageOptions) {
    storageOptions = newStorageOptions;
  }

  public static void setDefaultCloudStorageConfiguration(
      @Nullable CloudStorageConfiguration newDefault) {
    MySampleFileSystem.setDefaultCloudStorageConfiguration(newDefault);
  }


  @Override
  public String getScheme() {
    return MySampleFileSystem.URI_SCHEME;

  }

  @Override
  public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
    initStorage();
    return newFileSystem(uri, Collections.<String, Object>emptyMap());
  }

  private void initStorage() {
    if (this.storage == null) {
      doInitStorage();
    }
  }

  void doInitStorage() {
    return;
  }

  @Override
  public FileSystem getFileSystem(URI uri) {
    return null;
  }

  @Override
  public Path getPath(URI uri) {
    return null;
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
      FileAttribute<?>... attrs) throws IOException {
    return null;
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
      throws IOException {
    try {
        return new DirectoryStream<Path>() {
          @Override
          public Iterator<Path> iterator() {
            return new LazyPathIterator(
                new MySampleFileSystem(), "prefix", null, filter, dir.isAbsolute());
          }

          @Override
          public void close() throws IOException {
            // Does nothing since there's nothing to close. Commenting this method to quiet codacy.
          }
        };
      } catch (StorageException exs) {
        return null;
        // Will rethrow a StorageException if all retries/reopens are exhausted
        // we're being aggressive by retrying even on scenarios where we'd normally reopen.
      }
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

  }

  @Override
  public void delete(Path path) throws IOException {

  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {

  }

  @Override
  public boolean isSameFile(Path path, Path path2) throws IOException {
    return false;
  }

  @Override
  public boolean isHidden(Path path) throws IOException {
    return false;
  }

  @Override
  public FileStore getFileStore(Path path) throws IOException {
    return null;
  }

  @Override
  public void checkAccess(Path path, AccessMode... modes) throws IOException {

  }

  @Override
  public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type,
      LinkOption... options) {
    return null;
  }

  @Override
  public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type,
      LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options)
      throws IOException {
    return null;
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options)
      throws IOException {

  }
}
