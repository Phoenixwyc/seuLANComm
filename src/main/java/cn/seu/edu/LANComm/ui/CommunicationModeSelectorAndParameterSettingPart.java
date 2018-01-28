package cn.seu.edu.LANComm.ui;

import cn.seu.edu.LANComm.util.CommunicationModeEnum;
import cn.seu.edu.LANComm.util.ExtendStringToSameLength;
import cn.seu.edu.LANComm.util.FontEnum;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * 这里绘制工作模式部分
 * Created by Administrator on 2018/1/26.
 * @author WYCPhoenix
 * @date 2018-1-27:41
 *
 */
public class CommunicationModeSelectorAndParameterSettingPart {
    private static String selectedMode;
    private static JPanel parameterSettingPart = new JPanel();
    /**
     * 主panel网格布局参数
     */
    private static final int DEFAULT_GRID_ROWS = 6;
    private static final int DEFAULT_GRID_COLUMN = 1;
    /**
     * 主 panel 默认大小
     */
    private static final int DEFAULT_PANEL_WIDTH = 200;
    private static final int DEFAULT_PANEL_HEIGHT = 300;

    public static JPanel createCommunicationModeSelectorPanel(String propertyPath) {
        // 模式选择 + 参数设置的panel
        JPanel combinedPanel = new JPanel();
        combinedPanel.setBackground(Color.WHITE);
        combinedPanel.setLayout(new FlowLayout());

        // 通信模式选择UI部分
        // 模式选择的panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(DEFAULT_GRID_ROWS, DEFAULT_GRID_COLUMN));
        panel.setBackground(Color.WHITE);
        // 单选按钮
        ButtonGroup buttonGroup = new ButtonGroup();
        // 为了便于布局，将所有字符串变为相同长度
        List<String> supportedModeName = ExtendStringToSameLength.extendString(getSupportedMode());
        for (int i = 0; i < supportedModeName.size(); i++) {
            // 默认选中第一个
            if (i == 0) {
                JRadioButton radioButton = new JRadioButton(supportedModeName.get(i), true);
                radioButton.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
                radioButton.setActionCommand(supportedModeName.get(i));
                selectedMode = radioButton.getActionCommand().trim();
                radioButton.setBackground(Color.WHITE);
                // 专门用于参数传输
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO: 2018/1/28 增加工作模式选择参数传递
                        System.out.println(radioButton.getText().trim() + " " + radioButton.getActionCommand());
                    }
                });
                // 专门用于参数更新
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedMode = radioButton.getActionCommand().trim();
                        combinedPanel.remove(1);
                        JPanel panelToUpdate = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(
                                                                            selectedMode, propertyPath);
                        combinedPanel.add(panelToUpdate);
                        combinedPanel.paintAll(combinedPanel.getGraphics());

                    }
                });
                buttonGroup.add(radioButton);
                panel.add(radioButton);
            } else {
                JRadioButton radioButton = new JRadioButton(supportedModeName.get(i), false);
                radioButton.setFont(FontEnum.RADIOBUTTOBN_FONT.getFont());
                radioButton.setActionCommand(supportedModeName.get(i));
                selectedMode = radioButton.getActionCommand().trim();
                radioButton.setBackground(Color.WHITE);
                // 专门用于参数传输
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // TODO: 2018/1/28 增加工作模式选择参数传递
                        selectedMode = radioButton.getActionCommand().trim();
                        parameterSettingPart.paint(parameterSettingPart.getGraphics());
                        System.out.println(radioButton.getText().trim() + " " + radioButton.getActionCommand());
                    }
                });
                // 专门用于参数设置panel的更新
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedMode = radioButton.getActionCommand().trim();
                        combinedPanel.remove(1);
                        JPanel panelToUpdate = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(
                                selectedMode, propertyPath);
                        combinedPanel.add(panelToUpdate);
                        combinedPanel.paintAll(combinedPanel.getGraphics());
                    }
                });
                buttonGroup.add(radioButton);
                panel.add(radioButton);
            }
        }

        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                                            "模式选择", TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION,
                                            FontEnum.BORDER_TITLE_FONT.getFont());
        panel.setBorder(titledBorder);
        panel.setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));

        // 参数设置的panel
        JPanel parameterSettingPanel = CommunicationParameterSettingPart.createCommunicationParameterSettingPanel(selectedMode, propertyPath);
        combinedPanel.add(panel);
        combinedPanel.add(parameterSettingPanel);
        return combinedPanel;
    }

    /**
     * 从CommunicationModeEnum中获取所有支持的模式
     * 模式顺序为：模式名的自然顺序
     * @return
     */
    private static List<String> getSupportedMode() {
        Set<CommunicationModeEnum> communicationModeEnums = EnumSet.allOf(CommunicationModeEnum.class);
        // 按字符自然顺序排序
        List<String> supportedModeName = new ArrayList<>();
        Iterator<CommunicationModeEnum> iterator = communicationModeEnums.iterator();
        while (iterator.hasNext()) {
            CommunicationModeEnum now = iterator.next();
            supportedModeName.add(now.getCommunicationMode());
        }
        Collections.sort(supportedModeName);
        return supportedModeName;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.add(createCommunicationModeSelectorPanel("LANComm.proerties"));
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void setParameterSettingPart(JPanel parameterSettingPart) {
        CommunicationModeSelectorAndParameterSettingPart.parameterSettingPart = parameterSettingPart;
    }
}
