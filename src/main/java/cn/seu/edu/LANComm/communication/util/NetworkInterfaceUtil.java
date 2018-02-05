package cn.seu.edu.LANComm.communication.util;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

/**
 * Created by Administrator on 2018/2/4.
 */
public class NetworkInterfaceUtil {

    /**
     * 获取本地所有的MAC 地址
     * @return
     */
    public static String[] getAllMACAddress() {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        String[] macStrings = new String[devices.length];
        for (int index = 0; index < devices.length; index++) {
            macStrings[index] = MACStringConvertor.macToString(devices[index].mac_address);
        }
        return macStrings;
    }
}
