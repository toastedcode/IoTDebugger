package com.roboxes.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.roboxes.robox.Robox;

@SuppressWarnings("serial")
public class DPadGui extends JPanel
{
   private static final Dimension BUTTON_DIMENSION = new Dimension(50, 50);
   
   public DPadGui(Robox robox)
   {
      this.robox = robox;
      
      createGui();      
   }
   
   private void createGui()
   {
      // Absolute layout.
      setLayout(new GridLayout(3, 3));
      
      add(Box.createHorizontalStrut((int)BUTTON_DIMENSION.getWidth()));
      
      JButton button = new JButton("Up");
      button.setSize(BUTTON_DIMENSION);
      button.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent arg0) {}

         @Override
         public void mouseEntered(MouseEvent arg0) {}

         @Override
         public void mouseExited(MouseEvent arg0) {}

         @Override
         public void mousePressed(MouseEvent arg0)
         {
            // Forward.
            //robox.drive(speed);
         }

         @Override
         public void mouseReleased(MouseEvent arg0)
         {
            //robox.drive(0);  
         }
         
      });
      add(button);
      
      add(Box.createHorizontalStrut((int)BUTTON_DIMENSION.getWidth()));
      
      button = new JButton("Left");
      button.setSize(BUTTON_DIMENSION);
      button.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent arg0) {}

         @Override
         public void mouseEntered(MouseEvent arg0) {}

         @Override
         public void mouseExited(MouseEvent arg0) {}

         @Override
         public void mousePressed(MouseEvent arg0)
         {
            // Rotate left.
            //robox.rotate(-speed);
         }

         @Override
         public void mouseReleased(MouseEvent arg0)
         {
            //robox.rotate(0);
         }
         
      });
      add(button);
      
      add(Box.createHorizontalStrut((int)BUTTON_DIMENSION.getWidth()));
      
      button = new JButton("Right");
      button.setSize(BUTTON_DIMENSION);
      button.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent arg0) {}

         @Override
         public void mouseEntered(MouseEvent arg0) {}

         @Override
         public void mouseExited(MouseEvent arg0) {}

         @Override
         public void mousePressed(MouseEvent arg0)
         {
            // Rotate right.
            //robox.rotate(speed);
         }

         @Override
         public void mouseReleased(MouseEvent arg0)
         {
            //robox.rotate(0); 
         }
         
      });
      add(button);
      
      add(Box.createHorizontalStrut((int)BUTTON_DIMENSION.getWidth()));
      
      button = new JButton("Down");
      button.setSize(BUTTON_DIMENSION);
      button.addMouseListener(new MouseListener()
      {
         @Override
         public void mouseClicked(MouseEvent arg0) {}

         @Override
         public void mouseEntered(MouseEvent arg0) {}

         @Override
         public void mouseExited(MouseEvent arg0) {}

         @Override
         public void mousePressed(MouseEvent arg0)
         {
            // Reverse
            // robox.drive(-speed);
         }

         @Override
         public void mouseReleased(MouseEvent arg0)
         {
            // robox.drive(0); 
         }
         
      });
      add(button);
      
      add(Box.createHorizontalStrut((int)BUTTON_DIMENSION.getWidth()));
   }
   
   private Robox robox;
   
   int speed = 100;
}
