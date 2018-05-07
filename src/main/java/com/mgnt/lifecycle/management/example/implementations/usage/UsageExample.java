package com.mgnt.lifecycle.management.example.implementations.usage;

import com.mgnt.lifecycle.management.example.BaseInfoFormatter;
import com.mgnt.lifecycle.management.example.InfoFormatter;
import com.mgnt.lifecycle.management.example.InfoFormatterFactory;
import com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter;
import com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter;

import java.util.ArrayList;
import java.util.List;

public class UsageExample {
    private static final String MESSAGE = "Hello World";

   public static void main(String... args) {
        init();
        printFormattedGreetings();
   }

    private static void printFormattedGreetings() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON");
       System.out.println("JSON greeting: " + formatter.formatMessage(MESSAGE));
       formatter = InfoFormatterFactory.getInstance("XML");
        System.out.println("XML greeting: " + formatter.formatMessage(MESSAGE));
       List<String> allMessages = new ArrayList<>();
       for(InfoFormatter formattedMessage : InfoFormatterFactory.getAllInstances()) {
           allMessages.add(formattedMessage.formatMessage(MESSAGE));
       }
        System.out.println("All greetings: " + allMessages);
    }

    private static void init() {
       new JsonInfoFormatter();
       new XmlInfoFormatter();
    }
}
