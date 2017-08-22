package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class Motor extends Component
{
   public Motor(String componentId, Robox parent)
   {
      super(componentId, parent);
   }
   
   public void drive(int speed)
   {
      Message message = new Message("drive");
      message.put("speed", speed);
      
      sendMessage(message);
   }
   
   public void stop()
   {
      drive(0);
   }
}
