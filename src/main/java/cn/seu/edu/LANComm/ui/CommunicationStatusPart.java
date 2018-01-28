package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.FontEnum;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private static final int DEFAULT_WIDTH = 150;
    private static final int DEFAULT_HEIGHT = 300;
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 6;
    private static final int DEFAULT_GRID_COLUMN = 1;

    public static JPanel createStatusPanel() {
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
        bitErrorRate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 添加误码率显示响应事件
            }
        });

        // 正常状态显示
        JRadioButton normal = new JRadioButton(NORMAL);
        normal.setBackground(Color.WHITE);
        normal.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        normal.setFont(FontEnum.STATUS_NORMAL_FONT.getFont());

        // 故障状态显示
        JRadioButton abnormal = new JRadioButton(ABNORMAL);
        abnormal.setBackground(Color.WHITE);
        abnormal.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        abnormal.setFont(FontEnum.STATUS_ABNORMAL_FONT.getFont());
        // 添加响应事件
        normal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 增加正常工作状态设置
                normal.setForeground(Color.BLUE);
                normal.setBackground(Color.GREEN);
                abnormal.setForeground(Color.BLACK);
                abnormal.setBackground(Color.WHITE);
            }
        });

        abnormal.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 增加故障状态设置
                normal.setBackground(Color.WHITE);
                normal.setForeground(Color.BLACK);
                abnormal.setForeground(Color.ORANGE);
                abnormal.setBackground(Color.RED);
            }
        });

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

        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.add(createStatusPanel());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
