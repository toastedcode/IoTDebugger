package com.roboxes.robox;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.roboxes.communication.TcpClient;
import com.roboxes.communication.TcpClientListener;
import com.roboxes.messaging.Message;
import com.roboxes.messaging.Protocol;
import com.roboxes.robox.component.Motor;
import com.roboxes.robox.component.MotorPair;
import com.roboxes.robox.component.Servo;
import com.roboxes.scanner.HealthMonitor;
import com.roboxes.scanner.HealthMonitorListener;

public class Robox implements TcpClientListener, HealthMonitorListener
{
   
   public Robox(RoboxInfo roboxInfo)
   {
      this.roboxInfo = roboxInfo;
   }
   
   public void setProtocol(Protocol protocol)
   {
      this.protocol = protocol;
   }
   
   public void setLocalAddress(InetAddress localAddress)
   {
      this.localAddress = localAddress;
   }
   
   public RoboxInfo getRoboxInfo()
   {
      return (roboxInfo);
   }

   // **************************************************************************
   //                                  Connection
  
   public void connect()
   {
      if (isConnected())
      {
         disconnect();
      }
      
      // Connect to the Robox with TCP socket connection.
      client = new TcpClient(roboxInfo.address, TCP_PORT, localAddress, false);
      client.addListener(this);
      client.connect();
   }
   
   public void disconnect()
   {
      if (isConnected())
      {
         client.disconnect();
      }
   }
   
   public boolean isConnected()
   {
      return ((client != null) && (client.isConnected()));
   }
   
   // **************************************************************************
   //                                  Listeners
   
   public boolean addListener(RoboxListener listener)
   {
      return (listeners.add(listener));
   }
   
   public boolean removeListener(RoboxListener listener)
   {
      return (listeners.remove(listener));
   }

   // **************************************************************************
   //                                  Control
   
   public boolean ping()
   {
      return (sendMessage(new Message("ping")));
   }
   
   public boolean pong()
   {
      return (sendMessage(new Message("pong")));
   }
   
   public boolean reset()
   {
      return (sendMessage(new Message("reset")));
   }
   
   public boolean analogWrite(int pin, int value)
   {
      Message message = new Message("analogWrite");
      message.put("pin",  pin);
      message.put("value",  value);
      
      return (sendMessage(message));
   }
   
   public boolean digitalWrite(int pin, int value)
   {
      Message message = new Message("digitalWrite");
      message.put("pin",  pin);
      message.put("value",  value);
      
      return (sendMessage(message));
   }
   
   // **************************************************************************
   //                                  Messaging
   
   public boolean sendMessage(Message message)
   {
      boolean success = false;
      
      String rawMessage = protocol.serialize(message);
      
      if (!rawMessage.isEmpty())
      {
         success = sendRawMessage(rawMessage);
      }
          
      return (success);
   }
   
   public boolean sendRawMessage(String rawMessage)
   {
      boolean success = false;
      
      if (isConnected() && (protocol != null))
      {
         if (!rawMessage.isEmpty())
         {
            success = client.send(rawMessage);
         }
      }
      
      return (success);
   }
   
   // **************************************************************************
   //                         TcpClientListener interface
   
   @Override
   public void receiveData(String data)
   {
      if (protocol != null)
      {
         Message message = protocol.parse(data);
         
         if (message != null)
         {
            handleMessage(message);
         }
      }
   }

   @Override
   public void onConnected()
   {
      // Start the health monitor.
      healthMonitor = new HealthMonitor(client, protocol, 1000, 1);
      healthMonitor.addListener(this);
      healthMonitor.start();
      
      for (RoboxListener listener : listeners)
      {
         listener.onConnected(this);
      }
   }

   @Override
   public void onDisconnected()
   {
      // Stop the health monitor.
      if (healthMonitor != null)
      {
         healthMonitor.stop();
         healthMonitor = null;
      }
      
      // Delete the TCP client.
      client = null;
      
      for (RoboxListener listener : listeners)
      {
         listener.onDisconnected(this);
      }
   }

   @Override
   public void onConnectionFailure()
   {
      System.out.format("Failed to connect to %s.\n", roboxInfo.address.toString());
   }

   // **************************************************************************
   //                       HealthMonitorListener interface

   @Override
   public void onHealthChange(HealthMonitor monitor)
   {
      if (monitor.getHealth() == 0)
      {
         disconnect();
      }
   }
   
   public MotorPair motorPair = new MotorPair("motorPair", this);
   
   public Motor motor1 = new Motor("motor1", this);
   
   public Motor motor2 = new Motor("motor2", this);
   
   public Servo servo1 = new Servo("servo1", this);
   
   public Servo servo2 = new Servo("servo2", this);
   
   // **************************************************************************
   //                                 Private
   // **************************************************************************
   
   private void handleMessage(Message message)
   {
      for (RoboxListener listener : listeners)
      {
         listener.onMessage(this, message);
      }
   }
   
   private static final int TCP_PORT = 1977;
   
   private RoboxInfo roboxInfo;
   
   private TcpClient client;
   
   private Protocol protocol;
   
   InetAddress localAddress;
   
   private List<RoboxListener> listeners = new ArrayList<>();
   
   private HealthMonitor healthMonitor;
}
