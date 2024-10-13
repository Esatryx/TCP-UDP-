import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // 客户端
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("已连接到服务端！");
            System.out.println("服务端IP地址为：" + socket.getInetAddress().getHostAddress());
            System.out.println("服务端端口号为：" + socket.getPort());
            System.out.println("请随时输入要发送的消息，输入 'sendfile <文件路径>' 来发送文件。");

            // 通过输出流向服务端发送数据
            OutputStream stream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(stream, true); // 创建 PrintWriter 以便于发送消息

            // 启动一个线程用于接收服务端消息
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("接收服务端消息时发生错误！");
                    e.printStackTrace();
                }
            }).start(); // 启动接收线程

            // 主线程用于读取用户输入并发送到服务端
            while (true) {
                String input = scanner.nextLine();

                // 检查用户输入是否是发送文件的命令
                if (input.startsWith("sendfile ")) {
                    String filePath = input.substring("sendfile ".length());
                    try {
                        File file = new File(filePath);
                        if (file.exists()) {
                            BufferedReader fileReader = new BufferedReader(new FileReader(file));
                            String fileLine;
                            while ((fileLine = fileReader.readLine()) != null) {
                                out.println(fileLine);
                            }
                            fileReader.close();
                            System.out.println("文件发送完毕！");
                        } else {
                            System.out.println("文件不存在！");
                        }
                    } catch (IOException e) {
                        System.out.println("读取文件时发生错误！");
                        e.printStackTrace();
                    }
                } else {
                    // 发送用户输入的内容到服务端
                    out.println(input);
                }
            }

        } catch (IOException e) {
            System.out.println("服务端连接失败！");
            e.printStackTrace();
        }
    }
}