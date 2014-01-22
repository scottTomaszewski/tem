package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteSource;

public final class StreamingWorkflow {
  @Test
  public void flowsTwice() throws IOException {
    ByteSource from = Res.byteSource();
    ByteSource first = new PrintAndPassThrough(from, "\nfirst");
    ByteSource second = new PrintAndPassThrough(first, "second");
    second.copyTo(System.out);
    second.copyTo(System.out);
  }

  private static final class PrintAndPassThrough extends ByteSource {
    private final ByteSource from;
    private final String toPrint;

    public PrintAndPassThrough(ByteSource from, String toPrint) {
      this.from = from;
      this.toPrint = toPrint; 
    }

    @Override
    public InputStream openStream() throws IOException {
      InputStream input = from.openStream();
      System.out.println(toPrint);
      return input;
    }
  }
}
