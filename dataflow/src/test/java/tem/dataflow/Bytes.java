package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

/**
 * <p>
 * Assists in generating arbitrary random data in a useful format for tests in
 * an entirely streaming way.
 * </p>
 * <p>
 * Currently this class is unfinished and I caution its use in production code.
 * One day this class may move to koka.util, but its performance and use needs to
 * be assessed before finalizing this API
 * </p>
 */
@Beta
public abstract class Bytes implements InputSupplier<InputStream> {
  public static Unsized random() {
    return random(8L);
  }

  public static Unsized random(final long seed) {
    class EndlessRandom extends Bytes implements Unsized {
      @Override
      public Bytes endless() {
        return this;
      }

      @Override
      public InputStream getInput() {
        return new InputStream() {
          private final Random byteGenerator = new Random(seed);

          @Override
          public int read(byte[] b, int off, int len) {
            byte[] tmp = new byte[len - off];
            byteGenerator.nextBytes(tmp);
            System.arraycopy(tmp, 0, b, off, len);
            return tmp.length;
          }

          @Override
          public int read(byte[] b) {
            byteGenerator.nextBytes(b);
            return b.length;
          }

          @Override
          public int read() {
            return byteGenerator.nextInt(0xFF + 1);
          }
        };
      }
    }
    return new EndlessRandom();
  }

  public static Bytes of(final InputSupplier<? extends InputStream> raw) {
    return new Bytes() {
      @Override
      public InputStream getInput() throws IOException {
        return raw.getInput();
      }
    };
  }

  public final Bytes limitedTo(long length) {
    return of(ByteStreams.slice(this, 0, length));
  }

  /**
   * Not sure how long this will remain. Turns out caching to disk is slower
   * than regenerating random data so this method will likely be removed
   */
  @Beta
  final Bytes cacheToDisk() throws IOException {
    FileBackedOutputStream cache = new FileBackedOutputStream(1_000_000);
    ByteStreams.copy(this, cache);
    return Bytes.of(cache.getSupplier());
  }

  public final void copyTo(OutputSupplier<? extends OutputStream> to)
      throws IOException {
    try (OutputStream out = to.getOutput()) {
      copyTo(out);
    }
  }

  public void copyTo(OutputStream to) throws IOException {
    ByteStreams.copy(this, to);
  }

  private Bytes() {}

  public interface Unsized {
    Bytes limitedTo(long length);

    Bytes endless();
  }
}
