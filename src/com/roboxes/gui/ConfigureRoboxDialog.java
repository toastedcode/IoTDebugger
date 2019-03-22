package com.roboxes.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class ConfigureRoboxDialog extends JDialog
{

   private final JPanel contentPanel = new JPanel();
   private JTextField textField;
   private JTextField textField_1;

   /**
    * Launch the application.
    */
   public static void main(String[] args)
   {
      try
      {
         ConfigureRoboxDialog dialog = new ConfigureRoboxDialog();
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.setVisible(true);
      } catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Create the dialog.
    */
   public ConfigureRoboxDialog()
   {
      setBounds(100, 100, 450, 300);
      getContentPane().setLayout(new BorderLayout());
      contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      getContentPane().add(contentPanel, BorderLayout.CENTER);
      SpringLayout sl_contentPanel = new SpringLayout();
      contentPanel.setLayout(sl_contentPanel);
      
      JLabel lblNewLabel = new JLabel("SSID");
      sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNewLabel, 62, SpringLayout.NORTH, contentPanel);
      sl_contentPanel.putConstraint(SpringLayout.EAST, lblNewLabel, -299, SpringLayout.EAST, contentPanel);
      contentPanel.add(lblNewLabel);
      
      textField = new JTextField();
      sl_contentPanel.putConstraint(SpringLayout.NORTH, textField, -3, SpringLayout.NORTH, lblNewLabel);
      sl_contentPanel.putConstraint(SpringLayout.WEST, textField, 16, SpringLayout.EAST, lblNewLabel);
      sl_contentPanel.putConstraint(SpringLayout.EAST, textField, -114, SpringLayout.EAST, contentPanel);
      contentPanel.add(textField);
      textField.setColumns(10);
      
      textField_1 = new JTextField();
      sl_contentPanel.putConstraint(SpringLayout.NORTH, textField_1, 18, SpringLayout.SOUTH, textField);
      sl_contentPanel.putConstraint(SpringLayout.WEST, textField_1, 0, SpringLayout.WEST, textField);
      sl_contentPanel.putConstraint(SpringLayout.EAST, textField_1, -111, SpringLayout.EAST, contentPanel);
      contentPanel.add(textField_1);
      textField_1.setColumns(10);
      
      JLabel lblNewLabel_1 = new JLabel("Password");
      sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 3, SpringLayout.NORTH, textField_1);
      sl_contentPanel.putConstraint(SpringLayout.EAST, lblNewLabel_1, 0, SpringLayout.EAST, lblNewLabel);
      contentPanel.add(lblNewLabel_1);
      {
         JPanel buttonPane = new JPanel();
         buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
         getContentPane().add(buttonPane, BorderLayout.SOUTH);
         {
            JButton okButton = new JButton("OK");
            okButton.setActionCommand("OK");
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);
         }
         {
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setActionCommand("Cancel");
            buttonPane.add(cancelButton);
         }
      }
   }
}
