package tem.dataflow;

import static tem.dataflow.Processor.duplicateInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Closer;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public final class Lvl3_ByteSourceToByteSink {
  /**
   * ByteSources are not stateful like InputStreams so we can read multiple
   * times.
   */
  @Test
  public void notStateful() throws IOException {
    ByteSource from = Res.byteSource();
    System.out.println("First:");
    Processor.run(from, System.out);
    System.out.println("\n\nSecond:");
    Processor.run(from, System.out);
  }

  /**
   * Shorter and less confusing IO handling
   */
  @Test
  public void betterIOHandling() throws IOException {
    ByteSource from = Res.byteSource();
    Closer c = Closer.create();
    try {
      InputStream in = c.register(from.openStream());
      Processor.run(in, System.out);
    } catch (Throwable e) {
      throw c.rethrow(e);
    } finally {
      c.close();
    }
  }

  /**
   * For multiple operations, you will need to store your data
   * <p>
   * Could have used temp.deleteOnExit();
   * </p>
   * <p>
   * Wont delete until jvm terminates. What if youre running in a webserver...
   * also has potential for memory leak
   * </p>
   */
  @Test
  public void needToStoreBetweenOperations() throws IOException {
    ByteSource from = Res.byteSource();
    File temp = File.createTempFile("partOne", ".xml");
    try {
      duplicateInput(from, Files.asByteSink(temp, FileWriteMode.APPEND));
      Processor.run(Files.asByteSource(temp), System.out);
    } finally {
      // dont expect this to run promptly (Effective Java - Item 7)
      temp.delete();
    }
  }
}
