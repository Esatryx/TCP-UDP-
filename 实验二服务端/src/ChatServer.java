import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatServer {
    // 用于存放连接的客户端处理器
    static Map<String, ChatHandler> socketMap = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("正在等待客户端连接...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("接收到新客户端的连接，IP地址为：" + socket.getInetAddress().getHostAddress());
                System.out.println("新客户端端口号为：" + socket.getPort());
                ChatHandler tmp = new ChatHandler(socket);
                socketMap.put(socket.getInetAddress().getHostAddress(), tmp);
                tmp.start();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // 处理每个客户端的线程
    static class ChatHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private String clientAddr;

        public ChatHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                // 获取客户端地址
                clientAddr = socket.getInetAddress().getHostAddress();
                System.out.println(clientAddr + " 已加入聊天");
                System.out.println("当前聊天室人数: "+socketMap.size());
                String message;
                while ((message = in.readLine()) != null) {
                    // 处理客户端发送的消息
                    handleIncomingMessage(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    socketMap.remove(clientAddr);
                    System.out.println(clientAddr + " 已离开聊天");
                    System.out.println("当前聊天室人数: "+socketMap.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 处理来自客户端的消息
        private void handleIncomingMessage(String message) {
            String[] parts = message.split(":", 2); // message格式: 目标IP:内容
            if (parts.length < 2) {
                System.out.println("广播Received:"+ Arrays.toString(parts));
                //out.println("友情提示：单人聊天正确格式为：目标IP:消息内容");
                for (Map.Entry<String, ChatHandler> entry : socketMap.entrySet()) {
                    ChatHandler handler = entry.getValue();
                    // 不向发送者发送自己发送的消息
                    if (!entry.getKey().equals(clientAddr)) {
                        handler.out.println(clientAddr + " 对大家说: " + Arrays.toString(parts));

                    }
                }
                return;
            }

            String targetAddr = parts[0].trim();
            String msgContent = parts[1].trim();

            // 转发消息到目标客户端
            ChatHandler targetHandler = socketMap.get(targetAddr);
            if (targetHandler != null) {
                targetHandler.out.println(clientAddr + " 对您说: " + msgContent);
                System.out.println(clientAddr + " 对"+targetHandler+"说: " + msgContent);
            } else {
                out.println("未找到目标客户端: " + targetAddr);
            }
        }
    }
}