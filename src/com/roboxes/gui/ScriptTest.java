package com.roboxes.gui;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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
import com.roboxes.scripting.Script;
import com.roboxes.scripting.Script.Function;

import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ScriptTest extends JFrame implements ScannerListener, RoboxListener
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
               ScriptTest frame = new ScriptTest();
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
   public ScriptTest()
   {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      
      txtrEnterYourProgram = new JTextArea();
      txtrEnterYourProgram.setText("Enter your program here!");
      txtrEnterYourProgram.setBounds(40, 52, 362, 166);
      contentPane.add(txtrEnterYourProgram);
      
      JButton btnNewButton = new JButton("Play");
      btnNewButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            String code = txtrEnterYourProgram.getText();
            evaluate(code);
         }
      });
      btnNewButton.setBounds(211, 229, 89, 23);
      contentPane.add(btnNewButton);
      
      indicator = new IndicatorLight();
      indicator.setBounds(371, 11, 30, 30);
      contentPane.add(indicator);
      
      JButton btnNewButton_1 = new JButton("Stop");
      btnNewButton_1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            script.stop();
         }
      });
      btnNewButton_1.setBounds(313, 229, 89, 23);
      contentPane.add(btnNewButton_1);
      
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
   
   private void evaluate(String code)
   {
      if (script.interpret(code) == true)
      {
         Script.Parameter param = new Script.Parameter("myRobox", robox);
         script.execute(Function.MAIN, param);
      }
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
 
   private static final int UDP_PORT = 1993;
   
   private static final int SCANNER_FREQUENCY = 5000;  // milliseconds
   
   private JsonProtocol protocol = new JsonProtocol();  
   
   private InetAddress localAddress;
   
   private IndicatorLight indicator;
   
   private Scanner scanner;
   
   private Robox robox;
   
   private Script script = new Script();
   
   private JTextArea txtrEnterYourProgram;
}
