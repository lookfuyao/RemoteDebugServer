package com.app2.banana;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Log.d("Main Main Main");
        
        ServerThread serverThread = new ServerThread("server");
        serverThread.start();
    }

}
