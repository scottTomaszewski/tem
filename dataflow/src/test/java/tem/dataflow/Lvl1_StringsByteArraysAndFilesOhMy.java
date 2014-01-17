package tem.dataflow;

import java.io.File;
import java.net.URL;

public class Lvl1_StringsByteArraysAndFilesOhMy {
  interface Endpoint {

    /* Validates input */
    boolean validate(byte[] toValidate);

    // is this a url or the contents?
    boolean validate(String toValidate);

    boolean validate(File toValidate);

    boolean validate(URL toValidate);

    boolean validate(char[] toValidate);

    boolean validate(CharSequence toValidate);

    /* Validate input and ingests into cloud */

    boolean validateAndIngest(byte[] toValidate);

    boolean validateAndIngest(String toValidate);

    boolean validateAndIngest(File toValidate);

    boolean validateAndIngest(URL toValidate);

    boolean validateAndIngest(char[] toValidate);

    boolean validateAndIngest(CharSequence toValidate);
  }
}
