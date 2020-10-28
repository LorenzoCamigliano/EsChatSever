import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private String ipServer = "localhost";
    private int portaServer = 6789;
    private Socket socket;
    private BufferedReader tastiera;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    
    public void connect()
    {
        System.out.println("CLIENT: partito in esecuzione");
        try
        {
            tastiera = new BufferedReader(new InputStreamReader(System.in));
            socket = new Socket(ipServer, portaServer);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (ConnectException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Socket occupato, riprova più tardi");
            System.exit(1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    public void talk()
    {
        try
        {
            System.out.println("Benvenuto! Inserisci il tuo nome");
            System.out.println("Inserisci il tuo nome");
            outputStream.writeBytes(tastiera.readLine() + "\n");
            System.out.println("Adesso puoi iniziare a chattare (scrivi FINE se vuoi terminare la conversazione)");

            ThreadIn receiverThread = new ThreadIn(this);
            ThreadOut senderThread = new ThreadOut(this);
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Errore durante la comunicazione con il server");
            System.exit(1);
        }
    }
    
    public void close()
    {
        try
        {
            System.out.println("CLIENT: termina elaborazione e chiude connessione");
            socket.close();
            System.exit(0);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

class ThreadOut extends Thread
    {
        private Client parent;
        
        public ThreadOut(Client parent)
        {
            this.parent = parent;
            start();
        }
        
        @Override
        public void run() 
        {
            try
            {    
                while(true)
                {
                    String msg = tastiera.readLine();
                    if(msg.equals("FINE"))
                    {
                        outputStream.writeBytes(msg + "\n");
                        break;
                    }

                    System.out.println("IO: " + msg);
                    outputStream.writeBytes(msg + "\n");
                }
                close();
            } 
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
}

class ThreadIn extends Thread
    {
        private Client parent;
        public ThreadIn(Client parent)
        {
            this.parent = parent;
            start();
        }
        @Override
        public void run() 
        {
            try
            {    
                while(true)
                {
                    String msg = inputStream.readLine();
                    System.out.println(msg);
                }
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
        Client client = new Client();
        client.connect();
        client.talk();
    }
}