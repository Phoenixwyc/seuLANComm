package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.FontEnum;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by Administrator on 2018/1/27.
 */
public class CommunicationStatusPart {
    /**
     * 两种工作状态常量
     */
    private static final String NORMAL = " 正 常 ";
    private static final String ABNORMAL = " 故 障 ";
    /**
     * 状态栏的默认大小
     */
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 300;
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 6;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     *
     */
    private static final String PANEL_KEY = "StatusPanel";
    private static final String NORMAL_STATUS_KEY = "NormalRadioButon";
    private static final String ABNORMAL_STATUS_KEY = "AbnormalRadioButton";
    private static final String BIT_ERROR_RATE_TEXT_KEY = "BitErrorRateText";

    /**
     * 工作状态栏中的内容需要在运行时设置
     * 这里将内部的误码率显示、收发选择和确认按钮都暴露出去
     * @return
     */
    public static Map<String, Object> createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));
        ButtonGroup buttonGroup = new ButtonGroup();

        // 误码率显示
        JPanel bitErrorRatePanel = new JPanel();
        bitErrorRatePanel.setBackground(Color.WHITE);
        bitErrorRatePanel.setLayout(new GridLayout(1, 2));
        JLabel bitErrorRateLabel = new JLabel("误码率：");
        bitErrorRateLabel.setFont(FontEnum.LABEL_FONT.getFont());
        bitErrorRateLabel.setBackground(Color.WHITE);
        JTextField bitErrorRate = new JTextField("0.0");
        bitErrorRate.setFont(FontEnum.TEXTFIELD_FONT.getFont());
        bitErrorRate.setBackground(Color.WHITE);
        bitErrorRate.setEditable(false);
        bitErrorRatePanel.add(bitErrorRateLabel);
        bitErrorRatePanel.add(bitErrorRate);

        // 正常状态显示
        JRadioButton normal = new JRadioButton(NORMAL);
        normal.setBackground(Color.WHITE);
        normal.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        normal.setFont(FontEnum.STATUS_NORMAL_FONT.getFont());
        normal.setEnabled(false);

        // 故障状态显示
        JRadioButton abnormal = new JRadioButton(ABNORMAL);
        abnormal.setBackground(Color.WHITE);
        abnormal.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        abnormal.setFont(FontEnum.STATUS_ABNORMAL_FONT.getFont());
        abnormal.setEnabled(false);

        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                                            "工作状态", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                                            FontEnum.BORDER_TITLE_FONT.getFont());

        buttonGroup.add(normal);
        buttonGroup.add(abnormal);
        panel.add(bitErrorRatePanel);
        panel.add(normal);
        panel.add(abnormal);
        panel.setBorder(titledBorder);
        panel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        panel.setBackground(Color.WHITE);


        Map<String, Object> map = new HashMap<>();
        map.put(PANEL_KEY, panel);
        map.put(NORMAL_STATUS_KEY, normal);
        map.put(ABNORMAL_STATUS_KEY, abnormal);
        map.put(BIT_ERROR_RATE_TEXT_KEY, bitErrorRate);
        return map;
    }

    public static String getPanelKey() {
        return PANEL_KEY;
    }

    public static String getNormalStatusKey() {
        return NORMAL_STATUS_KEY;
    }

    public static String getAbnormalStatusKey() {
        return ABNORMAL_STATUS_KEY;
    }

    public static String getBitErrorRateTextKey() {
        return BIT_ERROR_RATE_TEXT_KEY;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add((JPanel)(createStatusPanel().get("StatusPanel")));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
