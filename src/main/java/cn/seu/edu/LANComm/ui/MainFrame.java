package cn.seu.edu.LANComm.ui;


import javax.swing.*;
import java.awt.*;

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

    /**
     * 设置最优尺寸
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
        super.add(CommunicationModeSelectorAndParameterSettingPart.createCommunicationModeSelectorPanel("LANComm.proerties"));
        // 通信状态部分
        super.add(CommunicationStatusPart.createStatusPanel());
        // 通信收发确认
        super.add(CommunicationTXRxSelectorPart.createCommunicationTXRxSelectorPanel());
        super.pack();
    }

}
