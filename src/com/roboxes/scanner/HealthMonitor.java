package com.roboxes.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.roboxes.communication.TcpClient;
import com.roboxes.communication.TcpClientListener;
import com.roboxes.messaging.Message;
import com.roboxes.messaging.Protocol;

public class HealthMonitor implements TcpClientListener
{
   public HealthMonitor(
      TcpClient client,
      Protocol protocol,
      int keepAliveTime,
      int health)
   {
      this.client = client;
      this.protocol = protocol;
      this.keepAliveTime = keepAliveTime;
      this.maxHealth = health;
      this.health = health;
      
      if (client != null)
      {
         client.addListener(this);
      }
   }
   
   public int getHealth()
   {
      return (health);
   }
   
   public void addListener(
      HealthMonitorListener listener)
   {
      listeners.add(listener);
   }
   
   public void removeListener(
      HealthMonitorListener listener)
   {
      listeners.remove(listener);
   }
   
   public void start()
   {
      if ((client != null) &&
          (protocol != null) &&
          (keepAliveTime > 0) &&
          (maxHealth > 0))
      {
         health = maxHealth;
         
         pingTimer = new Timer();
         pingTimer.scheduleAtFixedRate(new TimerTask()
         {
            @Override
            public void run()
            {
               if (sentPing && !receivedPong)
               {
                  onUnsuccessfulPing();
               }
               else if (sentPing && receivedPong)
               {
                  onSuccessfulPing();
               }
              
               receivedPong = false;
               sentPing = sendPing();
            }
         }, keepAliveTime, keepAliveTime);
      }
   }
   
   public void stop()
   {
      pingTimer.cancel();
   }

   @Override
   public void receiveData(String data)
   {
      if ((protocol != null) && !data.isEmpty())
      {
         Message message = protocol.parse(data);
         
         if ((message != null) &&
             (message.getMessageId().equals("pong")))
         {
            receivedPong = true;
         }
      }
   }

   @Override
   public void onConnected()
   {
      // Nothing to do here.
   }

   @Override
   public void onDisconnected()
   {
      // Nothing to do here.
   }

   @Override
   public void onConnectionFailure()
   {
      // Nothing to do here.
   }   
   
   private boolean sendPing()
   {
      boolean success = false;
      
      if ((client != null) && client.isConnected() && (protocol != null))
      {
         Message message = new Message("ping");
         
         success = client.send(protocol.serialize(message));
      }
      
      return (success);
   }
   
   private void onSuccessfulPing()
   {
      if (health < maxHealth)
      {
         health++;

         // Alert listeners.
         for (HealthMonitorListener listener : listeners)
         {
            listener.onHealthChange(this);
         }
         
         System.out.format("HealthMonitor::onSuccessfulPing: %d\n", health);
      }
   }
   
   private void onUnsuccessfulPing()
   {
      if (health > 0)
      {
         health--;

         // Alert listeners.
         for (HealthMonitorListener listener : listeners)
         {
            listener.onHealthChange(this);
         }
         
         System.out.format("HealthMonitor::onUnsuccessfulPing: %d\n", health);
      }
   }

   TcpClient client;
   
   Protocol protocol;
   
   private int keepAliveTime;
   
   private int maxHealth;
   
   private int health;
   
   List<HealthMonitorListener> listeners = new ArrayList<>();
   
   private Timer pingTimer;
   
   private boolean sentPing;
   
   private boolean receivedPong;
}
