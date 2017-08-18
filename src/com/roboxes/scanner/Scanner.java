package com.roboxes.scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.roboxes.communication.DataListener;
import com.roboxes.communication.UdpServer;
import com.roboxes.messaging.Message;
import com.roboxes.messaging.Protocol;
import com.roboxes.robox.RoboxInfo;

public class Scanner implements DataListener
{
   public Scanner(int sendPort, int listenPort, InetAddress localIpAddress, int pingFrequency)
   {
      this.sendPort = sendPort;
      this.pingFrequency = pingFrequency;
      
      server = new UdpServer(listenPort, localIpAddress);
      server.addListener(this);
      pingTimer = new Timer();
      
      pingMessage = new Message();
      pingMessage.setMessageId("ping");
   }
   
   public Scanner(int sendPort, int listenPort)
   {
      this.sendPort = sendPort;
      
      server = new UdpServer(listenPort);
      server.addListener(this);
      pingTimer = new Timer();
      
      pingMessage = new Message();
      pingMessage.setMessageId("ping");
   }
   
   public void setProtocol(Protocol protocol)
   {
      this.protocol = protocol;
   }
   
   public void start()
   {
      server.start();
      
      pingTimer = new Timer();
      pingTimer.scheduleAtFixedRate(new TimerTask()
      {
         @Override
         public void run()
         {
            broadcastPing();
            
            checkUndetected();
         }
      }, 0, pingFrequency);
   }
   
   public void pause()
   {
      pingTimer.cancel();
   }
   
   public void stop()
   {
      server.stop();
      pingTimer.cancel();
      detected.clear();
   }
   
   public void addListener(ScannerListener listener)
   {
      listeners.add(listener);
   }
   
   public void removeListener(ScannerListener listener)
   {
      listeners.remove(listener);
   }
   
   public Set<RoboxInfo> getDetected()
   {
      return (detected.keySet());
   }
   
   // **************************************************************************
   //                         TcpClientListener interface
   
   @Override
   public void receiveData(String data)
   {
      InetAddress nullInetAddress = null;
      try
      {
         nullInetAddress = InetAddress.getByName("0.0.0.0");
      }
      catch (UnknownHostException e)
      {
         // Shouldn't ever get here.
      }
      
      if (protocol != null)
      {
         Message message = protocol.parse(data);
         
         if ((message != null) &&
             (message.getMessageId().equals("pong")))
         {
            String deviceId = message.getString("deviceId");
            
            InetAddress inetAddress;
            try
            {
               inetAddress = InetAddress.getByName(message.getString("ipAddress"));
            }
            catch (UnknownHostException e)
            {
               inetAddress = nullInetAddress;
            }
            
            byte[] macAddress = {0, 0, 0, 0, 0, 0};
            
            RoboxInfo roboxInfo = new RoboxInfo(deviceId, inetAddress, macAddress);
            onDetected(roboxInfo);
         }
      }
   }
   
   // **************************************************************************
      
   protected void onDetected(RoboxInfo roboxInfo)
   {
      if (detected.containsKey(roboxInfo) == false)
      {
         detected.put(roboxInfo, new PingInfo(true, PingInfo.MAX_HEALTH));
         
         for (ScannerListener listener : listeners)
         {
            listener.onDetected(roboxInfo);
         }
      }
      else
      {
         detected.get(roboxInfo).recordResponse();
      }
   }
   
   protected void onUndetected(RoboxInfo roboxInfo)
   {
      detected.remove(roboxInfo);
      
      for (ScannerListener listener : listeners)
      {
         listener.onUndetected(roboxInfo);
      }
   }
   
   protected void broadcastPing()
   {
      try
      {
         InetAddress BROADCAST_ADDRESS = InetAddress.getByName("255.255.255.255");
         
         server.send(BROADCAST_ADDRESS, sendPort, protocol.serialize(pingMessage));
         
         //System.out.format("Sent ping on port %d\n", sendPort);
      }
      catch (UnknownHostException e)
      {
         System.out.format("%s\n", e.toString());
      }
   }
   
   protected void checkUndetected()
   {
      for (RoboxInfo roboxInfo : detected.keySet())
      {
         PingInfo pingInfo = detected.get(roboxInfo);
         
         // Check if we got a ping response from this robox.
         if (pingInfo.gotResponse == false)
         {
            pingInfo.health--;
            
            if (pingInfo.health == 0)
            {
               onUndetected(roboxInfo);
            }
         }
         
         // Reset for next round.
         pingInfo.gotResponse = false;
      }
   }
   
   private class PingInfo
   {
      public PingInfo(boolean gotResponse, int health)
      {
         this.gotResponse = gotResponse;
         this.health = health;
      }
      
      public void recordResponse()
      {
         if (health < MAX_HEALTH)
         {
            health++;
         }
         
         gotResponse = true;
      }
      
      public static final int MAX_HEALTH = 3;
      
      public boolean gotResponse;
      
      public int health;
   }
   
   private int sendPort = 0;
   
   private int pingFrequency = 0;
   
   private Timer pingTimer;
   
   private Map<RoboxInfo, PingInfo> detected = new HashMap<>();
   
   private Set<ScannerListener> listeners = new HashSet<>();
   
   private UdpServer server;
   
   private Protocol protocol;
   
   private Message pingMessage;
}
