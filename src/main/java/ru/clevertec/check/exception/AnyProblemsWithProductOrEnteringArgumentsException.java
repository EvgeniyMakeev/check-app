package ru.clevertec.check.exception;

public class AnyProblemsWithProductOrEnteringArgumentsException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                BAD REQUEST""";
    }
}
