package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class Component
{
   protected Component(String componentId, Robox parent)
   {
      this.componentId = componentId;
      this.parent = parent;
   }
   
   protected String getId()
   {
      return (componentId);
   }
   
   protected boolean sendMessage(Message message)
   {
      message.setDestination(getId());
      return (parent.sendMessage(message));
   }

   private String componentId;
   
   private Robox parent;
}
