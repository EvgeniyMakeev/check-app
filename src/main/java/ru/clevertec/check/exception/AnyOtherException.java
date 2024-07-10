package ru.clevertec.check.exception;

public class AnyOtherException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                INTERNAL SERVER ERROR""";
    }
}
