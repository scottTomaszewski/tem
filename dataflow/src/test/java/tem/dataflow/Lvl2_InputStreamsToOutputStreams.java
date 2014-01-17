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

  /**
   * https://stackoverflow.com/questions/7860137/what-is-the-java-7-try-with-resources-bytecode-equivalent-using-try-catch-finall
   */
  @Test
  public void properIOHandling() throws CustomFail, IOException {
    try {
      final InputStream in = Res.stream();
      Throwable ioEx = null;
      try {
        process(in, System.out);
      } catch (Throwable t) {
        ioEx = t;
        throw t;
      } finally {
        if (in != null) {
          if (ioEx != null) {
            try {
              in.close();
            } catch (Throwable t) {
              ioEx.addSuppressed(t);
            }
          } else {
            in.close();
          }
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * @throws CustomFail when marking is unsupported
   */
  private void process(InputStream from, OutputStream to) throws CustomFail,
      IOException {
    if (!from.markSupported()) {
      throw new CustomFail();
    }
    Processor.run(from, to);
  }

  @SuppressWarnings("serial")
  private static class CustomFail extends Exception {}
}
