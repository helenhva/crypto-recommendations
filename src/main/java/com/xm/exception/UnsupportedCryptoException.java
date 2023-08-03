package com.xm.exception;

public class UnsupportedCryptoException extends RuntimeException {
    public UnsupportedCryptoException(String cryptoSymbol) {
        super("Unsupported crypto symbol: " + cryptoSymbol);
    }
}
