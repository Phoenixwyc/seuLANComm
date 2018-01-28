package cn.seu.edu.LANComm.ui;


import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.List;

/**
 * Created by Administrator on 2018/1/26.
 */
public class MainFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameSet frameSet = new FrameSet("实时数据显示");
                frameSet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frameSet.setVisible(true);
            }
        });
    }
}

class FrameSet extends JFrame {
    private static final String DEFAULT_LOOKANDFEEL_CLASSNAME = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String PREPERED_LOOKANDFEEL_CLASSNAME1 = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static final String PREPERED_LOOKANDFEEL_CLASSNAME2 = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";

    /**
     * 主显示器的像素点大小screenSize
     */
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 300;


    public FrameSet(String title) {
        // 主窗口大小
        super.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        // 主窗口居中
        super.setLocationRelativeTo(null);
        // 主窗口标题
        super.setTitle(title);
        // 主窗口大小可调
        super.setResizable(true);
        // ICON
        Image mainFrameIcon = new ImageIcon("src/main/resources/MainIcon.jpg").getImage();
        super.setIconImage(mainFrameIcon);
        super.setLayout(new FlowLayout());

        // 通信模式选择部分
        List<Object> list = CommunicationModeSelectorPart.createCommunicationModeSelectorPanel("LANComm.proerties");
        super.add((JPanel)list.get(0));
        // 通信状态部分
        super.add(CommunicationStatusPart.createStatusPanel());
        // 通信收发确认
        super.add(CommunicationTXRxSelectorPart.createCommunicationTXRxSelectorPanel());
    }

}
