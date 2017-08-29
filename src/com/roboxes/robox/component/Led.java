package com.roboxes.robox.component;

import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;

public class Led extends Component
{
   public Led(String componentId, Robox parent)
   {
      super(componentId, parent);
   }
   
   public boolean setBrightness(int brightness)
   {
      Message message = new Message("setBrightness");
      message.put("brightness",  brightness);
      
      return (sendMessage(message));
   }
   
   public boolean blink(String pattern)
   {
      Message message = new Message("setBrightness");
      message.put("pattern",  pattern);
      
      return (sendMessage(message));
   }
   
   public boolean pulse(int period)
   {
      Message message = new Message("pulse");
      message.put("period",  period);
      
      return (sendMessage(message));
   }
}
