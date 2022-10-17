import java.net.*;
import java.io.*;

public class Client {
    private ServerSocket m_ServerSocket = null;
    private Socket m_Socket = null;

    private String m_Protocol = "Yan";
    private InputStreamReader isr = null;

    DataInputStream in = null;
    private  DataOutputStream m_Out = null;

    private BufferedReader m_Response = null;
    private PrintWriter m_Request = null;

    private boolean m_Running = true;

    public Client(String address, int port)
    {
        try
        {
            //ouvre le socket pour ce connecter au serveur
            m_Socket = new Socket(address, port);
            System.out.println("Connecter");

            //initialise le in et le out
            in = new DataInputStream(System.in);

            m_Out = new DataOutputStream(m_Socket.getOutputStream());

            isr = new InputStreamReader(m_Socket.getInputStream());
            m_Response = new BufferedReader(isr);

            m_Request = new PrintWriter(m_Socket.getOutputStream(), true);

            //m_Request.print("GET / HTTP/1.0\r\n");
            //m_Request.print("Host: jmarshall.com\r\n\r\n");

            //m_Request.flush();

            //tant que la commande close n'est pas appeler
            while(m_Running)
            {
                try
                {
                    String cmd = "";
                    System.out.println("commande HELP recevoir les info");
                    System.out.print("entrer le type de commande : ");
                    cmd = in.readLine();

                    CommandeSend(cmd, address);

                    if(!m_Running)
                    {
                        break;
                    }

                    m_Request.flush();

                    System.out.println();

                    String responseLine = "";
                    while(!(responseLine = m_Response.readLine()).equals("end"))
                    {
                        System.out.println(responseLine);
                    }
                    System.out.println();
                }
                catch (IOException i)
                {
                    System.out.println(i.toString());
                    break;
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


    void CommandeSend(String cmd, String host)
    {
        String path = "";
        try
        {
            switch (cmd)
            {
                case "GET":
                    System.out.print("entrer le path : \\");
                    path = in.readLine();

                    m_Request.print("GET /" + path + " HTTP/1.1\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");

                    break;
                case "HEAD":
                    System.out.print("entrer le path : \\");
                    path = in.readLine();

                    m_Request.print("HEAD /" + path + " HTTP/1.1\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");

                    break;
                case "PUT":
                    System.out.print("entrer le path : \\");
                    path = in.readLine();
                    System.out.print("entrer le body : " );
                    String body = in.readLine();

                    m_Request.print("PUT /" + path + " HTTP/1.1\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("Content-type: text.txt\r\n");
                    m_Request.print("Content-length: " + body.length() + "\r\n\r\n");
                    m_Request.print("<p>" + body + "</p>\r\n");
                    m_Request.print("end\r\n");
                    break;
                case "DELETE":
                    System.out.print("entrer le path : \\");
                    path = in.readLine();

                    m_Request.print("DELETE /" + path + " HTTP/1.1\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");

                    break;
                case "HELP":
                    m_Request.print("HELP /" + path + " HTTP/1.1\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");
                    break;
                case "CLOSE":
                    m_Request.print("CLOSE");
                    m_Running = false;
                    break;
                default:
                    System.out.println("commande non valide");
                    m_Running = false;
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
