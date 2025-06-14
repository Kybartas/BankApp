package org.kybartas.exception;

public class ImportException extends Exception {
    public ImportException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
