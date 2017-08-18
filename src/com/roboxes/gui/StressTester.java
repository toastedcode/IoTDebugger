package com.roboxes.gui;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.roboxes.gui.IndicatorLight.State;
import com.roboxes.messaging.JsonProtocol;
import com.roboxes.messaging.Message;
import com.roboxes.robox.Robox;
import com.roboxes.robox.RoboxInfo;
import com.roboxes.robox.RoboxListener;
import com.roboxes.scanner.Scanner;
import com.roboxes.scanner.ScannerListener;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class StressTester extends JFrame implements ScannerListener, RoboxListener
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
               StressTester frame = new StressTester();
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
   public StressTester()
   {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      txtmessageidping = new JTextField();
      txtmessageidping.setText("{\"messageId\":\"ping\"}");
      txtmessageidping.setBounds(93, 118, 320, 20);
      contentPane.add(txtmessageidping);
      txtmessageidping.setColumns(10);
      
      JLabel lblNewLabel = new JLabel("Command");
      lblNewLabel.setBounds(20, 121, 63, 14);
      contentPane.add(lblNewLabel);
      
      frequencySlider = new JSlider();
      frequencySlider.setMaximum(10);
      frequencySlider.setValue(0);
      frequencySlider.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent arg0) {
            JSlider source = (JSlider)arg0.getSource();
            if (!source.getValueIsAdjusting())
            {
                if (source.getValue() == 0)
                {
                   stopTest();
                }
                else
                {
                   startTest();
                }
            }
         }
      });
      frequencySlider.setPaintLabels(true);
      frequencySlider.setMajorTickSpacing(1);
      frequencySlider.setBounds(93, 149, 320, 37);
      contentPane.add(frequencySlider);
      
      JLabel lblNewLabel_1 = new JLabel("Frequency");
      lblNewLabel_1.setBounds(20, 149, 63, 14);
      contentPane.add(lblNewLabel_1);
      
      indicator = new IndicatorLight();
      indicator.setBounds(10, 11, 30, 30);
      contentPane.add(indicator);
      
      textField_1 = new JTextField();
      textField_1.setBounds(226, 197, 86, 20);
      contentPane.add(textField_1);
      textField_1.setColumns(10);
      
      JLabel lblNewLabel_3 = new JLabel("Dropped messages");
      lblNewLabel_3.setBounds(114, 200, 102, 14);
      contentPane.add(lblNewLabel_3);
      
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
      
      // Pause the scanner.
      scanner.pause();   
   }

   @Override
   public void onDisconnected(Robox robox)
   {
      System.out.format("Disconnected from %s.\n", robox.getRoboxInfo().deviceId);
      
      indicator.setState(State.UNDETECTED);
      
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
   
   public void startTest()
   {
      int frequency = 1000 / frequencySlider.getValue();
      
      stressTimer = new Timer();
      stressTimer.scheduleAtFixedRate(new TimerTask()
      {
         @Override
         public void run()
         {
            if ((robox != null) && robox.isConnected())
            {
               Message message = protocol.parse(txtmessageidping.getText());
               if (message != null)
               {
                  message.setTransactionId(Integer.toString(sequenceNumber));
                  robox.sendMessage(message);
                  System.out.println("Ping");
               }
            }
         }
      }, 0, frequency);
   }
   
   public void stopTest()
   {
      stressTimer.cancel();
   }
   
   private static final int UDP_PORT = 1993;
   
   private static final int SCANNER_FREQUENCY = 5000;  // milliseconds
   
   private InetAddress localAddress;
   
   private Scanner scanner;
   
   private Timer stressTimer;
   
   private Robox robox;
   
   private JsonProtocol protocol = new JsonProtocol();   
   private JTextField txtmessageidping;
   private JTextField textField_1;
   private JSlider frequencySlider;
   private IndicatorLight indicator;
   
   private int sequenceNumber = 0;
}
