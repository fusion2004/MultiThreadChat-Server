import java.io.*;
import java.net.*;


// This client thread opens the input and the output streams for a particular client,
// ask the client's name, informs all the clients currently connected to the
// server about the fact that a new client has joined the chat room,
// and as long as it receive data, echos that data back to all other clients.
// When the client leaves the chat room this thread informs also all the
// clients about that and terminates.
public class MultiThreadChatServer{

    // Declaration section:
    // declare a server socket and a client socket for the server
    // declare an input and an output stream

    static  Socket clientSocket = null;
    static  ServerSocket serverSocket = null;

    // This chat server can accept up to 10 clients' connections

    static  clientThread t[] = new clientThread[10];

    public static void main(String args[]) {

	// The default port

	int port_number=7777;

	if (args.length < 1)
	    {
		System.out.println("Usage: java MultiThreadChatServer \n"+
				   "Now using port number="+port_number);
	    } else {
		port_number=Integer.valueOf(args[0]).intValue();
	    }

	// Initialization section:
	// Try to open a server socket on port port_number (default 2222)
	// Note that we can't choose a port less than 1023 if we are not
	// privileged users (root)

        try {
	    serverSocket = new ServerSocket(port_number);
        }
        catch (IOException e)
	    {System.out.println(e);}

	// Create a socket object from the ServerSocket to listen and accept
	// connections.
	// Open input and output streams for this socket will be created in
	// client's thread since every client is served by the server in
	// an individual thread

	while(true){
	    try {
		clientSocket = serverSocket.accept();
		for(int i=0; i<=9; i++){
		    if(t[i]==null)
			{
			    (t[i] = new clientThread(clientSocket,t)).start();
			    break;
			}
		}
	    }
	    catch (IOException e) {
		System.out.println(e);}
	}
    }
}

class clientThread extends Thread{

    DataInputStream is = null;
    PrintStream os = null;
    Socket clientSocket = null;
    clientThread t[];

    public clientThread(Socket clientSocket, clientThread[] t){
	this.clientSocket=clientSocket;
        this.t=t;
    }

    public void run()
    {
	String line;
        String name;
        Boolean ragequit = false;
	try{
	    is = new DataInputStream(clientSocket.getInputStream());
	    os = new PrintStream(clientSocket.getOutputStream());
	    os.println("Enter your username:");
	    name = is.readLine();
	    os.println("Hello "+name+" to our chat room.\nTo leave enter /quit in a new line");
	    for(int i=0; i<=9; i++)
		if (t[i]!=null && t[i]!=this)
		    t[i].os.println("*** A new user "+name+" entered the chat room !!! ***" );
	    while (true) {
		line = is.readLine();
                if(line.startsWith("/quit")) break;
                else if(line.startsWith("/ragequit")) {
                    ragequit = true;
                    break;
                }
		for(int i=0; i<=9; i++)
		    if (t[i]!=null)  t[i].os.println("<"+name+"> "+line);
	    }
	    for(int i=0; i<=9; i++)
		if (t[i]!=null && t[i]!=this) {
                    if(ragequit) {
                        t[i].os.println("*** The user "+name+" is RAGEQUITTING!11!!1!ONE1!!1 ***" );
                    } else {
                        t[i].os.println("*** The user "+name+" is leaving the chat room !!! ***" );
                    }
                }

	    if(ragequit)
                os.println("*** RAAAAAAAAAAAAAAAAAAAAAAAAAAAAGE ***");
            else
                os.println("*** Bye "+name+" ***");

            os.println("/quit");
	    // Clean up:
	    // Set to null the current thread variable such that other client could
	    // be accepted by the server

	    for(int i=0; i<=9; i++)
		if (t[i]==this) t[i]=null;

	    // close the output stream
	    // close the input stream
	    // close the socket

	    is.close();
	    os.close();
	    clientSocket.close();
	}
	catch(IOException e){};
    }
}