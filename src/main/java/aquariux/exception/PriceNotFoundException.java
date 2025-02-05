package aquariux.exception;

public class PriceNotFoundException extends TradingException{ 

    public PriceNotFoundException(String message) {
        super(message);
    }

    public PriceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}