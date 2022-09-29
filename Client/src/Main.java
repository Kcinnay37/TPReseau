import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] argv)
    {
        try
        {
            InetAddress ip = InetAddress.getByName("robdangero.us");
            //Client c = new Client(ip.getHostAddress(), 80);
            Client c = new Client("127.0.0.1", 80);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
