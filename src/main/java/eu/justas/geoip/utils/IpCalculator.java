/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.justas.geoip.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author justas
 */
public class IpCalculator {

    public static Long getLong(String ipAddressString) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ipAddressString);

        long result = 0;
        for (byte b : inetAddress.getAddress()) {
            result = result << 8 | (b & 0xFF);
        }
        
        return result;

    }
}
