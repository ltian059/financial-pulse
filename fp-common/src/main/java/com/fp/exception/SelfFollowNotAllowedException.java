package com.fp.exception;

public class SelfFollowNotAllowedException extends BaseException{
    public SelfFollowNotAllowedException() {
        super("You cannot follow yourself.");
    }

    public SelfFollowNotAllowedException(String message) {
        super(message);
    }
}
