package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.FontEnum;
import javafx.beans.binding.FloatExpression;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Administrator on 2018/1/27.
 */
public class CommunicationTXRxSelectorPart {
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 6;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 收发文本显示
     */
    private static final String TRANSMIT_TEXT = "发送";
    private static final String RECEIVE_TEXT = "接收";
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 150;
    private static final int DEFAULT_PANEL_HEIGHT = 300;

    public static JPanel createCommunicationTXRxSelectorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(DEFAULT_GRID_ROWS,DEFAULT_GRID_COLUMN));

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton transmit = new JRadioButton(TRANSMIT_TEXT);
        transmit.setBackground(Color.WHITE);
        transmit.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        transmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 发送控制

                System.out.println("选择了： " + transmit.getText());
            }
        });
        buttonGroup.add(transmit);
        JPanel trasmitPanel = new JPanel();
        trasmitPanel.setBackground(Color.WHITE);
        trasmitPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        trasmitPanel.add(transmit);

        JRadioButton receive = new JRadioButton(RECEIVE_TEXT);
        receive.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
        receive.setBackground(Color.WHITE);
        receive.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 接收控制
                System.out.println("选择了：" + receive.getText());
            }
        });
        buttonGroup.add(receive);
        JPanel receivePanel = new JPanel();
        receivePanel.setBackground(Color.WHITE);
        receivePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        receivePanel.add(receive);

        JPanel confirmPanel = new JPanel();
        JButton confirm = new JButton("确认");
        confirm.setFont(FontEnum.BUTTON_FONT.getFont());
        confirm.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2018/1/28 确认事件处理
                System.out.println("按下确认按钮");
            }
        });
        confirmPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        confirmPanel.setBackground(Color.WHITE);
        confirmPanel.add(confirm);

        panel.add(trasmitPanel);
        panel.add(receivePanel);
        panel.add(confirmPanel);

        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                                            "收发确认", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                                            FontEnum.BORDER_TITLE_FONT.getFont());
        panel.setBorder(titledBorder);
        panel.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(createCommunicationTXRxSelectorPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
