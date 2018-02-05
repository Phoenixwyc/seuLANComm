package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.communication.util.MACStringConvertor;
import cn.seu.edu.LANComm.communication.util.NetworkInterfaceUtil;
import cn.seu.edu.LANComm.util.ExtendStringToSameLength;
import cn.seu.edu.LANComm.util.FontEnum;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/4.
 */
public class MACExchangeDialog {
    private static final String TXMAC_LABEL = "发端方 MAC";
    private static final String RXMAX_LABEL = "接收方 MAC";
    private static final String LOCAL_MAC_LABEL = "上位机 MAC";
    private static final String COMFIRM = "确 认";
    private static final String MAC_TIPS = "十六进制MAC格式:XX-XX-XX-XX-XX-XX 无空格";
    private static final String MAINFRAME_TITLE = "MAC输入提示";
    private static final int DEFAULT_WIDTH = 350;
    private static final int DEFAULT_HEIGHT = 170;

    public static void showDialog(UIParameterCollector collector) {
        // 字符串转为相同长度
        java.util.List<String> temp = new ArrayList<>();
        temp.add(TXMAC_LABEL);
        temp.add(RXMAX_LABEL);
        temp.add(LOCAL_MAC_LABEL);
        List<String> labels = ExtendStringToSameLength.extendString(temp);
        // MAC地址交换主界面
        JFrame mainFrame = new JFrame();
        JPanel mainPanel = new JPanel(new GridLayout(5, 1));
        mainPanel.setBackground(Color.WHITE);

        // MAC输入提示
        JLabel tips = new JLabel(MAC_TIPS);
        tips.setBackground(Color.WHITE);
        tips.setFont(FontEnum.LABEL_FONT.getFont());
        JPanel tipsPanel = new JPanel(new GridLayout(1,1));
        tipsPanel.setBackground(Color.WHITE);
        tipsPanel.add(tips);

        // 发射端MAC交换
        JLabel Tx = new JLabel(labels.get(0));
        Tx.setBackground(Color.WHITE);
        Tx.setFont(FontEnum.LABEL_FONT.getFont());
        JTextField TxText = new JTextField(20);
        TxText.setBackground(Color.WHITE);
        TxText.setFont(FontEnum.TEXTFIELD_FONT.getFont());
        TxText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!MACStringConvertor.checkMAC(TxText.getText().trim().toUpperCase())) {
                    showErrorDialog("发射端MAC地址输入错误\n" + MAC_TIPS);
                }
            }
        });
        JPanel TxPanel = new JPanel(new GridLayout(1,2));
        TxPanel.setBackground(Color.WHITE);
        TxPanel.add(Tx);
        TxPanel.add(TxText);

        // 接收端MAC交换
        JLabel Rx = new JLabel(labels.get(1));
        Rx.setBackground(Color.WHITE);
        Rx.setBackground(Color.WHITE);
        Rx.setFont(FontEnum.LABEL_FONT.getFont());
        JTextField RxText = new JTextField(20);
        RxText.setBackground(Color.WHITE);
        RxText.setFont(FontEnum.TEXTFIELD_FONT.getFont());
        RxText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!MACStringConvertor.checkMAC(RxText.getText().trim().toUpperCase())) {
                    showErrorDialog("接收端MAC地址输入错误\n" + MAC_TIPS);
                }
            }
        });
        JPanel RxPanel = new JPanel(new GridLayout(1,2));
        RxPanel.setBackground(Color.WHITE);
        RxPanel.add(Rx);
        RxPanel.add(RxText);

        // 本地MAC交换
        JLabel local = new JLabel(labels.get(2));
        local.setBackground(Color.WHITE);
        local.setFont(FontEnum.LABEL_FONT.getFont());
        JComboBox<String> localMACList = new JComboBox<>();
        localMACList.setBackground(Color.WHITE);
        localMACList.setFont(FontEnum.COMBOBOX_ITEM_FONT.getFont());
        localMACList.setEditable(false);
        String[] macStrings = NetworkInterfaceUtil.getAllMACAddress();
        for (int index = 0; index < macStrings.length; index++) {
            localMACList.addItem(macStrings[index]);
        }
        localMACList.setSelectedIndex(0);
        collector.setLocalMAC(macStrings[0]);
        localMACList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                collector.setLocalMAC(localMACList.getItemAt(localMACList.getSelectedIndex()));
            }
        });
        JPanel localPanel = new JPanel(new GridLayout(1,2));
        localPanel.setBackground(Color.WHITE);
        localPanel.add(local);
        localPanel.add(localMACList);

        // 确认按钮
        JButton confirm = new JButton(COMFIRM);
        confirm.setBackground(Color.WHITE);
        confirm.setFont(FontEnum.BUTTON_FONT.getFont());
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String TxMACString = TxText.getText().trim().toUpperCase();
                String RxMACString = RxText.getText().trim().toUpperCase();
                if (MACStringConvertor.checkMAC(TxMACString) && MACStringConvertor.checkMAC(RxMACString)) {
                    collector.setTxMAC(TxMACString);
                    collector.setRxMAC(RxMACString);
                    collector.setLocalMAC(localMACList.getItemAt(localMACList.getSelectedIndex()));
                    // TODO: 2018/2/5 增加MAC地址的连接测试功能，保证MAC设置是可靠的
                    mainFrame.dispose();
                    System.out.println(collector);
                }else {
                    showErrorDialog("MAC参数设置错误");
                }
            }
        });
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmPanel.setBackground(Color.WHITE);
        confirmPanel.add(confirm);

        mainPanel.add(tipsPanel);
        mainPanel.add(TxPanel);
        mainPanel.add(RxPanel);
        mainPanel.add(localPanel);
        mainPanel.add(confirmPanel);
        mainPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        mainFrame.add(mainPanel);
        mainFrame.setTitle(MAINFRAME_TITLE);
        mainFrame.setVisible(true);
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        // 禁用关闭按钮
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });
    }

    private static void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage,"错误", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        UIParameterCollector collector = new UIParameterCollector();
        MACExchangeDialog.showDialog(collector);
    }
}
