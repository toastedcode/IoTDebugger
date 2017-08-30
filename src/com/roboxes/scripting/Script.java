package com.roboxes.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import bsh.EvalError;
import bsh.Interpreter;

public class Script
{
   public enum Function
   {
      // Java style
      MAIN("main"),
      // Arduiono Style
      SETUP("setup"),
      LOOP("loop");
      
      private Function(String callString)
      {
         CALL_STRING = callString;
      }
      
      public String getCallString()
      {
         return (CALL_STRING);
      }
      
      private final String CALL_STRING;
   }
   
   public static class Parameter
   {
      public Parameter(
         String name,
         Object value)
      {
         NAME = name;
         VALUE = value;
      }
      
      public String getName()
      {
         return (NAME);
      }
      
      public Object getValue()
      {
         return (VALUE);
      }
      
      private final String NAME;
      
      private final Object VALUE;
   }
   
   public Script()
   {
   }
   
   public Script(
      File file)
   {
      try
      {
         interpreter.source(file.getAbsolutePath());
         
         isValid = true;
      } 
      catch (FileNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (EvalError e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void clear()
   {
      try
      {      
         interpreter.eval("clear()");
      }
      catch (EvalError e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();         
      }
   }
   
   public boolean interpret(
      String code)
   {
      try
      {
         interpreter.eval(code);
         isValid = true;
      }
      catch (EvalError e)
      {
         // TODO: Handle evaluation errors.
         System.out.format("Eval error: %s\n", e.toString());     
      }
      
      return (isValid);
   }
   
   public boolean execute(
      String functionName,
      Script.Parameter ... parameters)
   {
      boolean success = false;
      
      try
      {
         // Get the call string.
         String callString = functionName + "();";
         
         // Set parameters.
         for (Script.Parameter parameter : parameters)
         {
            interpreter.set(parameter.getName(),  parameter.getValue());
         }
         
         // Evaluate.
         interpreter.eval(callString);
         
         success = true;
      }
      catch (EvalError e)
      {
         // TODO: Handle evaluation errors.
         System.out.format("Eval error: %s\n", e.toString());
      }
      
      return (success);
   }
   
   public boolean execute(
      Script.Function function,
      Script.Parameter ... parameters)
   {
      return (execute(function.getCallString(), parameters));
   }
   
   private boolean isValid = false;
   
   private Interpreter interpreter = new Interpreter();
}
