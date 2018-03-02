## 下位机开发注意事项

1. 见src/resources目录下的配置文件
    - LANComm.properties文件配置系统工作参数
    - MAC.properties文件配置收发端MAC地址信息
2. MAC帧结构
    - 前导码、界定符、CRC为系统默认设置；
    - FrameType字段固定为8511；
    - data段结构定义，见src\main\java\cn\seu\edu\LANComm\communication\util\FramingEncode中的描述；
    - 每帧的数据含义，见src\main\java\cn\seu\edu\LANComm\communication\util\DataLinkParameterEnum中的定义；
    - 通信参数定义，见src\main\java\cn\seu\edu\LANComm\util\CommunicationParameterEnum中的定义；
    - 通信模式编码，见rc\main\java\cn\seu\edu\LANComm\util\CommunicationModeEnum中的定义
3. 数据类型，统一为**float类型**，4个字节表示，字节顺序为**Big Endian**；
4. 上位机向下位机发送参数配置信息时，各参数的顺序见src\main\java\cn\seu\edu\LANComm\ui\MainFrame-getParameterSelected中的描述

5. 通信大致过程
    - 启动时，弹出MAC地址交换框，从MAC.properties中读取预先配置的收发端MAC；若预配置MAC不合适，需要修改，同时会跟新原预配置文件；如本地有多个网卡，则还需要选择本地网卡MAC；点击确定
    - 进入通信配置界面，其中通信参数选择部分，需要先选择参数的单位，后选择参数值；点击确定
    - 上位机向下位机发送参数配置信息，下位机接收后；收/发端回送中频采样率，float类型，一方面作为上位机FFT的参数，另一方面作为连接验证；
    - 上位机收到中频采样信号时，会首先校验两者是否相同，不相同或者未收到任何一方信息，则根据错误类型给出提示；否则启动接收线程
    - 下位机发送采样率后，等待1s，发送中频、星座、收/发码元；跳频图案若处于非跳频模式，则不发送；
    - 上位机收到包后，进行分发、解析、显示；收到的包将以packet的形式写入对应的dumpfile，充当数据保存
    - 点击断开后，上位机关闭所有接收线程；向下位机发送停机指令（待定）；

