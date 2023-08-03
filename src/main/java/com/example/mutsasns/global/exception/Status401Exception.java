package com.example.mutsasns.global.exception;

public abstract class Status401Exception extends RuntimeException {
    public Status401Exception(String message) {
        super(message);
    }
}
