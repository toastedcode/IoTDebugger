package com.roboxes.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class IndicatorLight extends JLabel
{
   public enum State
   {
     UNDETECTED,
     DETECTED,
     CONNECTED
   }
   
   public IndicatorLight()
   {
      super();
      
      setState(State.UNDETECTED);
   }
   
   public void setState(State state)
   {
      switch (state)
      {
         case DETECTED:
         {
            setIcon(yellowLight);
            break;
         }
         
         case CONNECTED:
         {
            setIcon(greenLight);
            break;
         }

         case UNDETECTED:
         default:
         {
            setIcon(redLight);
            break;
         }
      }
   }
   
   private ImageIcon redLight = new ImageIcon("C:\\Users\\jtost\\Desktop\\red_light_30x30.png");
   private ImageIcon yellowLight = new ImageIcon("C:\\Users\\jtost\\Desktop\\yellow_light_30x30.png");
   private ImageIcon greenLight = new ImageIcon("C:\\Users\\jtost\\Desktop\\green_light_30x30.png");
}
