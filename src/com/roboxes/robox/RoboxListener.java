package com.roboxes.robox;

import com.roboxes.messaging.Message;

public interface RoboxListener
{
   void onConnected(Robox robox);
   
   void onDisconnected(Robox robox);
   
   void onMessage(Robox robox, Message message);
}
