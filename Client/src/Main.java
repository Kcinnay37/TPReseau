import java.io.IOException;
import java.net.*;

public class Main {
    public static void main(String[] argv)
    {
        try
        {
            InetAddress ip = InetAddress.getByName("jojobizarre.hopto.org");
            //Client c = new Client(ip.getHostAddress(), 80);
            //.out.println(ip.getHostAddress());
            //"206.41.87.6"
            Client c = new Client("206.41.87.6", 10080);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
