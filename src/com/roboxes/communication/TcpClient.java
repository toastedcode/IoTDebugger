package com.roboxes.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class TcpClient implements Runnable
{
   // **************************************************************************
   //                            Public
   // **************************************************************************
   
   public TcpClient(
      InetAddress host,
      int port,
      boolean autoReconnect)
   {
      this.host = host;
      this.port = port;
      this.autoReconnect = autoReconnect;
      
      runThread = new Thread(this);
      runThread.start();
   }
   
   public TcpClient(
         InetAddress host,
         int port,
         InetAddress localIpAddress,
         boolean autoReconnect)
      {
         this.host = host;
         this.port = port;
         this.localIpAddress = localIpAddress;
         this.autoReconnect = autoReconnect;
         
         runThread = new Thread(this);
         runThread.start();
      }   
   
   public InetAddress getHost()
   {
      return (host);
   }
   
   public int getPort()
   {
      return (port);
   }
   
   public void connect()
   {
      if (!isConnected())
      {
         System.out.format("Connecting TCP client to %s:%d\n", host.toString(), port);
         
         // Clean up old socket, if it exists.
         if (clientSocket != null)
         {
            try
            {
               clientSocket.close();
            } 
            catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
         
         // Reconnect on a timer.
         retryTimer = new Timer();
         retryTimer.scheduleAtFixedRate(new TimerTask()
         {
            @Override
            public void run()
            {
               try
               {
                  System.out.format("Trying ...\n");
                  
                  // Connect.
                  if (localIpAddress != null)
                  {
                     clientSocket = new Socket(host, port, localIpAddress, port);
                  }
                  else
                  {
                     clientSocket = new Socket(host, port);
                  }

                  // Setup input/output streams.
                  output = new PrintWriter(clientSocket.getOutputStream(), true);
                  input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                  
                  onConnected();
                  
                  retryTimer.cancel();
               }
               catch (IOException e)
               {
                  clientSocket = null;
                  input = null;
                  output = null;
                  
                  onConnectionFailure();
               }
            }
         }, 0, DELAY);
      }
   }

   public void disconnect()
   {
      if (isConnected())
      {
         System.out.format("Disconnecting TCP client.\n");
         
         try
         {
            clientSocket.close();
         } 
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      
      clientSocket = null;
      output = null;
      input = null;
      
      onDisconnected();
   }
   
   public boolean isConnected()
   {
      return ((clientSocket != null) && (clientSocket.isConnected()));
   }
   
   public boolean send(String data)
   {
      boolean success = false;
      
      if (output != null)
      {
         output.write(data);
         output.write(EOL_CHARACTER);
         output.flush();
         
         success = true;
      }
      
      return (success);
   }
   
   public void addListener(TcpClientListener listener)
   {
      listeners.add(listener);
   }
   
   public void removeListener(TcpClientListener listener)
   {
      listeners.remove(listener);
   }
   
   // **************************************************************************
   //                              Thread interface

   @Override
   public void run()
   {
      System.out.println("Thread start.");
      while (true)
      {
         try
         {
            if (isConnected() && (input != null) && input.ready())
            {
               // Read data from the connection.
               String data = input.readLine();
               
               if (!data.isEmpty())
               {
                  // Alert listeners.
                  for (DataListener listener : listeners)
                  {
                     listener.receiveData(data);
                  }
               }
            }
            
            Thread.sleep(10);
         }
         catch (Exception e)
         {
            disconnect();
         }
      }
   }
  
   // **************************************************************************
   //                              Protected
   // **************************************************************************   
   
   protected void onConnected()
   {
      // Alert listeners.
      for (TcpClientListener listener : listeners)
      {
         listener.onConnected();
      }
   }
   
   protected void onConnectionFailure()
   {
      // Alert listeners.
      for (TcpClientListener listener : listeners)
      {
         listener.onConnectionFailure();
      }      
   }
   
   protected void onDisconnected()
   {
      // Alert listeners.
      for (TcpClientListener listener : listeners)
      {
         listener.onDisconnected();
      } 
      
      if (autoReconnect)
      {
         connect();
      }
   }
   
   // **************************************************************************
   //                                Private
   // **************************************************************************   

   private final int DELAY = 5000;
   
   private static final char EOL_CHARACTER = '\n';
   
   private InetAddress host;
   
   private int port;
   
   private InetAddress localIpAddress;
   
   private boolean autoReconnect = false;
   
   private Socket clientSocket;
   
   private PrintWriter output;
   
   private BufferedReader input;
   
   private volatile Set<TcpClientListener> listeners = new HashSet<>();
   
   private Timer retryTimer;
   
   Thread runThread;
}
