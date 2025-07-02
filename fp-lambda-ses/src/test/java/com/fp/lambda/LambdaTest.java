package com.fp.lambda;

public class LambdaTest {

    public static void main(String[] args) {
        System.setProperty("SES_FROM_EMAIL", "li.tian2000@outlook.com");
        System.setProperty("APP_BASE_URL", "http://localhost:8080");

        EmailProcessorHandler handler = new EmailProcessorHandler();
    }
}
