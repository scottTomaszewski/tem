package tem.dataflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

/**
 * Mysterious processor. Represents any operation we perform in our code. Could
 * be validation, a transform, etc.
 * <p>
 * This Processor simply copies the input to the output for clarity
 * </p>
 */
final class Processor {
  static void run(
      InputSupplier<? extends InputStream> from,
      OutputSupplier<? extends OutputStream> to) throws IOException {
    try (OutputStream out = to.getOutput()) {
      run(from, out);
    }
  }

  static void run(ByteSource from, ByteSink to) throws IOException {
    try (OutputStream out = to.openStream()) {
      run(from, out);
    }
  }

  static void run(InputSupplier<? extends InputStream> from, OutputStream to)
      throws IOException {
    try (InputStream in = from.getInput()) {
      run(in, to);
    }
  }

  static void run(InputStream from, OutputStream to) throws IOException {
    ByteStreams.copy(from, to);
  }

  static void duplicateInput(
      InputSupplier<? extends InputStream> from,
      OutputSupplier<? extends OutputStream> to) throws IOException {
    try (OutputStream out = to.getOutput()) {
      ByteStreams.copy(ByteStreams.join(from, from), out);
    }
  }

  static void duplicateInput(
      InputSupplier<? extends InputStream> from,
      OutputStream to) throws IOException {
    ByteStreams.copy(ByteStreams.join(from, from), to);
  }
}
