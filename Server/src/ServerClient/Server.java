package ServerClient;
import java.net.*;
import java.io.*;

public class Server
{
    //recoi et envoi les info
    private Socket m_Socket = null;

    //le socket qui est ouvert qui permet douvrir la connection et permettre decouter si quelqun
    //veux ce connecter a la connection c'est aussi lui qui va accepter si quelqun demande de ce connecter
    private ServerSocket m_Server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private InputStreamReader isr = null;
    private BufferedReader m_Response = null;
    private PrintWriter m_Request = null;

    private Boolean m_RequestEnd = false;

    public Server(int port)
    {
        try
        {
            //ouvre un serveur socket qui ecouteras pour une conection extern
            m_Server = new ServerSocket(port);

            System.out.println("En attente d'une connection");
            //block le code jusqua une connection extern sois effectuer
            //et retourne le socket qui permetera la connection client serveur
            m_Socket = m_Server.accept();
            System.out.println("Client connecter");

            isr = new InputStreamReader(m_Socket.getInputStream());
            m_Response = new BufferedReader(isr);

            m_Request = new PrintWriter(m_Socket.getOutputStream(), true);

            String line = "";
            String endCondition = "close";

            while(!line.equals(endCondition))
            {
                try
                {
                    if(!m_RequestEnd)
                    {
                        String currLine = m_Response.readLine();
                        if(currLine.equals("end"))
                        {
                            m_RequestEnd = true;
                        }
                        else if (!currLine.equals(""))
                        {
                            line += currLine;
                            if(currLine.contains("HOST:") || currLine.contains("Content-length:"))
                            {
                                line += "\r\n\r\n";
                            }
                            else
                            {
                                line += "\r\n";
                            }
                        }
                    }
                    else
                    {

                        System.out.print(line);
                        System.out.print("test");
                        m_Request.print("yo\r\n");
                        m_Request.flush();
                        m_RequestEnd = false;
                    }
                }
                catch (IOException i)
                {
                    System.out.println(i);
                    break;
                }
            }
            System.out.println("Connection Fermer");

            m_Socket.close();
            in.close();
        }
        catch (IOException a)
        {
            System.out.println(a);
        }
    }
}
