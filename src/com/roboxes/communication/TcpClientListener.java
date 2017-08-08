package com.roboxes.communication;

public interface TcpClientListener extends DataListener
{
   void onConnected();
   
   void onDisconnected();
   
   void onConnectionFailure();
}
