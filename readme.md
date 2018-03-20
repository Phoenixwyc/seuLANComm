# 基于Jpcap以太网上位机说明

## 运行环境要求 

1、 win7 **64位操作系统**

2、 winpcap

## 软件安装使用

1、 如果没有winpcap，安装winpcap.exe

2、 安装seuLANCommSetup.exe，安装过程同其他软件安装过程，如遇杀毒软件拦截，放行，建议安装时关闭杀毒软件；

3、 如果没有JRE，双击seuLANComm.exe，如果有JRE，也可以点击seuLANComm.jar；

## 下位机开发注意事项

1、 见onfig目录下的配置文件

    - LANComm.properties文件配置系统工作参数

    - MAC.properties文件配置收发端MAC地址信息

2、 MAC帧结构

    - 前导码、界定符、CRC为系统默认设置；

    - FrameType字段固定为8511；

    - data段结构定义，见src\main\java\cn\seu\edu\LANComm\communication\util\FramingEncode中的描述；

    - 每帧的数据含义，见src\main\java\cn\seu\edu\LANComm\communication\util\DataLinkParameterEnum中的定义；

    - 通信参数定义，见src\main\java\cn\seu\edu\LANComm\util\CommunicationParameterEnum中的定义；

    - 通信模式编码，见rc\main\java\cn\seu\edu\LANComm\util\CommunicationModeEnum中的定义

3、 下位机控制指令，见src\main\java\cn\seu\edu\LANComm\communication\util\DataLinkParameterEnum中的定义

4、 数据类型，统一为**float类型**，4个字节表示，字节顺序为**Big Endian**；

5、 上位机向下位机发送参数配置信息时，各参数的顺序见src\main\java\cn\seu\edu\LANComm\ui\UIParameterCollector中的定义

6、 参数单位的映射关系，见src\main\java\cn\seu\edu\LANComm\communication\util\ParameterUnitEnum中的定义；

## 数据帧的特殊要求

1、 中频信号数据帧，由于还将参与FFT计算，最好每帧的数据量大一些，在30~255之间，若中频数据帧中数据点数小于10时，会弹窗警告FFT计算时数据点数太少（因为这时FFT计算没什么意义）；

2、 星座数据帧，在星座绘图是横轴是同相I路数据，纵轴是正交Q路数据，因此要求该帧的数据是I-Q成对出现，即格式为I-Q-I-Q...-I-Q的格式，如果不满足要求，将弹窗提示，且该帧的数据将被忽略；

3、 跳频图案数据帧，暂无

4、 发送符号和接收符号数据帧，要求发送符号数据帧和接收符号数据帧内的符号个数必须相同；否则上位机将弹窗提醒；上位机以发送符号数据帧的长度为准计算，即如果发送符号数据帧接收符号数据帧符号个数不同，那么必须保证发送符号帧长度不得小于接收符号数据帧，否则上位机会因ArrayIndexOutOfBounds这个运行时异常而崩溃，引起未知错误；

5、 数据帧的长度，在满足上述特殊要求的情况下，还需要考虑PHY芯片实现的协议，EthernetV2协议没有比特填充功能，此时如果帧的data段字节数小于48byte时，将被CSMA/CD协议认为是碎片而被网卡抛弃，802.3协议有比特自动填充，不存在上述情况

## 数据保存

&emsp;&emsp;上位机目前仅保存中频数据，其他类型的数据仅创建了对应的log文件，但没有数据写入；数据保存位置在软件安装目录，文件夹名为点击接受开始时的时间，且每次开始接收时，都将创建新的文件夹，实现不同参数配置下的数据隔离；IntermediateFrequencyData\.log中保存的就是中频信号，注意使用的jpcap的writePacket(Packet)方法，即保存的是原始数据帧，若需要读取数据，见Jpcap的API。

## 通信大致过程

    - 启动时，弹出MAC地址交换框，从MAC.properties中读取预先配置的收发端MAC；若预配置MAC不合适，请直接在窗口中修改，同时会跟新原预配置文件，下次程序启动时，将使用新的MAC地址；如本地有多个网卡，则还需要选择本地网卡MAC，如果电脑有虚拟网卡，不要选择这些虚拟网卡的MAC地址，可能出现未知的错误；

    - 进入通信配置界面，其中通信参数选择部分，需要先选择参数的单位，后选择参数值；
    - 上位机向下位机发送参数配置信息，下位机接收后；收/发端回送中频采样率，float类型，一方面作为上位机FFT的参数，另一方面作为连接验证；

    - 上位机收到中频采样信号时，会首先校验两者是否相同，不相同或者未收到任何一方信息，则根据错误类型给出提示；否则启动接收线程；

    - 下位机发送采样率后，等待1s（待定，上位机没有在意这一点），发送中频、星座、收/发码元；跳频图案若处于非跳频模式，则不发送，此时上位机将显示No Data；

    - 上位机收到包后，进行分发、解析、显示；收到的包将以packet的形式写入对应的dumpfile，充当数据保存

    - 点击断开后，上位机关闭所有接收线程；向下位机发送停机指令，下位机回到参数接收状态，等待下一轮参数发送

    - 点击上位机关闭按钮后，上位机将弹窗提醒，程序将在5秒后完全关闭，等待后台缓冲区数据的写入，建议等待5秒后再次启动上位机；
