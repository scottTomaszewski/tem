package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class Lvl2_InputStreamsToOutputStreams {
  @Test
  public void streamsAreStateful() throws Exception {
    InputStream in = Res.stream();
    in.read();
    in.read();
    Processor.run(in, System.out); // prints "oo><bar/></foo>"
  }

  @Test
  public void whatYouUsuallySeeForIOHandling() throws Exception {
    InputStream in = null;
    try {
      in = Res.stream();
      process(in, System.out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void properIOHandling() throws CustomFail, IOException {
    InputStream in = Res.stream();
    CustomFail fail = null;
    try {
      process(in, System.out);
    } catch (CustomFail e) {
      fail = e;
    } finally {
      try {
        in.close();
      } catch (IOException e) {
        if (fail == null) {
          throw e;
        } else {
          fail.addSuppressed(e);
        }
      }
    }
    if (fail != null) {
      throw fail;
    }
  }

  /**
   * @throws CustomFail when marking is unsupported
   */
  private void process(InputStream from, OutputStream to)
      throws CustomFail, IOException {
    if (!from.markSupported()) {
      throw new CustomFail();
    }
    Processor.run(from, to);
  }

  @SuppressWarnings("serial")
  private static class CustomFail extends Exception {}
}
