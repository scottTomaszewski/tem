package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class Lvl2_InputStreamsToOutputStreams {
  @Test
  public void streamsAreStateful() throws Exception {
    InputStream in = Res.stream();
    in.read();
    in.read();
    // prints "oo><bar/></foo>"
    Processor.run(in, System.out); 
  }

  @Test
  public void whatYouUsuallySeeForIOHandling() throws Exception {
    InputStream in = null;
    try {
      in = Res.stream();
      Processor.run(in, System.out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * https://stackoverflow.com/questions/7860137/what-is-the-java-7-try-with-resources-bytecode-equivalent-using-try-catch-finall
   */
  @Test
  public void properIOHandling() throws IOException {
    try {
      final InputStream in = Res.stream();
      Throwable ioEx = null;
      try {
        Processor.run(in, System.out);
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
}
