package ru.clevertec.check.exception;

public class NotEnoughMoneyException extends Exception {
    @Override
    public String getMessage() {
        return """
                ERROR
                NOT ENOUGH MONEY""";
    }
}
