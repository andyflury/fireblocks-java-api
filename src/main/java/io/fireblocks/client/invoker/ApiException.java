package io.fireblocks.client.invoker;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -3708701844311581744L;

    public ApiException() {
        super();
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

}
