package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteSource;

public final class StreamingWorkflow {
  @Test
  public void flowsTwice() throws IOException {
    ByteSource first = new CreateAndPrint();
    ByteSource printed = new Prints(first);
    printed.copyTo(System.out);
    printed.copyTo(System.out);
  }

  private final class CreateAndPrint extends ByteSource {
    @Override
    public InputStream openStream() throws IOException {
      InputStream stream = Res.stream();
      System.out.println("\nNew InputStream created.");
      return stream;
    }
  }

  private static final class Prints extends ByteSource {
    private final ByteSource from;

    public Prints(ByteSource from) {
      this.from = from;
    }

    @Override
    public InputStream openStream() throws IOException {
      InputStream input = from.openStream();
      System.out.println("Second operation executed.");
      return input;
    }
  }
}
