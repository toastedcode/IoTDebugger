package com.roboxes.messaging;

public interface Protocol
{
   Message parse(String messageString);
   
   String serialize(Message message);
}
