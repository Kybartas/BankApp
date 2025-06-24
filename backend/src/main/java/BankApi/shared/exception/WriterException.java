package BankApi.shared.exception;

public class WriterException extends Exception{
    public WriterException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
