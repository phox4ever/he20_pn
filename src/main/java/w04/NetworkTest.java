package w04;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetworkTest {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        /*System.out.println(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        System.out.println(InetAddress.getByName("localhost"));
        System.out.println(InetAddress.getLocalHost());
        System.out.println(InetAddress.getLoopbackAddress());
        System.out.println(InetAddress.getByName("www.google.com"));
        System.out.println(InetAddress.getByName("www.google.com").getHostAddress());
        System.out.println(InetAddress.getByName("www.google.com").getHostName());
        System.out.println(InetAddress.getByName("www.google.com").getCanonicalHostName());*/

        InetAddress me = InetAddress.getLocalHost();
        String host = me.getCanonicalHostName();
        for (InetAddress address : InetAddress.getAllByName(host)) {
            System.out.println(address);
        }

        // Get all my network interfaces and print their addresses
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                System.out.println(address + " on " + ni.getDisplayName());
            }
        }
    }
}
