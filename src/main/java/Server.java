import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket server;
    private ArrayList<ThreadServer> serverThreads;
    private int maxConnessioni;
    
    public void listen()
    {
        try
        {
            System.out.println("SERVER: partito in esecuzione");
            server = new ServerSocket(6789);
            
            serverThreads = new ArrayList<ThreadServer>();
            maxConnessioni = 2;
            
            for (int i = 0; i < maxConnessioni; i++)
                serverThreads.add(new ThreadServer(server.accept()));
            
            server.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    class ThreadServer extends Thread
    {
        private Socket client;
        private DataOutputStream outputStream;
        private BufferedReader inputStream;
        private String nomeClient;
        
        public ThreadServer(Socket s)
        {
            try 
            {
                client = s;
                outputStream = new DataOutputStream(client.getOutputStream());
                inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                start();
            } 
            catch (Exception e) 
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
       
        @Override
        public void run()
        {
            try
            {
                System.out.println("Connessione stabilita con il server");

                nomeClient = inputStream.readLine();

                while (true)
                {                        
                    String msg = inputStream.readLine();
                    if(msg.equals("FINE"))
                    {
                        for (ThreadServer s : serverThreads)
                        {
                            if(s == this)
                                continue;

                            s.outputStream.writeBytes("SERVER: " + nomeClient + " si è disconnesso\n");
                        }
                        break;
                    }

                    if(serverThreads.size() > 1)
                    {
                        for (ThreadServer s : serverThreads)
                        {
                            if(s == this)
                                continue;

                            s.outputStream.writeBytes(nomeClient + ": " + msg + "\n");
                        }
                    }
                    else
                    {
                        outputStream.writeBytes("SERVER: non c'è nessuno in linea\n");
                    }
                }
                close();
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
        
        public void close()
        {
            try
            {
                System.out.println("SERVER: connessione terminata");
                serverThreads.remove(this);
                client.close();
            } 
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    
    
    public static void main(String[] args)
    {
        Server server = new Server();
        server.listen();
    }
}
