import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

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

            String cmd = "";
            //tant que la commande close n'est pas appeler
            while(m_Running)
            {
                try
                {
                    System.out.println("commande HELP recevoir les info");
                    System.out.print("entrer le type de commande : ");
                    cmd = in.readLine();

                    CommandeReceve(cmd);

                    if(!m_Running)
                    {
                        break;
                    }

                    //methode avec just socket
                    //m_Out.write("GET / HTTP/1.0\r\n".getBytes("ISO-8859-1"));
                    //m_Out.write("HOST: 192.168.2.26\r\n\r\n".getBytes("ISO-8859-1"));
                    //m_Out.write("end\r\n".getBytes("ISO-8859-1"));

                    //requete GET
                    //m_Request.print("GET / HTTP/1.0\r\n");

                    //requete HEAD
                    //m_Request.print("HEAD / HTTP/1.0\r\n");

                    //requete PUT
                    //m_Request.print("PUT / HTTP/1.0\r\n");

                    //requete DELETE
                    //m_Request.print("DELETE / HTTP/1.0\r\n");

                    //definition du HOST
                    //m_Request.print("HOST: robdangero.us\r\n\r\n");

                    //requete PUT
                    //m_Request.print("Content-type: text/html\r\n");
                    //m_Request.print("Content-length: 16\r\n\r\n");
                    //m_Request.print("<p>New File</p>\r\n");

                    //m_Request.print("end\r\n");

                    m_Request.flush();

                    String responseLine = "";
                    while((responseLine = m_Response.readLine()) != null)
                    {
                        System.out.println(responseLine);
                    }
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


    void CommandeReceve(String cmd)
    {
        String path = "";
        String host = "";
        try
        {
            switch (cmd)
            {
                case "GET":
                    System.out.print("entrer le path : /");
                    path = in.readLine();
                    System.out.print("entrer le host: ");
                    host = in.readLine();

                    m_Request.print("GET /" + path + " HTTP/1.0\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    //m_Request.print("end\r\n");

                    break;
                case "HEAD":
                    System.out.print("entrer le path : /");
                    path = in.readLine();
                    System.out.print("entrer le host: ");
                    host = in.readLine();

                    m_Request.print("HEAD /" + path + " HTTP/1.0\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");

                    break;
                case "PUT":
                    System.out.print("entrer le path : /");
                    path = in.readLine();
                    System.out.print("entrer le host: ");
                    host = in.readLine();
                    System.out.print("entrer le content-type: ");
                    String contentType = in.readLine();
                    System.out.print("entrer le body : " );
                    String body = in.readLine();

                    m_Request.print("PUT /" + path + " HTTP/1.0\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("Content-type: " + contentType + "\r\n");
                    m_Request.print("Content-length: " + body.length() + "\r\n\r\n");
                    m_Request.print("<p>" + body + "</p>\r\n");
                    m_Request.print("end\r\n");
                    break;
                case "DELETE":
                    System.out.print("entrer le path : /");
                    path = in.readLine();
                    System.out.print("entrer le host: ");
                    host = in.readLine();

                    m_Request.print("DELETE /" + path + " HTTP/1.0\r\n");
                    m_Request.print("HOST: " + host + "\r\n\r\n");
                    m_Request.print("end\r\n");

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
