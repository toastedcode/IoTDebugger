package com.roboxes.scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.roboxes.communication.DataListener;
import com.roboxes.communication.UdpServer;
import com.roboxes.messaging.Message;
import com.roboxes.messaging.Protocol;

public class Scanner implements DataListener
{
   final int DELAY = 10 * 1000;  // milliseconds
   
   public Scanner(int sendPort, int listenPort, InetAddress localIpAddress)
   {
      this.sendPort = sendPort;
      
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
         }
      }, 0, DELAY);
   }
   
   public void stop()
   {
      server.stop();
      pingTimer.cancel();
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
      return (detected);
   }
   
   protected void onDetected(RoboxInfo roboxInfo)
   {
      if (detected.contains(roboxInfo) == false)
      {
         detected.add(roboxInfo);
         
         for (ScannerListener listener : listeners)
         {
            listener.onDetected(roboxInfo);
         }
      }
   }
   
   protected void broadcastPing()
   {
      try
      {
         InetAddress BROADCAST_ADDRESS = InetAddress.getByName("255.255.255.255");
         
         server.send(BROADCAST_ADDRESS, sendPort, protocol.serialize(pingMessage));
         
         System.out.format("Sent ping on port %d\n", sendPort);
      }
      catch (UnknownHostException e)
      {
         System.out.format("%s\n", e.toString());
      }
   }

   @Override
   public void receiveData(String data)
   {
      InetAddress nullInetAddress = null;
      try
      {
         nullInetAddress = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
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
   
   private int sendPort = 0;
   
   private Timer pingTimer;
   
   private Set<RoboxInfo> detected = new HashSet<>();
   
   private Set<ScannerListener> listeners = new HashSet<>();
   
   private UdpServer server;
   
   private Protocol protocol;
   
   private Message pingMessage;
}
