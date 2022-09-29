import java.net.*;
import java.io.*;

public class Client {
    private ServerSocket m_ServerSocket = null;
    private Socket m_Socket = null;

    private String m_Protocol = "Yan";
    private InputStreamReader isr = null;

    private BufferedReader m_Response = null;
    private PrintWriter m_Request = null;


    public Client(String address, int port)
    {
        try
        {
            //ouvre le socket pour ce connecter au serveur
            m_Socket = new Socket(address, port);
            System.out.println("Connecter");

            //initialise le in et le out
            DataInputStream in = new DataInputStream(System.in);

            isr = new InputStreamReader(m_Socket.getInputStream());
            m_Response = new BufferedReader(isr);

            m_Request = new PrintWriter(m_Socket.getOutputStream(), true);

            //m_Request.print("GET / HTTP/1.0\r\n");
            //m_Request.print("Host: jmarshall.com\r\n\r\n");

            //m_Request.flush();

            String cmd = "";
            //tant que la commande close n'est pas appeler
            while(!cmd.equals("close"))
            {
                try
                {
                    cmd = in.readLine();

                    //requete GET
                    m_Request.print("GET / HTTP/1.0\r\n");

                    //requete HEAD
                    //m_Request.print("HEAD / HTTP/1.0\r\n");

                    //requete PUT
                    //m_Request.print("PUT / HTTP/1.0\r\n");

                    //requete DELETE
                    //m_Request.print("DELETE / HTTP/1.0\r\n");

                    //definition du HOST
                    m_Request.print("HOST: 127.0.0.1\r\n\r\n");

                    //requete PUT
                    //m_Request.print("Content-type: text/html\r\n");
                    //m_Request.print("Content-length: 16\r\n\r\n");
                    //m_Request.print("<p>New File</p>\r\n");

                    m_Request.flush();

                    String responseLine = "";
                    while((responseLine = m_Response.readLine()) != null)
                    {
                        System.out.println(responseLine);
                    }
                    System.out.println("test");
                }
                catch (IOException i)
                {
                    System.out.println(i.toString());
                    //break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        try
        {
            m_Request.close();
            m_Response.close();
            isr.close();

            m_Socket.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
