package cn.seu.edu.LANComm.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/26.
 * @author WYCPhoenix
 * @date 2018-1-29-15:46
 */
public class MainFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameSet frameSet = new FrameSet("实时数据显示");
                frameSet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frameSet.setBitErrorRateTextFieldString("0.000001");
                frameSet.setNormalStatus();
                frameSet.setVisible(true);
                System.out.println("主界面 " + frameSet.getUIParameterCollector());
            }
        });
    }
}

class FrameSet extends JFrame {
    UIParameterCollector collector = new UIParameterCollector();
    private JRadioButton normalStatusRadioButton;
    private JRadioButton abnormalStatusRadioButton;
    private JTextField bitErrorRateTextField;
    private JButton confirmButton;

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
        super.add(CommunicationModeSelectorAndParameterSettingPart.createCommunicationModeSelectorAndParameterSettingPanel("LANComm.proerties", collector));

        // 通信状态部分
        Map<String, Object> statusPanel = CommunicationStatusPart.createStatusPanel();
        super.add((JPanel)statusPanel.get(CommunicationStatusPart.getPanelKey()));
        normalStatusRadioButton = (JRadioButton) statusPanel.get(CommunicationStatusPart.getNormalStatusKey());
        abnormalStatusRadioButton = (JRadioButton) statusPanel.get(CommunicationStatusPart.getAbnormalStatusKey());
        bitErrorRateTextField = (JTextField) statusPanel.get(CommunicationStatusPart.getBitErrorRateTextKey());

        // 通信收发确认部分
        Map<String, Object> txRxSelectorPart = CommunicationTXRxSelectorPart.createCommunicationTXRxSelectorPanel(collector);
        super.add((JPanel)txRxSelectorPart.get(CommunicationTXRxSelectorPart.getStatusPanel()));
        confirmButton = (JButton) txRxSelectorPart.get(CommunicationTXRxSelectorPart.getConfirmButton());
        super.pack();
    }

    /**
     * 通信参数收集器
     * note：仅用于调用getter方法，禁止使用setter方法
     * @return 通信参数设置汇总
     */
    public UIParameterCollector getUIParameterCollector() {
        return collector;
    }

    /**
     *  系统正常状态
     */
    public void setNormalStatus() {
        normalStatusRadioButton.setForeground(Color.BLUE);
        normalStatusRadioButton.setBackground(Color.GREEN);
        normalStatusRadioButton.setSelected(true);
        // 重置故障状态
        abnormalStatusRadioButton.setForeground(Color.BLACK);
        abnormalStatusRadioButton.setBackground(Color.WHITE);
    }

    /**
     * 系统关故障状态
     */
    public void setAbnormalStatus() {
        abnormalStatusRadioButton.setSelected(true);
        abnormalStatusRadioButton.setBackground(Color.RED);
        abnormalStatusRadioButton.setForeground(Color.ORANGE);
        // 重置正常状态
        normalStatusRadioButton.setBackground(Color.WHITE);
        normalStatusRadioButton.setForeground(Color.BLACK);
    }

    /**
     * 设置误码率数值
     * @param bitErrorRate 误码率，字符串
     */
    public void setBitErrorRateTextFieldString(String bitErrorRate) {
        if (bitErrorRate == null) {
            bitErrorRateTextField.setText("-1");
        }
        bitErrorRateTextField.setText(bitErrorRate);

    }
}
