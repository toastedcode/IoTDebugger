package com.roboxes.gui;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ConfigureRoboxPanel extends JPanel
{
   private JTextField textField;
   private JTextField textField_1;
   private JTextField textField_2;
   private JTextField textField_3;
   private JTextField textField_4;
   private JTextField textField_5;
   private JTextField textField_6;
   private JTextField textField_7;

   /**
    * Create the panel.
    */
   public ConfigureRoboxPanel()
   {
      SpringLayout springLayout = new SpringLayout();
      setLayout(springLayout);
      
      JLabel lblDeviceConfig = new JLabel("Device Config");
      lblDeviceConfig.setFont(new Font("Tahoma", Font.BOLD, 14));
      springLayout.putConstraint(SpringLayout.NORTH, lblDeviceConfig, 10, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.WEST, lblDeviceConfig, 10, SpringLayout.WEST, this);
      add(lblDeviceConfig);
      
      JLabel lblDeviceId = new JLabel("Device id");
      springLayout.putConstraint(SpringLayout.NORTH, lblDeviceId, 21, SpringLayout.SOUTH, lblDeviceConfig);
      springLayout.putConstraint(SpringLayout.WEST, lblDeviceId, 44, SpringLayout.WEST, this);
      add(lblDeviceId);
      
      textField = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField, 21, SpringLayout.SOUTH, lblDeviceConfig);
      springLayout.putConstraint(SpringLayout.WEST, textField, 12, SpringLayout.EAST, lblDeviceId);
      springLayout.putConstraint(SpringLayout.EAST, textField, 204, SpringLayout.EAST, lblDeviceId);
      add(textField);
      textField.setColumns(10);
      
      JLabel lblWifiConfig = new JLabel("Wifi Config");
      springLayout.putConstraint(SpringLayout.WEST, lblWifiConfig, 0, SpringLayout.WEST, lblDeviceConfig);
      lblWifiConfig.setFont(new Font("Tahoma", Font.BOLD, 14));
      add(lblWifiConfig);
      
      JLabel lblSsid = new JLabel("SSID");
      springLayout.putConstraint(SpringLayout.NORTH, lblSsid, 132, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.SOUTH, lblWifiConfig, -21, SpringLayout.NORTH, lblSsid);
      springLayout.putConstraint(SpringLayout.WEST, lblSsid, 0, SpringLayout.WEST, lblDeviceId);
      add(lblSsid);
      
      textField_1 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField_1, 61, SpringLayout.SOUTH, textField);
      springLayout.putConstraint(SpringLayout.WEST, textField_1, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.EAST, textField_1, 0, SpringLayout.EAST, textField);
      textField_1.setColumns(10);
      add(textField_1);
      
      JLabel lblPassword = new JLabel("Password");
      springLayout.putConstraint(SpringLayout.WEST, lblPassword, 0, SpringLayout.WEST, lblDeviceId);
      add(lblPassword);
      
      textField_2 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, lblPassword, 3, SpringLayout.NORTH, textField_2);
      springLayout.putConstraint(SpringLayout.NORTH, textField_2, 26, SpringLayout.SOUTH, textField_1);
      springLayout.putConstraint(SpringLayout.WEST, textField_2, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.EAST, textField_2, 0, SpringLayout.EAST, textField);
      textField_2.setColumns(10);
      add(textField_2);
      
      JLabel lblServerConfig = new JLabel("Server Config");
      springLayout.putConstraint(SpringLayout.NORTH, lblServerConfig, 39, SpringLayout.SOUTH, lblPassword);
      springLayout.putConstraint(SpringLayout.WEST, lblServerConfig, 0, SpringLayout.WEST, lblDeviceConfig);
      lblServerConfig.setFont(new Font("Tahoma", Font.BOLD, 14));
      add(lblServerConfig);
      
      JLabel lblHost = new JLabel("Host");
      springLayout.putConstraint(SpringLayout.NORTH, lblHost, 25, SpringLayout.SOUTH, lblServerConfig);
      springLayout.putConstraint(SpringLayout.WEST, lblHost, 0, SpringLayout.WEST, lblDeviceId);
      add(lblHost);
      
      textField_3 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField_3, 19, SpringLayout.SOUTH, lblServerConfig);
      springLayout.putConstraint(SpringLayout.WEST, textField_3, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.EAST, textField_3, 0, SpringLayout.EAST, textField);
      textField_3.setColumns(10);
      add(textField_3);
      
      JLabel lblPort = new JLabel("Port");
      springLayout.putConstraint(SpringLayout.NORTH, lblPort, 18, SpringLayout.SOUTH, lblHost);
      springLayout.putConstraint(SpringLayout.WEST, lblPort, 0, SpringLayout.WEST, lblDeviceId);
      add(lblPort);
      
      textField_4 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField_4, -3, SpringLayout.NORTH, lblPort);
      springLayout.putConstraint(SpringLayout.WEST, textField_4, 0, SpringLayout.WEST, textField);
      textField_4.setColumns(10);
      add(textField_4);
      
      JLabel lblUsername = new JLabel("Username");
      springLayout.putConstraint(SpringLayout.NORTH, lblUsername, 21, SpringLayout.SOUTH, lblPort);
      springLayout.putConstraint(SpringLayout.WEST, lblUsername, 0, SpringLayout.WEST, lblDeviceId);
      add(lblUsername);
      
      textField_5 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField_5, 12, SpringLayout.SOUTH, textField_4);
      springLayout.putConstraint(SpringLayout.WEST, textField_5, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.EAST, textField_5, 0, SpringLayout.EAST, textField);
      textField_5.setColumns(10);
      add(textField_5);
      
      JLabel label = new JLabel("Password");
      springLayout.putConstraint(SpringLayout.NORTH, label, 20, SpringLayout.SOUTH, lblUsername);
      springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lblDeviceId);
      add(label);
      
      textField_6 = new JTextField();
      springLayout.putConstraint(SpringLayout.NORTH, textField_6, 20, SpringLayout.SOUTH, textField_5);
      springLayout.putConstraint(SpringLayout.WEST, textField_6, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.EAST, textField_6, 0, SpringLayout.EAST, textField);
      textField_6.setColumns(10);
      add(textField_6);
      
      JLabel lblToken = new JLabel("Token");
      springLayout.putConstraint(SpringLayout.WEST, lblToken, 0, SpringLayout.WEST, lblDeviceId);
      springLayout.putConstraint(SpringLayout.SOUTH, lblToken, -10, SpringLayout.SOUTH, this);
      add(lblToken);
      
      textField_7 = new JTextField();
      springLayout.putConstraint(SpringLayout.WEST, textField_7, 0, SpringLayout.WEST, textField);
      springLayout.putConstraint(SpringLayout.SOUTH, textField_7, -10, SpringLayout.SOUTH, this);
      springLayout.putConstraint(SpringLayout.EAST, textField_7, 0, SpringLayout.EAST, textField);
      textField_7.setColumns(10);
      add(textField_7);

   }
}
