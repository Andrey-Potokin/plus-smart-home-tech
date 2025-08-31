//package shoppingstore;
//
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.experimental.FieldDefaults;
//import org.springframework.http.HttpStatus;
//
//@Getter
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class ProductNotFoundException extends Exception {
//
//    Cause cause;
//    StackTrace stackTrace;
//    HttpStatus httpStatus;
//    String userMessage;
//    String message;
//    Suppressed suppressed;
//    String localizedMessage;
//
//    public ProductNotFoundException(String message, Cause cause, StackTrace stackTrace,
//                                    HttpStatus httpStatus, String userMessage, Suppressed suppressed,
//                                    String localizedMessage) {
//        super(message);
//        this.message = message;
//        this.cause = cause;
//        this.stackTrace = stackTrace;
//        this.httpStatus = httpStatus;
//        this.userMessage = userMessage;
//        this.suppressed = suppressed;
//        this.localizedMessage = localizedMessage;
//    }
//
//    public record Cause(StackTrace stackTrace, String message, String localizedMessage) {
//    }
//
//    public record StackTrace(String classLoaderName, String moduleName, String moduleVersion,
//                                           String methodName, String fileName, int lineNumber, String className,
//                                           boolean nativeMethod) {
//    }
//
//    public record Suppressed(StackTrace stackTrace, String message,
//                                    String localizedMessage) {
//    }
//}