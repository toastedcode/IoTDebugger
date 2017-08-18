package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class Servo extends Component
{
   public Servo(String componentId, Robox parent)
   {
      super(componentId, parent);
   }
   
   public void rotate(int angle)
   {
      Message message = new Message("rotate");
      message.put("angle", angle);
      
      sendMessage(message);      
   }
   
   public void panTo(int angle, int seconds)
   {
      Message message = new Message("panTo");
      message.put("angle", angle);
      message.put("seconds", seconds);
      
      sendMessage(message);
   }
   
   public void oscillate(int startAngle, int endAngle, int seconds)
   {
      Message message = new Message("oscillate");
      message.put("startAngle", startAngle);
      message.put("endAngle", endAngle);
      message.put("seconds", seconds);
      
      sendMessage(message);
   }
   
   public void stop()
   {
      Message message = new Message("stop");
      
      sendMessage(message);
   }
}
