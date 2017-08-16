package com.roboxes.robox;

import java.net.InetAddress;
import java.util.Arrays;

public class RoboxInfo
{
   public RoboxInfo()
   {
      
   }
   
   public RoboxInfo(String deviceId, InetAddress address, byte[] macAddress)
   {
      this.deviceId = deviceId;
      this.address = address;
      this.macAddress = macAddress;
   }
   
   public boolean equals(Object rhs)
   {
      return ((rhs instanceof RoboxInfo) && 
              (deviceId.equals(((RoboxInfo)rhs).deviceId)) &&
              (address.equals(((RoboxInfo)rhs).address)) &&
              (Arrays.equals(macAddress, ((RoboxInfo)rhs).macAddress)));
   }
   
   @Override
   public int hashCode()
   {
       return deviceId.hashCode() + address.hashCode() + address.hashCode();
   }
   
   public String deviceId;
   
   public InetAddress address;
   
   public byte[] macAddress;
}
