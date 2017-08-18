package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class MotorPair extends Component
{
   public MotorPair(String componentId, Robox parent)
   {
      super(componentId, parent);
   }
   
   public void drive(int speed)
   {
      Message message = new Message("drive");
      message.setDestination(getId());
      message.put("speed", speed);
      
      sendMessage(message);
   }
   
   public void rotate(int speed)
   {
      Message message = new Message("rotate");
      message.setDestination(getId());
      message.put("speed", speed);
      
      sendMessage(message);      
   }
   
   public void stop()
   {
      drive(0);
   }
}
