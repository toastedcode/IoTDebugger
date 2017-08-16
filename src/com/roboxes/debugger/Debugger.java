package com.roboxes.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.roboxes.messaging.JsonProtocol;
import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;
import com.roboxes.robox.RoboxInfo;
import com.roboxes.robox.RoboxListener;
import com.roboxes.scanner.HealthMonitor;
import com.roboxes.scanner.Scanner;
import com.roboxes.scanner.ScannerListener;

public class Debugger implements ScannerListener, RoboxListener
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
      
      selectInterfaceDialog();
      
      // Start scanning for Roboxes.
      println("Scanning for roboxes ...");
      scanner = new Scanner(UDP_PORT, UDP_PORT, localAddress, SCANNER_FREQUENCY);
      scanner.setProtocol(protocol);
      scanner.addListener(this);
      scanner.start();
   }
   
   // **************************************************************************
   //                         ScannerListener interface
   
   @Override
   public void onDetected(RoboxInfo roboxInfo)
   {
      if (detectedRoboxes.add(roboxInfo))
      {
         println("Detected " + roboxInfo.deviceId + " at " + roboxInfo.address.toString() + ".");
         
         // Create a new robox.
         robox = new Robox(roboxInfo);
         robox.setProtocol(new JsonProtocol());
         robox.setLocalAddress(localAddress);
         robox.addListener(this);

         // Connect!
         robox.connect();
      }
   }

   @Override
   public void onUndetected(RoboxInfo roboxInfo)
   {
      println("Undetected robox " + roboxInfo.deviceId + ".");
   }
   
   // **************************************************************************
   //                          RoboxListener interface
   
   @Override
   public void onConnected(Robox robox)
   {
      println("Connected to " +  robox.getRoboxInfo().deviceId + ".");
      
      // Stop the scanner.
      scanner.stop();   
   }

   @Override
   public void onDisconnected(Robox robox)
   {
      println("Disconnected from " +  robox.getRoboxInfo().deviceId + ".");
      
      // Start the scanner.
      detectedRoboxes.clear();
      scanner.start();
   }

   @Override
   public void onMessage(Robox robox, Message message)
   {
      handleMessage(message);
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
            
            robox.sendRawMessage(text);
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
            robox.ping();
         }          
      });
      shortcutPanel.add(button);
      
      button = new JButton("Properties");
      button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            robox.sendMessage(new Message("property"));
         }          
      });
      shortcutPanel.add(button);
 
      frame.setSize(640, 480);
      frame.setVisible(true);
   }
   
   private void selectInterfaceDialog()
   {
      try
      {
         Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
         
         List<NetworkInterface> physicalNets = new ArrayList<>();
         
         for (NetworkInterface net : Collections.list(nets))
         {
            if (net.isUp() && !net.isLoopback() && !net.isVirtual())
            {
               physicalNets.add(net);
            }
         }
         
         NetworkInterface net = 
               (NetworkInterface)JOptionPane.showInputDialog(
                  frame,
                  "Select the network interface to use:\n",
                  "Select Interface",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  physicalNets.toArray(),
                  "ham");
         
         if (net != null)
         {
            localAddress = net.getInetAddresses().nextElement();
         }
      }
      catch (SocketException e)
      {
         
      }
   }
   
   private void handleMessage(Message message)
   {
      if (message.getMessageId().equals("ping"))
      {
         robox.pong();
      }
      else if (message.getMessageId().equals("pong"))
      {
         // Silently ignore.
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
   
   private static final int UDP_PORT = 1993;
   
   private static final int SCANNER_FREQUENCY = 5000;  // milliseconds
   
   private JFrame frame;
   
   private JTextPane logsPane;
   
   private JTextField commandBox;
   
   private Scanner scanner;
   
   private Robox robox;
   
   private Set<RoboxInfo> detectedRoboxes = new HashSet<>();
   
   InetAddress localAddress;
   
   private JsonProtocol protocol = new JsonProtocol();
   
   HealthMonitor healthMonitor;
}
