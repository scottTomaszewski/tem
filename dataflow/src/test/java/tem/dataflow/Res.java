package tem.dataflow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import koka.util.io.guava.Bytes;

import com.google.common.io.ByteSource;

/**
 * Resources for use in the tem. Generates data, random or otherwise.
 */
final class Res {
  public static final int mb1 = 1024 * 1024;
  public static final long mb512 = mb1 * 512L;
  public static final long gb1 = mb1 * 1024L;

  static String string() {
    return "<foo><bar/></foo>";
  }

  static InputStream stream() {
    return new ByteArrayInputStream(string().getBytes());
  }

  static ByteSource byteSource() {
    return ByteSource.wrap(string().getBytes());
  }

  static Bytes randomBytesOfLength(long size) {
    return Bytes.random().slice(0, size);
  }

  static Bytes randomCachedBytesOfLength(long size) throws IOException {
    return randomBytesOfLength(size).cacheToDisk();
  }

  private Res() {}
}
