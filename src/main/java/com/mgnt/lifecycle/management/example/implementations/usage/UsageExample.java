package com.mgnt.lifecycle.management.example.implementations.usage;

import java.util.ArrayList;
import java.util.List;

import com.mgnt.lifecycle.management.example.InfoFormatter;
import com.mgnt.lifecycle.management.example.InfoFormatterFactory;
import com.mgnt.lifecycle.management.example.implementations.DATA_TYPE;
import com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter;
import com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter;

public class UsageExample {
    private static final String MESSAGE = "Hello World";

   public static void main(String... args) {
        init();
        printFormattedGreetings();
   }

    private static void printFormattedGreetings() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance(DATA_TYPE.JSON.toString());
       System.out.println("JSON greeting: " + formatter.formatMessage(MESSAGE));
       formatter = InfoFormatterFactory.getInstance(DATA_TYPE.XML.toString());
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
