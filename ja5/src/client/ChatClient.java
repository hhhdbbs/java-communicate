package client;

import java.io.*;
import java.net.*;

/**
 * ChatClient的描述
 * ＜p＞ 作为服务端和客户端监听器的连接器，发送消息
 *
 */
public class ChatClient extends Thread {

  private String serverIP;
  private String username;
  private int port;
  PrintWriter sender;
  BufferedReader getter;
  Socket socket;
  boolean stopChat = false;//运行状态
  Client parentThread; //创办他的client
  ListenerThread listener;

  /**
   * 构造方法 的描述
   * @param serverIP IP
   * @param port 端口
   * @param username 当前用户名
   * @param parentThread 创办他的Client
   */
  public ChatClient(String serverIP, int port, String username, Client parentThread) throws IOException {
    this.serverIP = serverIP;
    this.port = port;
    this.parentThread = parentThread;
    this.socket = new Socket(serverIP, port);
    this.username = username;
    sender = new PrintWriter(socket.getOutputStream(), true);
    getter = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // sender.println(username);
    listener = new ListenerThread();
    listener.start();
  }

  /**
   * @author Zhang Xiaohan
   * stopChatThread 方法的简述.
   * ＜p＞停止线程
   */
  public void stopChatThread() { stopChat = true; }

  /**
   * @author Zhang Xiaohan
    * sendMessage 方法的简述.
   * ＜p＞发送消息
   * @param message send message
   */
  public void sendMessage(String message) {
    sender.println(message);
    sender.flush();
  }
  /**
   * @author Zhang Xiaohan
   * run 方法的简述.
   * ＜p＞线程不断运行 直到退出 offline
   */
  public void run() {
    try {
      while (!stopChat) {
        // ...
      }
      listener.shutdown();
      sender.println("[OFFLINE]");
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
     * ＜p＞ 作为服务端的监听器，接受消息发送给前端
     */

    /**
     * @author Zhang Xiaohan
     * run 方法的简述.
     * ＜p＞接收消息
     */

    private boolean stop = false;

    public void run() {
      while (!stop) {
        try {
          String message = getter.readLine();
          if (message == null)
            continue;
          parentThread.receiveMessage(message);
        } catch (IOException e) {
           e.printStackTrace();
        }
      }
    }

    /**
     * @author Zhang Xiaohan
     * shutdown 方法的简述.
     * ＜p＞通过设置stop 结束进程
     */
    public void shutdown() { stop = true; }
  }
}