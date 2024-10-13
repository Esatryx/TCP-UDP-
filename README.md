# TCP-UDP-
# 实验一/实验二
* jdk22版本，IDE为IDEA2024.1.4
* 构建系统为Intellij，点击路径下对应/src文件夹的java文件开始运行整个项目。
* 区分客户端部分与服务端部分,先运行服务端等待客户端连接，再运行客户端
  * 实验一中注意修改代码中的下列部分，本地测试请改为localhost
    * try (Socket socket = new Socket("localhost", 8080);
    * try (ServerSocket serverSocket = new ServerSocket(8080))
  * 实验二中修改代码中的下列部分，客户端本地测试请尽量不要改为localhost，使用真实ip。
    *  try (ServerSocket serverSocket = new ServerSocket(8080))
    *  try (Socket socket = new Socket("localhost", 8080);

# 实验三
* jdk22版本，IDE为IDEA2024.1.4
* 构建系统为Maven，配置使用javacv 1.5.7和javacvplatform 1.5.7，点击路径下对应\src\main\java\com\example文件夹的java文件开始运行整个项目。
* 不区分客户端部分与服务端部分,收发双方为同一代码，开两次程序即可。
  * 实验三中注意修改代码中的下列部分，端口号默认一致，只改对方的ip地址即可，建议使用两台主机进行测试。
    *  private static final String RECEIVER_IP = "10.15.4.16";
