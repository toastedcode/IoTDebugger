package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class DistanceSensor extends Component
{
   public DistanceSensor(String componentId, Robox parent)
   {
      super(componentId, parent);
   }
   
   public int read()
   {
      Message message = new Message("read");
      sendMessage(message);
      
      return (0);
   }
}