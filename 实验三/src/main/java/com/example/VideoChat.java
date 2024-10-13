package com.example;

import org.bytedeco.javacv.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class VideoChat {
    private static final int PORT = 12345;
    private static final String RECEIVER_IP = "localhost"; // 目标接收地址（可以替换为实际接收者的IP）

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress receiverAddress = InetAddress.getByName(RECEIVER_IP);

        // 创建并启动发送线程
        new Thread(() -> {
            try {
                sendVideo(socket, receiverAddress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 创建并启动接收线程
        new Thread(() -> {
            try {
                receiveVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void sendVideo(DatagramSocket socket, InetAddress receiverAddress) throws Exception {
        FrameGrabber grabber = new OpenCVFrameGrabber(0); // 使用默认摄像头
        grabber.start();

        // 创建 CanvasFrame 用于显示发送画面
        CanvasFrame sendingCanvas = new CanvasFrame("Sending Video", 1);

        // 改为使用更高效的 BufferedImage 处理
        byte[] buffer;
        float quality = 0.8f;  // JPEG压缩质量: 1.0 是最好，0.0 是最差
        long startTime = System.currentTimeMillis();
        int frameCount = 0;
        long totalDelay = 0;

        try {
            while (true) {
                long frameStartTime = System.currentTimeMillis();
                Frame frame = grabber.grab();
                if (frame != null) {
                    // 将 Frame 转换为 BufferedImage
                    BufferedImage img = new Java2DFrameConverter().convert(frame);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    // 使用 JPEG 压缩质量
                    ImageIO.write(img, "jpg", baos);
                    buffer = baos.toByteArray();

                    // 发送视频数据
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, PORT);
                    long sendStartTime = System.currentTimeMillis();
                    socket.send(packet);
                    long sendEndTime = System.currentTimeMillis();

                    // 计算延迟
                    long delay = sendEndTime - sendStartTime;
                    totalDelay += delay;

                    frameCount++;

                    // 显示发送的画面
                    sendingCanvas.showImage(new Java2DFrameConverter().convert(img));

                    // 每 100 幅帧输出一次发送速率和平均延迟
                    if (frameCount % 100 == 0) {
                        long elapsedTime = System.currentTimeMillis() - startTime; // 从开始到现在的时间
                        double fps = (frameCount / (elapsedTime / 1000.0));
                        double averageDelay = (totalDelay / (double) frameCount);

                        System.out.printf("FPS: %.2f, Average Delay: %.2f ms%n", fps, averageDelay);
                    }
                }
                // 调整这么换成更小的值以提高帧率，30帧应为约33ms
                Thread.sleep(10); // 控制帧率，可以根据需求调整
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sendingCanvas.dispose(); // 确保在退出时释放资源
            grabber.stop();
            socket.close();
        }
    }

    private static void receiveVideo() throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[65536]; // 接收缓存
        CanvasFrame canvas = new CanvasFrame("Video Receiver", 1);

        try {
            while (canvas.isVisible()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // 将字节数组转换为 BufferedImage
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                BufferedImage img = ImageIO.read(bais);

                // 显示接收的图像
                Frame frame = new Java2DFrameConverter().convert(img);
                canvas.showImage(frame);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            canvas.dispose();
            socket.close();
        }
    }
}