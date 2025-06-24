package BankApi.shared.exception;

public class ReaderException extends Exception{
    public ReaderException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
