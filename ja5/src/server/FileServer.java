package server;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * FileServer的描述
 * ＜p＞ 监听FileClient
 */
public class FileServer extends Thread {

  private List<ClientThread> clients = new ArrayList<ClientThread>();//List储存各个线程
  private ServerSocket server;

  /**
   * 构造方法 的描述
   * @param port 端口
   */

  public FileServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("FileServer start at 127.0.0.1:" + port);
  }

  /**
   * @author Hu Pengfei
   * run 方法的简述.
   * ＜p＞文件服务器主线程
   */
  public void run() {
    while (true) {
      try {
        ClientThread client = new ClientThread(server.accept());
        client.start();
        clients.add(client);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * ClientThread的描述
   * ＜p＞ 作为前端文件的监听器，接受文件和发送文件
   */
  private class ClientThread extends Thread {

    private Socket sock;                // Socket类
    private DataInputStream getter;     // 输入流
    private DataOutputStream sender;    // 输出流
    private String username;

    /**
     * @author Hu Pengfei
     * getSender 方法的简述.
     * ＜p＞获取发送记录的流
     * @return 流
     */
    public DataOutputStream getSender() { return sender; }

    /**
     * @author Hu Pengfei
     * getPort 方法的简述.
     * ＜p＞获取端口
     * @return 端口
     */
    public int getPort() { return sock.getPort(); }

    /**
     * @author Hu Pengfei
     * getIP 方法的简述.
     * ＜p＞获取IP
     * @return IP
     */
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }

    /**
     * 构造方法 的描述
     * @param sock socket
     */

    public ClientThread(Socket sock) {
      try {
        this.sock = sock;
        this.getter = new DataInputStream(sock.getInputStream());
        this.sender = new DataOutputStream(sock.getOutputStream());
        this.username = getter.readUTF();
        System.out.println("FileServer: [ONLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * @author Hu Pengfei
     * sendFileToAllUser 方法的简述.
     * ＜p＞发送给全体成员文件
     * @param buff 文件内容
     * @param length 文件长度
     */
    private void sendFileToAllUser(byte[] buff, int length) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username))
            continue;
          clients.get(i).getSender().write(buff, 0, length);
          clients.get(i).getSender().flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * @author Hu Pengfei
     * sendBasicInfoToAllUser 方法的简述.
     * ＜p＞群发文件基本信息
     * @param fileInfo 文件名
     * @param fileLength 文件长度
     */

    private void sendBasicInfoToAllUser(String fileInfo, long fileLength) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username))
            continue;
          clients.get(i).getSender().writeUTF(fileInfo);
          clients.get(i).getSender().flush();
          clients.get(i).getSender().writeLong(fileLength);
          clients.get(i).getSender().flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    /**
     * @author Hu Pengfei
     * sendFileToSpecificUser 方法的简述.
     * ＜p＞发送新建信息给指定用户
     * @param buff 文件内容
     * @param length 文件长度
     * @param dest 目的地
     */

    private void sendFileToSpecificUser(byte[] buff, int length, String dest) {
      try {
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(dest)) {
            clients.get(i).getSender().write(buff, 0, length);
            clients.get(i).getSender().flush();
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * @author Hu Pengfei
     * sendBasicInfoSpecificUser 方法的简述.
     * ＜p＞发送文件基本信息给指定用户
     * @param fileInfo 文件名
     * @param fileLength 文件长度
     * @param dest 目的地
     */

    private void sendBasicInfoToSpecificUser(String fileInfo, long fileLength, String dest) {
      try {
        for (int i = 0; i < clients.size(); ++i) {      // 遍历
          if (clients.get(i).getUsername().equals(dest)) {
            clients.get(i).getSender().writeUTF(fileInfo);
            clients.get(i).getSender().flush();
            clients.get(i).getSender().writeLong(fileLength);
            clients.get(i).getSender().flush();
            break;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * @author Hu Pengfei
     * run 方法的简述.
     * ＜p＞主线程不断运行
     */
    public void run() {
      byte[] buff = new byte[1024];
      while (true) {
        try {
          String message = getter.readUTF();
          if (message.equals("[OFFLINE]")) {
            offline();
            return;
          }
          StringTokenizer stringTokenizer = new StringTokenizer(message, "[#]");
          String command = stringTokenizer.nextToken();
          String fileName = stringTokenizer.nextToken();
          String dest = null;
          boolean toAll = false;
          long fileLength = getter.readLong();
          if (command.equals("GROUP")) {// 群发文件
            sendBasicInfoToAllUser("GROUP[#]" + fileName + "[#]" + username, fileLength);
            toAll = true;
          }
          else if (command.equals("P2P")) {// 私发文件
            dest = stringTokenizer.nextToken();
            sendBasicInfoToSpecificUser("P2P[#]" + fileName + "[#]" + username, fileLength, dest);
          }
          int length = 0, total = 0;
          while (total < fileLength) {
            length = getter.read(buff);
            total += length;
            if (toAll)
              sendFileToAllUser(buff, length);
            else
              sendFileToSpecificUser(buff, length, dest);
          }
        } catch (IOException e) {
          offline();
          return;
        }
      }
    }

    /**
     * @author Hu Pengfei
     * offline 方法的简述.
     * ＜p＞用户下线 关闭socket资源
     */
    private void offline() {
      try {
        System.out.println("FileServer: [OFFLINE] [" + username + "] [" + getIP() + ":" + getPort() + "]");
        getter.close();
        sender.close();
        sock.close();
        for (int i = 0; i < clients.size(); ++i) {
          if (clients.get(i).getUsername().equals(this.username)) {
            clients.remove(i);
            return;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
