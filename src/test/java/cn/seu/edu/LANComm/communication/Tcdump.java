package cn.seu.edu.LANComm.communication;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/29.
 */
public class Tcdump implements PacketReceiver{
    @Override
    public void receivePacket(Packet packet) {
        System.out.println(packet);
    }

    public static void main(String[] args) {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        try {
            JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(devices[0], 2000, false, 20);
            jpcapCaptor.loopPacket(-1, new Tcdump());
        } catch (IOException e) {

        }


    }
}
