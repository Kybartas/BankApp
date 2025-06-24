package BankApi.shared.exception;

public class ExportException extends Exception{
    public ExportException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
