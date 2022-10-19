package ServerClient;
import java.net.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Date;

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
            String endCondition = "CLOSE";

            while(!line.equals(endCondition))
            {
                try
                {
                    line = "";
                    String currLine = "";
                    while(!(currLine = m_Response.readLine()).equals("end"))
                    {
                        line += currLine;
                        if(currLine.contains("HOST:") || currLine.contains("Content-length:"))
                        {
                            line += "\r\n\r\n";
                        }
                        else if(currLine.equals(endCondition))
                        {
                            line = endCondition;
                            break;
                        }
                        else if(!currLine.equals(""))
                        {
                            line += "\r\n";
                        }
                    }

                    if(line.equals(endCondition))
                    {
                        break;
                    }

                    CMDRecive(line);

                    m_Request.flush();
                }
                catch (IOException i)
                {
                    System.out.println(i);
                    break;
                }
            }
            System.out.println("Connection Fermer");

            m_Socket.close();
            //in.close();
        }
        catch (IOException a)
        {
            System.out.println(a);
        }
    }

    public void CMDRecive(String cmd)
    {
        System.out.println("Commande:");
        System.out.print(cmd);

        String path = GetPath(cmd);

        System.out.println("path : " + path);

        if(cmd.contains("GET"))
        {
            GET(cmd, path);
        }
        else if(cmd.contains("HEAD"))
        {
            HEAD(cmd, path);
        }
        else if(cmd.contains("PUT"))
        {
            PUT(cmd, path);
        }
        else if(cmd.contains("DELETE"))
        {
            DELETE(cmd, path);
        }
        else if(cmd.contains("HELP"))
        {
            HELP();
        }

    }

    public void GET(String cmd, String path)
    {
        m_Request.print("HTTP/1.1 200 OK\r\n");

        File file = new File(path);

        if (file.exists())
        {
            Scanner scanner = null;
            try
            {
                scanner = new Scanner(file);
            }
            catch (IOException e)
            {
                System.out.println(e);
            }

            m_Request.print("File name: " + file.getName() + "\r\n");
            m_Request.print("Absolute path: " + file.getAbsolutePath() + "\r\n");
            m_Request.print("Writeable: " + file.canWrite() + "\r\n");
            m_Request.print("Readable: " + file.canRead() + "\r\n");
            m_Request.print("File size in bytes: " + file.length() + "\r\n");
            m_Request.print("Date: " + new Date() + "\r\n\r\n");

            if(scanner == null)
            {
                String[] test = file.list();
                for(int i = 0; i < test.length; i++)
                {
                    m_Request.print(test[i] + "\r\n");
                }
            }
            else
            {
                while (scanner.hasNextLine())
                {
                    String data = scanner.nextLine();
                    m_Request.print(data + "\r\n");
                }
                scanner.close();
            }

        }
        else
        {
            m_Request.print("the file does not exist\r\n");
        }
        m_Request.print("end\r\n");
    }

    public void HEAD(String cmd, String path)
    {
        m_Request.print("HTTP/1.1 200 OK\r\n");

        File file = new File(path);

        if (file.exists())
        {
            m_Request.print("File name: " + file.getName() + "\r\n");
            m_Request.print("Absolute path: " + file.getAbsolutePath() + "\r\n");
            m_Request.print("Writeable: " + file.canWrite() + "\r\n");
            m_Request.print("Readable: " + file.canRead() + "\r\n");
            m_Request.print("File size in bytes: " + file.length() + "\r\n");
            m_Request.print("Date: " + new Date() + "\r\n\r\n");
        }
        else
        {
            m_Request.print("the file does not exist\r\n");
        }
        m_Request.print("end\r\n");
    }

    public void PUT(String cmd, String path)
    {
        String body = GetBody(cmd);
        try
        {
            File file = new File(path);
            if(file.createNewFile())
            {
                m_Request.print("HTTP/1.1 201 Created\r\n");
            }
            else
            {
                m_Request.print("HTTP/1.1 204 No content\r\n");
            }
            m_Request.print("Content-Location: " + path + "\r\n");

            FileWriter writer = new FileWriter(path);
            writer.write(body);
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        m_Request.print("end\r\n");
    }

    public void DELETE(String cmd, String path)
    {
        File file = new File(path);
        if(file.delete())
        {
            m_Request.print("HTTP/1.1 200 OK\r\n");
            m_Request.print("Date: " + new Date() + "\r\n\r\n");
        }
        else
        {
            m_Request.print("HTTP/1.1 204 No Content\r\n");
            m_Request.print("Date: " + new Date() + "\r\n\r\n");
        }
        m_Request.print("end\r\n");
    }

    public void HELP()
    {
        m_Request.print("GET: sert a avoir les information d'un fichier\r\n");
        m_Request.print("HEAD: sert a avoir les information d'un fichier sans le body\r\n");
        m_Request.print("PUT: sert a cree un fichier avec ce qu'il a ecrit a l'interieur\r\n");
        m_Request.print("DELETE: sert a supprimer un fichier\r\n");
        m_Request.print("CLOSE: sert a ce deconnecter\r\n");
        m_Request.print("le path commence a l'interieur du dossier data\r\n");
        m_Request.print("end\r\n");
    }
    public String GetPath(String cmd)
    {
        String path = "";
        boolean write = false;
        int nb = 0;

        for(int i = 0; i < cmd.length(); i++)
        {
            if(cmd.charAt(i) == ' ' && nb == 1)
            {
                break;
            }

            if(write == true && nb == 1)
            {
                path += cmd.charAt(i);
            }

            if(cmd.charAt(i) == '/')
            {
                write = true;
                nb += 1;
            }
        }

        String dataPath = "";
        try
        {
            dataPath = new java.io.File(".").getCanonicalPath();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }

        dataPath += "\\data";

        path = dataPath + "\\" + path;

        return path;
    }

    public String GetBody(String cmd)
    {
        String body = "";

        for(int i = 0; i < cmd.length(); i++)
        {
            body = "";
            int index = i;
            while(cmd.charAt(index) == '\r' || cmd.charAt(index) == '\n')
            {
                index += 1;
            }

            while (cmd.charAt(index) != '\r')
            {
                body += cmd.charAt(index);
                index += 1;
            }

            if(body.contains("<p>"))
            {
                body = body.replace("<p>", "");
                body = body.replace("</p>", "");
                break;
            }
        }

        return body;
    }
}
