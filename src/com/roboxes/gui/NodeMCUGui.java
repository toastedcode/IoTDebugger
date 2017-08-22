package com.roboxes.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.roboxes.gui.IndicatorLight.State;
import com.roboxes.messaging.JsonProtocol;
import com.roboxes.messaging.Message;
import com.roboxes.robox.Gpio;
import com.roboxes.robox.Robox;
import com.roboxes.robox.RoboxInfo;
import com.roboxes.robox.RoboxListener;
import com.roboxes.scanner.Scanner;
import com.roboxes.scanner.ScannerListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class NodeMCUGui extends JFrame implements ScannerListener, RoboxListener
{

   private JPanel contentPane;

   /**
    * Launch the application.
    */
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
      {
         public void run()
         {
            try
            {
               NodeMCUGui frame = new NodeMCUGui();
               frame.setVisible(true);
            } catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      });
   }

   /**
    * Create the frame.
    */
   public NodeMCUGui()
   {
      setTitle("Node MCU");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 650, 550);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      JCheckBox checkBox = new JCheckBox("");
      checkBox.setName(Gpio.GPIO_16.toString());
      checkBox.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            Gpio pin = Gpio.valueOf(((JCheckBox)e.getItemSelectable()).getName());
            boolean isChecked = ((JCheckBox)e.getItemSelectable()).isSelected();
            onPinClicked(pin, isChecked);
         }
      });
      checkBox.setOpaque(false);
      checkBox.setToolTipText("GPIO16");
      checkBox.setBounds(379, 54, 21, 23);
      contentPane.add(checkBox);
      
      JLabel label = new JLabel("");
      label.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent arg0) {
            robox.reset();
         }
      });
      label.setBounds(192, 444, 30, 29);
      contentPane.add(label);
      
      JLabel lblNewLabel = new JLabel("");
      lblNewLabel.setBounds(0, 0, 624, 501);
      lblNewLabel.setIcon(new ImageIcon("C:\\Users\\jtost\\Desktop\\nodemcu_pins.png"));
      contentPane.add(lblNewLabel);
      
      indicator = new IndicatorLight();
      indicator.setBounds(10, 11, 30, 30);
      contentPane.add(indicator);
      
      setVisible(true);
      
      selectInterfaceDialog();
      
      // Start scanning for Roboxes.
      System.out.format("Scanning for roboxes ...\n");
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
      System.out.format("Detected %s at %s.\n", roboxInfo.deviceId, roboxInfo.address.toString());
      
      indicator.setState(State.DETECTED);
      
      // Create a new robox.
      robox = new Robox(roboxInfo);
      robox.setProtocol(new JsonProtocol());
      robox.setLocalAddress(localAddress);
      robox.addListener(this);

      // Connect!
      robox.connect();
   }

   @Override
   public void onUndetected(RoboxInfo roboxInfo)
   {
      System.out.format("Undetected robox %s.\n", roboxInfo.deviceId);
      
      indicator.setState(State.UNDETECTED);
   }
   
   // **************************************************************************
   //                          RoboxListener interface
   
   @Override
   public void onConnected(Robox robox)
   {
      System.out.format("Connected to robox %s.\n", robox.getRoboxInfo().deviceId);
      
      indicator.setState(State.CONNECTED);
      
      // Stop the scanner.
      scanner.stop();   
   }

   @Override
   public void onDisconnected(Robox robox)
   {
      System.out.format("Disconnected from %s.\n", robox.getRoboxInfo().deviceId);
      
      indicator.setState(State.DETECTED);
      
      // Start the scanner.
      scanner.start();
   }

   @Override
   public void onMessage(Robox robox, Message message)
   {
      handleMessage(message);
   }

   // **************************************************************************
   
   private void handleMessage(Message message)
   {
      
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
                  this,
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
   
   void onPinClicked(Gpio pin, boolean isClicked)
   {
      robox.digitalWrite((pin.ordinal() + 1), ((isClicked == true) ? 1 : 0));
   }

   private static final int UDP_PORT = 1993;
   
   private static final int SCANNER_FREQUENCY = 5000;  // milliseconds
   
   private InetAddress localAddress;
   
   private Scanner scanner;
   
   private Robox robox;
   
   private JsonProtocol protocol = new JsonProtocol();
   
   private IndicatorLight indicator;
}
