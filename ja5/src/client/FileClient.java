package client;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Random;

/**
 * FileClient的描述
 * ＜p＞ 作为前端和客户端监听器的连接器，发送文件
 */
public class FileClient extends Thread {

  private String serverIP;
  private String username;//用户名
  private int port;
  DataOutputStream sender;
  DataInputStream getter;
  Socket socket;
  boolean stopFile = false;//运行状态
  Client parentThread;
  ListenerThread listener;

  /**
   * @author Zhang Xiaohan
   * stopFileMessage 方法的简述.
   * ＜p＞停止线程
   */
  public void stopFileThread() { stopFile = true; }

  /**
   * 构造方法 的描述
   * @param serverIP IP
   * @param port 端口
   * @param username 当前用户名
   * @param parentThread 创办他的Client
   */
  public FileClient(String serverIP, int port, String username, Client parentThread) throws IOException {
    this.serverIP = serverIP;
    this.port = port;
    this.parentThread = parentThread;
    this.username = username;
    this.socket = new Socket(serverIP, port);
    sender = new DataOutputStream(socket.getOutputStream());
    getter = new DataInputStream(socket.getInputStream());
    sender.writeUTF(username);
    listener = new ListenerThread();
    listener.start();
  }

  /**
   * @author Zhang Xiaohan
   * sendFile 方法的简述.
   * ＜p＞向服务器发送文件内容
   * @param info 消息内容
   * @param filename 文件名字
   */
  public void sendFile(String info, String filename) {
    try {
      File file = new File(filename);
      FileInputStream fis = new FileInputStream(file);
      sender.writeUTF(info);
      sender.writeLong(file.length());
      byte[] buff = new byte[1024];
      int length = 0;
      while ((length = fis.read(buff, 0, buff.length)) > 0) {
        sender.write(buff, 0, length);
        sender.flush();
      }
    } catch (Exception e) {
      // ...
    }
  }

  /**
   * @author Zhang Xiaohan
   * run 方法的简述.
   * ＜p＞线程运行 直到线程结束
   */
  public void run() {
    try {
      while (!stopFile) {
        // ...
      }
      listener.shutdown();
      sender.writeUTF("[OFFLINE]");
      sender.close();
      getter.close();
      socket.close();
    } catch (Exception e) {
      return;
    }
  }

  private class ListenerThread extends Thread {
    /**
     * ListenerThread的描述
     * ＜p＞ 作为客户端的监听器，接受文件信息给前端
     */
    private boolean stop = false;

    /**
     * @author Zhang Xiaohan
     * run 方法的简述.
     * ＜p＞监听服务器，接受文件发送给客户端
     */
    public void run() {
      while (!stop) {
        try {
          String info = getter.readUTF();
          long filelength = getter.readLong();
          StringTokenizer tokenizer = new StringTokenizer(info, ".");
          String extendName = tokenizer.nextToken();
          extendName = tokenizer.nextToken();
          File file = new File(username + (new Random()).nextInt(1000) + "." + extendName);
          FileOutputStream fos = new FileOutputStream(file);
          byte[] buff = new byte[1024];
          int length = 0, total = 0;
          while (total < filelength) {
            length = getter.read(buff);
            total += length;
            fos.write(buff, 0, length);
            fos.flush();
          }
          fos.close();
          parentThread.receiveMessage("FILE[#]" + info);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    /**
     * @author Zhang Xiaohan
     * shutdown 方法的简述.
     * ＜p＞终止线程
     */
    public void shutdown() { stop = true; }
  }
}