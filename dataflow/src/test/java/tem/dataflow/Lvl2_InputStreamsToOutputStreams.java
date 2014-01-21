package tem.dataflow;

import static com.google.common.base.Charsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

public class Lvl2_InputStreamsToOutputStreams {
  @Test
  public void centralizedAPI() throws Exception {
    InputStream in; 
    // Byte array
    in = new ByteArrayInputStream(new byte[]{'f', 'o','o'});
    // String
    in = new ByteArrayInputStream("foo".getBytes(UTF_8));
    // File
    in = new FileInputStream(new File("filename.txt"));
    // URL
    in = new URL("http://www.foo.com/a.txt").openStream();
    // Char array
    in = new ByteArrayInputStream(
        new String(new char[] {'f','o','o'}).getBytes(UTF_8));
    // CharSequence
    in = new ByteArrayInputStream(
        new StringBuilder("foo").toString().getBytes(UTF_8));
  }
  
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
