package com.roboxes.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class UdpServer implements Runnable
{
   public UdpServer(int port, InetAddress localIpAddress)
   {
      try
      {
         serverSocket = new DatagramSocket(port, localIpAddress);
      }
      catch (SocketException e)
      {
         System.out.format("%s", e.toString());
      }
      
      Thread thread = new Thread(this);
      thread.start();
   }
   
   public UdpServer(int port)
   {
      try
      {
         serverSocket = new DatagramSocket(port);
      }
      catch (SocketException e)
      {
         System.out.format("%s", e.toString());
      }
      
      Thread thread = new Thread(this);
      thread.start();
   }
   
   public void start()
   {
      listening = true;
      System.out.format("UDP server listening on port %d\n", serverSocket.getLocalPort());
   }

   public void stop()
   {
      listening = false;
      System.out.format("UDP server stopped listening");
   }
   
   public int getPort()
   {
      int port = 0;
      
      if (serverSocket != null)
      {
         port = serverSocket.getLocalPort();
      }
      
      return (port);
   }
   
   @Override
   public void run()
   {
      byte[] receiveData = new byte[1024];
      
      while (true)
      {
         if ((listening) && (serverSocket != null))
         {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            try
            {
               serverSocket.receive(receivePacket);
               
               if (receivePacket.getLength() > 0)
               {
                  String data = new String(receivePacket.getData(), 0, receivePacket.getLength());
   
                  // Alert listeners.
                  for (DataListener listener : listeners)
                  {
                     listener.receiveData(data);
                  }
               }
            }
            catch (IOException e)
            {
               System.out.format("%s", e.toString());
            }
         }
      }
   }
   
   public boolean send(InetAddress address, int port, String data)
   {
      boolean success = false;
      
      byte[] sendData = new byte[1024];
      
      if (serverSocket != null)
      {
         sendData = data.getBytes();
         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
         
         try
         {
            serverSocket.send(sendPacket);
            success = true;
         }
         catch (IOException e)
         {
            System.out.format("%s", e.toString());
         }
      }
      
      return (success);
   }
   
   public void addListener(DataListener listener)
   {
      listeners.add(listener);
   }
   
   public void removeListener(DataListener listener)
   {
      listeners.remove(listener);
   }
   
   private volatile boolean listening = false;
   
   private volatile DatagramSocket serverSocket;
   
   private volatile Set<DataListener> listeners = new HashSet<>();
   
}