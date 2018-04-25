package com.mgnt.lifecycle.management.example.implementations.usage;

import com.mgnt.lifecycle.management.example.FormattedMessage;
import com.mgnt.lifecycle.management.example.FormattedMessageFactory;
import com.mgnt.lifecycle.management.example.implementations.JsonFormattedMessage;
import com.mgnt.lifecycle.management.example.implementations.XmlFormattedMessage;

import java.util.ArrayList;
import java.util.List;

public class UsageExample {
    private static final String MESSAGE = "Hello World";

   public static void main(String... args) {
        init();
        printFormattedGreetings();
   }

    private static void printFormattedGreetings() {
       System.out.println("JSON greeting: " + FormattedMessageFactory.getInstance("JSON").getFormattedMessage(MESSAGE));
        System.out.println("XML greeting: " + FormattedMessageFactory.getInstance("XML").getFormattedMessage(MESSAGE));
       List<String> allMessages = new ArrayList<>();
       for(FormattedMessage formattedMessage : FormattedMessageFactory.getAllInstances()) {
           allMessages.add(formattedMessage.getFormattedMessage(MESSAGE));
       }
        System.out.println("All greetings: " + allMessages);
    }

    private static void init() {
       new JsonFormattedMessage();
       new XmlFormattedMessage();
    }
}
