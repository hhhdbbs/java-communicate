package server;

import client.ClientView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ChatSever的描述
 * ＜p＞ 客户端，监听新的client
 */
public class ChatServer extends Thread {


  private static List<ClientThread> clients = new ArrayList<ClientThread>();     // List储存各线程
  private volatile  ServerSocket server;
  private static ChatServer cs = null;
  private JTextArea textArea;
  private JScrollPane rightScroll;
  private JFrame frame;
  /**
   * 构造方法 的描述
   * @param port 端口
   */
  public ChatServer(int port) throws IOException {
    server = new ServerSocket(port);
    System.out.println("ChatServer start at 127.0.0.1:" + port);
  }

  /**
   * @author Hu Pengfei
   * run 方法的简述.
   * ＜p＞服务器线程运行
   */
  public void run() {
    initialGUI();
    while (true) {
      try {
        ClientThread client = new ClientThread(server.accept());    // 等待连接
        System.out.println("链接用户是" + client.username);
        client.start();
        clients.add(client);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @author Hu Pengfei
   * initialGUI 方法的简述.
   * ＜p＞初始化界面
   */
  private void initialGUI() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    UIManager.put("RootPane.setupButtonVisible", false);

    textArea = new JTextArea();
    textArea.setEditable(false);
    textArea.setForeground(Color.gray);


    rightScroll = new JScrollPane(textArea);
    rightScroll.setBorder(BorderFactory.createTitledBorder("服务端"));

    frame = new JFrame("JAVA聊天小平台");
    frame.setSize(700, 400);

    frame.setIconImage(toolkit.createImage(ClientView.class.getResource("chat.png")));

    frame.setLayout(new BorderLayout());
    frame.add(rightScroll, "Center");

    int screenWidth = toolkit.getScreenSize().width;
    int screenHeight = toolkit.getScreenSize().height;

    frame.setLocation((screenWidth - frame.getWidth()) / 2,
            (screenHeight - frame.getHeight()) / 2);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
  }

  /**
   * @author Hu Pengfei
   * receiveMessage 方法的简述.
   * ＜p＞打印服务端消息
   * @param message 消息内容
   */
  public void receiveMessage(String message) {
    textArea.append(message);
    textArea.append("\r\n\r\n");
  }
  /**
   * ClientThread的描述
   * ＜p＞ 作为前端的监听器，接受信息并发送信息,为客户端服务
   */
  public class ClientThread extends Thread {
    private Socket sock;
    private BufferedReader getter;
    private PrintWriter sender;
    private String username;

    public PrintWriter getSender() { return sender; }
    public int getPort() { return sock.getPort(); }
    public String getIP() { return sock.getInetAddress().getHostAddress(); }
    public String getUsername() { return username; }
    public String getIdentifier() { return sock.getInetAddress().getHostAddress() + ":" + sock.getPort(); }

    /**
     * 构造方法 的描述
     * @param sock socket
     */
    public ClientThread(Socket sock) {      // 初始化，即用户一开始登录
      try {
        this.sock = sock;
        this.getter = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.sender = new PrintWriter(sock.getOutputStream(), true);
        this.username = getter.readLine();
        receiveMessage(username+"上线了");
        // 将新上线用户信息群发给其他所有已在线用户
        sendToAllUser("ONLINE[#]" + username+ "[#]");
        // 将当前在线用户信息发送给新上线用户
        FileInputStream inputfile = new FileInputStream(Account.USERLIST_FILE);
        DataInputStream inputdata = new DataInputStream(inputfile);
        InputStreamReader reader = new InputStreamReader(inputfile,"UTF-8");
        BufferedReader br = new BufferedReader(reader);
        String strRead;
        while ((strRead = br.readLine()) != null) {
          System.out.println(strRead);
          String[] strings=strRead.split("\\|");
          sender.println("INFO[#]" + strings[0] + "[#]" + "ip" + "[#]" + "port");     // 其他用户
        }
        sender.flush();     // 清空缓存数据
        System.out.println("ChatServer: [ONLINE] [" + username + "] [" + getIdentifier() + "]");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * @author Hu Pengfei
     * sendToAllUser 方法的简述.
     * ＜p＞发送群发消息
     * @param message 消息内容
     */
    private void sendToAllUser(String message) {
      for (int i = 0; i < clients.size(); ++i) {
        if (clients.get(i).getIdentifier().equals(this.getIdentifier()))
          continue;
        clients.get(i).getSender().println(message+ "[#]"
                + clients.get(i).getIP() + "[#]" + clients.get(i).getPort());
        clients.get(i).getSender().flush();
      }
    }

    /**
     * @author Hu Pengfei
     * sendToSpecificUser 方法的简述.
     * ＜p＞发送私人聊天记录
     * @param message 消息内容
     * @param dest 接收方
     */
    private void sendToSpecificUser(String message, String dest) {
      for (int i = 0; i < clients.size(); ++i) {
        if (clients.get(i).getUsername().equals(dest)) {
          clients.get(i).getSender().println(message);
          clients.get(i).getSender().flush();
        }
      }
    }

    /**
     * @author Hu Pengfei
     * run 方法的简述.
     * ＜p＞监听器线程运行
     */
    public void run() {
      String line;
      while (true) {      // 真正的run程序
        try {
          line = getter.readLine();
          receiveMessage(line);
          if (line.equals("[OFFLINE]")) {// 用户下线
            offline();
            return;
          }
          else {// 普通消息
            System.out.println(line);
            StringTokenizer tokenizer = new StringTokenizer(line, "[#]");
            String command = tokenizer.nextToken();
            String message = tokenizer.nextToken();
            String from=tokenizer.nextToken();
            if (command.equals("GROUP")) {// 群聊消息
              if(tokenizer.hasMoreElements()) {   // @人的操作
                String dest = tokenizer.nextToken();
                sendToAllUser("GROUP[#]" + username + "[#]" + message + "[#]" + from+"[#]" + dest);
              }
              else {
                sendToAllUser("GROUP[#]" + username + "[#]" + message+"[#]" + from);
              }
              Account.saveToAll(from, "GroupChat", message);
            }
            else {// 私聊消息
              String dest = tokenizer.nextToken();
              sendToSpecificUser("P2P[#]" + username + "[#]" + message, dest);
              Account.saveToSpecificUser(username, dest, message);
            }
          }
        } catch (Exception e) {
          offline();
          return;
        }
      }
    }

    /**
     * @author Hu Pengfei
     * offline 方法的简述.
     * ＜p＞用户下线，转告其他用户，关闭资源
     */
    // 用户下线，转告其他用户，关闭socket资源并移除该线程
    private void offline() {
      try {
        sendToAllUser("OFFLINE[#]" + username + "[#]" + getIP() + "[#]" + getPort());
        System.out.println("ChatServer: [OFFLINE] [" + username + "] [" + getIdentifier() + "]");
        getter.close();
        sender.close();
        sock.close();
        for (int i = 0; i < clients.size(); i++) {
          if (clients.get(i).getIdentifier().equals(this.getIdentifier())) {
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
