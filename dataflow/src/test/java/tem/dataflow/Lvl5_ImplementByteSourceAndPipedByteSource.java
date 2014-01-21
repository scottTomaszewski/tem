package tem.dataflow;

import static tem.dataflow.Res.gb1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import koka.util.io.guava.PipedFromOutput;

import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public final class Lvl5_ImplementByteSourceAndPipedByteSource {
  /**
   * <p>
   * Wraps the operation you want to perform in the openStream() method of your
   * own ByteSource.
   * </p>
   * <p>
   * Uses a class, DuplicatedInput that extends the ByteSource class. In the
   * openStream method, the class will perform the desired operation. In the
   * case below, the openStream method will duplicate the provided input AS the
   * stream flows through it.
   * </p>
   * <p>
   * On T3500 Machine, 6.495 seconds for 1gb payload full streaming
   * </p>
   * <p>
   * Note: the input (and therefore the operation) isnt executed until a read()
   * is called on the input. Up until that read, the class is just wired to
   * perform its operations, but doesnt actually run it. The getInput is lazy.
   * </p>
   */
  @Test
  public void DIYByteSource() throws Exception {
    DuplicatedInput from = new DuplicatedInput(Res.randomBytesOfLength(gb1));
    Processor.run(from, ByteStreams.nullOutputStream());
  }

  private static class DuplicatedInput extends ByteSource {
    private final ByteSource toDuplicate;

    public DuplicatedInput(ByteSource toDuplicate) {
      this.toDuplicate = toDuplicate;
    }

    @Override
    public InputStream openStream() throws IOException {
      return ByteSource.concat(toDuplicate, toDuplicate).openStream();
    }
  }

  /**
   * Although you may implement logic inside the openStream method, that logic
   * doesn't get executed until the read is performed because ByteSources are
   * lazy.
   * <p>
   * To demonstrate, this method calls openStream, but the exception is not
   * thrown before printing "Still running." as you may have expected.
   * </p>
   * Output:
   * 
   * <pre>
   *   Still running.
   * </pre>
   */
  @Test
  public void byteSourcesAreLazy() throws IOException {
    InputStream unread = new Lazy().openStream();
    System.out.println("Still running.");
    ByteStreams.copy(unread, ByteStreams.nullOutputStream());
    System.out.println("never printed.");
  }

  private static final class Lazy extends ByteSource {
    @Override
    public InputStream openStream() {
      return new BlowsUpOnRead();
    }
  }

  private static final class BlowsUpOnRead extends InputStream {
    @Override
    public int read() throws IOException {
      throw new IOException();
    }
  }

  /**
   * Another demonstration of how lazy ByteSources are. Output:
   * 
   * <pre>
   *   13 ms
   *   6.485 s
   * </pre>
   */
  @Test
  public void byteSourceAreLazy2() throws Exception {
    Stopwatch t = Stopwatch.createStarted();
    InputStream hasntRunYet =
        new DuplicatedInput(Res.randomBytesOfLength(gb1)).openStream();
    System.out.println(t.elapsed(TimeUnit.MILLISECONDS) + " ms");
    Processor.run(hasntRunYet, ByteStreams.nullOutputStream());
    System.out.println(t.stop());
  }

  /**
   * In many cases, you need to work with a third party library uses an
   * OutputStream in their public API. In cases like these, you can't wrap an
   * InputSupplier because you need that OutputStream.
   * <p>
   * On T3500 Machine, 7.499 seconds for 1gb payload full streaming in
   * PipedFromOutput
   * </p>
   * <p>
   * Note: Be aware that PipedFromOutput starts up a second thread!
   * </p>
   * <p>
   * Note: This is using PipedFromOutput from util-io-guava v0.7-81
   * </p>
   * <p>
   * Note: Just like InputSuppliers, PipedFromOutput is lazy.
   * </p>
   */
  @Test
  public void pipedFromOutputToTheRescue() throws IOException {
    DuplicatedPiped from = new DuplicatedPiped(Res.supplier(gb1));
    Processor.run(from, ByteStreams.nullOutputStream());
  }

  private static final class DuplicatedPiped extends PipedFromOutput {
    private final InputSupplier<? extends InputStream> toDuplicate;

    public DuplicatedPiped(InputSupplier<? extends InputStream> toDuplicate) {
      this.toDuplicate = toDuplicate;
    }

    @Override
    protected void write(OutputStream to) throws IOException {
      Processor.duplicateInput(toDuplicate, to);
    }
  }

  /**
   * On T3500 Machine, 7.213 seconds for 1gb payload full streaming inlined
   * PipedFromOutput
   * <p>
   * You can inline the PipedFromOutput class to reduce boilerplate code
   * </p>
   */
  @Test
  public void extraCredit_inlinePipedFromOutput() throws IOException {
    final InputSupplier<? extends InputStream> from = Res.supplier(gb1);
    InputSupplier<InputStream> wrapped = new PipedFromOutput() {
      @Override
      protected void write(OutputStream to) throws IOException {
        Processor.duplicateInput(from, to);
      }
    };
    Processor.run(wrapped, ByteStreams.nullOutputStream());
  }
}
