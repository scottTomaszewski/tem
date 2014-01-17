package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

public final class StreamingWorkflow {
  @Test
  public void flowsTwice() throws IOException {
    InputSupplier<InputStream> first = new CreateAndPrint();
    InputSupplier<InputStream> printed = new Prints(first);
    ByteStreams.copy(printed, System.out);
    ByteStreams.copy(printed, System.out);
  }

  private final class CreateAndPrint
      implements InputSupplier<InputStream> {
    @Override
    public InputStream getInput() throws IOException {
      InputStream stream = Res.stream();
      System.out.println("\nNew InputStream created.");
      return stream;
    }
  }

  private static final class Prints
      implements InputSupplier<InputStream> {
    private final InputSupplier<InputStream> from;

    public Prints(InputSupplier<InputStream> from) {
      this.from = from;
    }

    @Override
    public InputStream getInput() throws IOException {
      InputStream input = from.getInput();
      System.out.println("Second operation executed.");
      return input;
    }
  }
}
