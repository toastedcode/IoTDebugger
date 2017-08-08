package com.roboxes.messaging;

import org.json.simple.JSONObject;

public class Message
{
   public Message()
   {
      json = new JSONObject();
   }
   
   public Message(String messageId)
   {
      json = new JSONObject();
      setMessageId(messageId);
   }
   
   public Message(JSONObject json)
   {
      this.json = (JSONObject)json.clone();
   }
   
   public String getMessageId()
   {
      return ((String)json.get("messageId"));
   }

   @SuppressWarnings("unchecked")
   public void setMessageId(
      String messageId)
   {
      json.put("messageId",  messageId);
   }

   public String getSource()
   {
      return ((String)json.get("source"));
   }

   @SuppressWarnings("unchecked")
   public void setSource(
      String id)
   {
      json.put("source",  id);
   }

   public String getDestination()
   {
      return ((String)json.get("destination"));
   }

   @SuppressWarnings("unchecked")
   public void setDestination(
      String id)
   {
      json.put("destination",  id);
   }

   public String getTopic()
   {
      return ((String)json.get("topic"));
   }

   @SuppressWarnings("unchecked")
   public void setTopic(
      String topic)
   {
      json.put("topic", topic);      
   }
   
   @SuppressWarnings("unchecked")
   public void put(String key, Object value)
   {
      json.put(key,  value);
   }
   
   public Object get(String key)
   {
      return (json.get(key));
   }
   
   public JSONObject getJson()
   {
      return ((JSONObject)json.clone());
   }
   
   public int getInt(String key)
   {
      int value = 0;
      
      try
      {
         Long longValue = (Long)json.get(key);
         value = longValue.intValue();
      }
      catch (NullPointerException | ClassCastException e)
      {
         //  No value existed, or was not an Integer.
      }
      
      return (value);
   }
   
   public String getString(String key)
   {
      String value = "";

      try
      {
         value = (String)json.get(key);
      }
      catch (NullPointerException | ClassCastException e)
      {
         //  No value existed, or was not a String.
      }

      return (value);
   }
   
   JSONObject json;
}
