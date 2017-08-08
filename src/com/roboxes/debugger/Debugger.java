package com.roboxes.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.roboxes.communication.TcpClient;
import com.roboxes.communication.TcpClientListener;
import com.roboxes.messaging.JsonProtocol;
import com.roboxes.messaging.Message;
import com.roboxes.scanner.RoboxInfo;
import com.roboxes.scanner.Scanner;
import com.roboxes.scanner.ScannerListener;

public class Debugger implements TcpClientListener, ScannerListener
{
   public static void main(final String args[])
   {
      try
      {
         // Spawn the GUI thread.
         // Note: invokeLater() causes this to be executed on the event dispatch thread.
         SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  new Debugger();
               }
            });
      }
      catch(Exception e)
      {
         System.out.print("Failed to start application.\n");            
      }
   }
   
   public Debugger()
   {
      loadConfiguration();
      
      createGui();
      
      // Start scanning for Roboxes.
      println("Scanning for roboxes ...");
      scanner = new Scanner(UDP_PORT, UDP_PORT);
      scanner.setProtocol(protocol);
      scanner.addListener(this);
      scanner.start();
   }
   
   // **************************************************************************
   //                          Scanner interface
   
   @Override
   public void onDetected(RoboxInfo roboxInfo)
   {
      if (detectedRoboxes.add(roboxInfo))
      {
         // Connect to the Robox with TCP socket connection.
         client = new TcpClient(roboxInfo.address, TCP_PORT, localAddress, true);
         client.addListener(this);
         client.connect();
      }
   }
   
   // **************************************************************************
   //                          TcpClientListener interface

   @Override
   public void receiveData(String data)
   {
      Message message = protocol.parse(data);
      
      if (message != null)
      {
         handleMessage(message);
      }
   }

   @Override
   public void onConnected()
   {
      println("Logger connected.");
      
      if ((client != null) && client.isConnected())
      {
         /*
         // Setup remote logging.
         Message message = new Message("setLogger");
         message.put("adapter",  "debug");
         client.send(protocol.serialize(message));
         */
         
         // Stop the scanner.
         scanner.stop();
      }
   }

   @Override
   public void onDisconnected()
   {
      println("Logger disconnected.");
      
      // TODO: Just remove the one that disconnected.
      detectedRoboxes.clear();
      
      // Start scanning again.
      scanner.start();
   }

   @Override
   public void onConnectionFailure()
   {
      println("Logger failed to connect.");
      
      client = null;
   }

   // **************************************************************************
   
   private void loadConfiguration()
   {
      
   }
   
   private void createGui()
   {
      frame = new JFrame("IoT Debugger");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
      
      frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
      
      // TODO: GUI creation
      
      logsPane = new JTextPane();
      logsPane.setEditable(false);
      
      JScrollPane scrollPane = new JScrollPane(logsPane);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
      
      centerPanel.add(scrollPane);
     
      JPanel commandPanel = new JPanel();
      commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.X_AXIS));
      commandPanel.add(new JLabel("Command: "));
      
      commandBox = new JTextField(50);
      commandBox.setMaximumSize(commandBox.getPreferredSize());
      commandBox.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent action)
         {
            String text = commandBox.getText();
            println(text);
            commandBox.setText("");
            
            if ((client != null) && client.isConnected())
            {
               client.send(text);
            }
         }
      });
      
      commandPanel.add(commandBox);
      centerPanel.add(commandPanel);
      
      JPanel shortcutPanel = new JPanel();
      shortcutPanel.setLayout(new BoxLayout(shortcutPanel, BoxLayout.X_AXIS));
      centerPanel.add(shortcutPanel);
      
      JButton button = new JButton("Ping");
      button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            sendMessage(new Message("ping"));
         }          
      });
      shortcutPanel.add(button);
      
      button = new JButton("Properties");
      button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            sendMessage(new Message("property"));
         }          
      });
      shortcutPanel.add(button);
 
      frame.setSize(640, 480);
      frame.setVisible(true);
   }
   
   private void handleMessage(Message message)
   {
      if (message.getMessageId().equals("ping"))
      {
         Message replyMessage = new Message("pong");
         replyMessage.setDestination(message.getSource());
         sendMessage(replyMessage);
      }
      else if (message.getMessageId().equals("logMessage"))
      {
         onLogMessage(message);
      }
      else
      {
         println(protocol.serialize(message));
      }
   }
   
   private void onLogMessage(
      Message logMessage)
   {
      
      println(logMessage.getString("logLevel") + ": " + logMessage.getString("message"));
   }
   
   private void println(
         String string)
   {
      StyledDocument doc = logsPane.getStyledDocument();
      
      try
      {
         doc.insertString(doc.getLength(),  string + "\n", null);
      } 
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }      
   }
   
   private void sendMessage(Message message)
   {
      if ((client != null) && client.isConnected())
      {
         String data = protocol.serialize(message);
         if (!data.isEmpty())
         {
            println(data);
            client.send(protocol.serialize(message));
         }
      }
   }
   
   private static final int UDP_PORT = 1993;
   
   private static final int TCP_PORT = 1977;
   
   private JFrame frame;
   
   private JTextPane logsPane;
   
   private JTextField commandBox;
   
   private Scanner scanner;
   
   private Set<RoboxInfo> detectedRoboxes = new HashSet<>();
   
   InetAddress localAddress;
   
   private TcpClient client;
   
   private JsonProtocol protocol = new JsonProtocol();
   
   private Timer penetrationTimer;
}
