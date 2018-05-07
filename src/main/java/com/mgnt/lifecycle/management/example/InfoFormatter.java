package com.mgnt.lifecycle.management.example;

/*
 * This is an Interface for which we will have some concrete implementations that will be accessible from its factory
 */
public interface InfoFormatter {
    String formatMessage(String messageContent);
}
