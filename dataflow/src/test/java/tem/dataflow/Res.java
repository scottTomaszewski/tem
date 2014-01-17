package tem.dataflow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

/**
 * Resources for use in the tem. Generates data, random or otherwise.
 */
final class Res {
  public static final int mb1 = 1024 * 1024;
  public static final long mb512 = mb1 * 512L;
  public static final long gb1 = mb1 * 1024L;

  static String string() {
    return "<foo><bar/></foo>";
  }

  static InputStream stream() {
    return new ByteArrayInputStream(string().getBytes());
  }

  static InputSupplier<? extends InputStream> supplier() {
    return ByteStreams.newInputStreamSupplier(string().getBytes());
  }

  static InputSupplier<InputStream> supplier(long size) {
    return Bytes.random().limitedTo(size);
  }

  static InputSupplier<InputStream> cachedSupplier(long size)
      throws IOException {
    return Bytes.random().limitedTo(size).cacheToDisk();
  }

  static InputSupplier<Source> source() {
    return new InputSupplier<Source>() {
      @Override
      public Source getInput() {
        return new StreamSource(stream());
      }
    };
  }

  static InputSupplier<Source> source(
      final InputSupplier<InputStream> from) {
    return new InputSupplier<Source>() {
      @Override
      public Source getInput() throws IOException {
        try {
          return new StreamSource(from.getInput());
        } catch (IOException e) {
          throw new IOException(e);
        }
      }
    };
  }

  static InputSupplier<InputStream> inXml(
      InputSupplier<InputStream> payload) {
    return ByteStreams.join(
        ByteStreams.newInputStreamSupplier("<foo>".getBytes()),
        payload,
        ByteStreams.newInputStreamSupplier("</foo>".getBytes()));
  }

  private Res() {}
}