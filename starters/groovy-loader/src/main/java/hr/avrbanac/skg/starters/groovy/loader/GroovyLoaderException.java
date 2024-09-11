package hr.avrbanac.skg.starters.groovy.loader;

import java.io.Serial;

public class GroovyLoaderException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 20240911131500L;

    public static final GroovyLoaderException GENERAL_ERROR = new GroovyLoaderException(ErrorClass.GENERAL_ERROR);
    public static final GroovyLoaderException SECURITY_ERROR = new GroovyLoaderException(ErrorClass.SECURITY_ERROR);
    public static final GroovyLoaderException ACCESS_ERROR = new GroovyLoaderException(ErrorClass.ACCESS_ERROR);
    public static final GroovyLoaderException PARSE_ERROR = new GroovyLoaderException(ErrorClass.PARSE_ERROR);
    public static final GroovyLoaderException NO_CLASSES_FOUND = new GroovyLoaderException(ErrorClass.NO_CLASSES_FOUND);


    private final ErrorClass errorClass;

    private GroovyLoaderException(final ErrorClass errorClass) {
        super(errorClass.errorMessage);
        this.errorClass = errorClass;
    }

    public GroovyLoaderException(final String customErrorMessage) {
        super(ErrorClass.CUSTOM_ERROR.errorMessage + customErrorMessage);
        this.errorClass = ErrorClass.CUSTOM_ERROR;
    }

    public int getErrorCode() {
        return this.errorClass.errorCode;
    }

    private String getErrorMessage() {
        return super.getMessage();
    }

    private enum ErrorClass {
        GENERAL_ERROR       (1,
                "General error occurred"),
        CUSTOM_ERROR        (128,
                "Application error occurred: "),
        SECURITY_ERROR      (2048,
                "Denied access to the file/folder by the security manager"),
        ACCESS_ERROR        (3072,
                "Error occurred while accessing file/folder"),
        PARSE_ERROR         (32768,
                "Error occurred while trying to parse groovy class"),
        NO_CLASSES_FOUND    (524288,
                "Found no Groovy implementation classes");

        private final int errorCode;
        private final String errorMessage;

        ErrorClass(
                final int errorCode,
                final String errorMessage) {

            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
