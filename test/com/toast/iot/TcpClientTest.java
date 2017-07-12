package com.toast.iot;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.toast.android.communication.TcpClient;
import com.toast.android.communication.TcpClientListener;

public class TcpClientTest implements TcpClientListener
{
   @Test
   public void test() throws UnknownHostException
   {
      host = InetAddress.getByName("10.1.50.113");
      port = 1975;
      InetAddress localAddress = InetAddress.getByName("10.1.50.217");
      
      client = new TcpClient(host, port, localAddress, true);
      client.addListener(this);
      client.connect();
      
      while (running)
      {
         // Loop.
      }
   }

   @Override
   public void receiveData(String data)
   {
      System.out.format("Received data: %s", data);
   }

   @Override
   public void onConnected()
   {
      System.out.format("Connected.\n");
      
      client.send("{messageId:ping}");
   }

   @Override
   public void onDisconnected()
   {
      System.out.format("Disconnected.\n");
      
      running = false;
   }

   @Override
   public void onConnectionFailure()
   {
      System.out.format("Connection failed.\n");      
   }
   
   boolean running = true;
   
   TcpClient client;
   
   InetAddress host;
   
   int port;
}
