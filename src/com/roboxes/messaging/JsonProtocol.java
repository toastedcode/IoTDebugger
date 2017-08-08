package com.roboxes.messaging;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonProtocol implements Protocol
{
   public Message parse(String messageString)
   {
      Message message = new Message();
      
      try
      {
         JSONObject json = (JSONObject)parser.parse(messageString);
         
         message = new Message(json);
      }
      catch (ParseException e)
      {
         message = null;
      }
      
      return (message);
   }
   
   public String serialize(Message message)
   {
      JSONObject json = message.getJson();
      
      return (json.toJSONString());
   }
   
   private JSONParser parser = new JSONParser();
}
