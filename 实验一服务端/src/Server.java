import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String[] args) {


        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("正在等待客户端连接...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("接收到新客户端的连接，IP地址为："+socket.getInetAddress().getHostAddress());
                System.out.println("新客户端端口号为：" + socket.getPort());
                ChatHandler tmp =new ChatHandler(socket);
                tmp.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatHandler extends Thread {
    private Socket socket;
    private PrintWriter out;

    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
            //out = new PrintWriter(socket.getOutputStream(),  true);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            // 在一个线程中接收消息
            new Thread(new MessageReceiver(in)).start();

            // 在当前线程中发送消息
            sendMessages();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 用于接收消息的线程
    private class MessageReceiver implements Runnable {
        private BufferedReader in;

        public MessageReceiver(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from "+ socket.getInetAddress() +"  :"+ message);
                    out.println("客户端已收到数据");  // Echo back to client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 用于发送消息
    private void sendMessages() {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String userMessage;
        try {
            while (true) {
                userMessage = userInput.readLine();
                if (userMessage.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println("服务端数据:"+userMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}