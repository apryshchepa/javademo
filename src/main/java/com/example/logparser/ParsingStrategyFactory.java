package com.example.logparser;

public final class ParsingStrategyFactory {
    private ParsingStrategyFactory() {}

    public static IParsingStrategy getInstance(String argument) {
        return new ReliableParsingStrategy(); // TODO implement faster strategy that does not use JSON serializer
    }
}
