package tem.dataflow;

import static tem.dataflow.Processor.duplicateInput;
import static tem.dataflow.Res.gb1;
import static tem.dataflow.Res.mb1;

import java.io.IOException;

import koka.util.io.guava.InMemoryBytes;

import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

public final class Lvl4_InMemoryBytesAndFileBackedOutputStream {
  /**
   * Instead of storing to a temp file, you could store in memory for speed, but
   * has a limited input size.
   * <p>
   * On T3500 Machine, 4.596 seconds before FAILURE on 1Gb payload:
   * </p>
   * 
   * <pre>
   * java.lang.OutOfMemoryError: Requested array size exceeds VM limit
   *   at java.util.Arrays.copyOf(Arrays.java:2271)
   *   at java.io.ByteArrayOutputStream.grow(ByteArrayOutputStream.java:113)
   *   at java.io.ByteArrayOutputStream.ensureCapacity(ByteArrayOutputStream.java:93)
   *   at java.io.ByteArrayOutputStream.write(ByteArrayOutputStream.java:140)
   *   at com.google.common.io.ByteStreams.copy(ByteStreams.java:211)
   *   at com.google.common.io.ByteSource.copyTo(ByteSource.java:183)
   *   at com.google.common.io.ByteStreams.copy(ByteStreams.java:172)
   *   at tem.dataflow.Processor.duplicateInput(Processor.java:16)
   *   at tem.dataflow.Lvl4_InMemoryBytesAndFileBackedOutputStream.storingInMemoryOverflowsHeap(Lvl4_InMemoryBytesFileBackedOutputStream.java:43)
   * </pre>
   */
  @Test
  public void storingInMemoryOverflowsHeap() throws IOException {
    InMemoryBytes outputToInput = new InMemoryBytes();
    duplicateInput(Res.randomBytesOfLength(gb1), outputToInput);
    Processor.run(outputToInput.asByteSource(), ByteStreams.nullOutputStream());
  }

  /**
   * To get around the size limit, you could use FileBackedOutputStream to store
   * some in memory and some on disk.
   * <p>
   * On T3500 Machine, 22.808 seconds for 1gb payload, no caching, fbos(1Mb)
   * </p>
   * <p>
   * Sends output to nullOutputStream to avoid crashing eclipse.
   * </p>
   * <p>
   * Note: This is also generating the payload from scratch (twice,
   * duplicateInput), but this operation is better than having the payload in
   * your repo and needing to download on checkout.
   * </p>
   * <p>
   * Also has potential for crashing due to disk space.
   * </p>
   */
  @Test
  public void storingOnDiskIsSlowAndCanCrash() throws IOException {
    ByteSource from = Res.randomBytesOfLength(gb1);
    FileBackedOutputStream outputToInput = new FileBackedOutputStream(mb1);
    duplicateInput(from, outputToInput);
    Processor.run(outputToInput.asByteSource(), ByteStreams.nullOutputStream());
  }

  /**
   * This one caches the payload to disk before duplicating to show that
   * regenerating the payload is faster than reading directly from disk.
   * <p>
   * On T3500 Machine, 31.692 seconds for 1gb payload, with caching, fbos(1Mb)
   * <p>
   * <p>
   * Caching: The randomly generated input is cached to disk so that when the
   * duplicateInput() method is called, the random generation doesnt have to run
   * again.
   * </p>
   * <p>
   * Sends output to nullOutputStream to avoid crashing eclipse.
   * </p>
   * <p>
   * Note: This is also generating the payload from scratch (twice), but this
   * operation is better than having the payload in your repo and needing to
   * download on checkout.
   * </p>
   */
  @Test
  public void cachedStoringOnDiskIsSlow() throws IOException {
    ByteSource from = Res.randomCachedBytesOfLength(gb1);
    FileBackedOutputStream fbos = new FileBackedOutputStream(mb1);
    duplicateInput(from, fbos);
    Processor.run(fbos.asByteSource(), ByteStreams.nullOutputStream());
  }
}
