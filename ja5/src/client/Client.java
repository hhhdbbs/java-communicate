package client;

import server.Account;
import server.ChatServer;

import javax.swing.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * ChatClient的描述
 * ＜p＞ 作为前端和监听器的连接器，对发送消息和接收消息做出一定反应
 */
public class Client extends Thread {

  private ChatClient cc;
  private FileClient fc;
  private ClientView clientView;
  private Map<String, ArrayList<String>> chatRecords = new HashMap<String, ArrayList<String>>();//聊天记录保存的地方
  private boolean stopClient = false;//运行状态
  private String username;//用户名
  private String lock="lock";

  /**
   * @author Zhang Xiaohan
   * setUsername 方法的简述.
   * ＜p＞设置username
   * @param username 用户名
   */
  public void setUsername(String username) {
    this.username=username;
  }
  /**
   * @author Zhang Xiaohan
   * getUsername 方法的简述.
   * ＜p＞获取username
   * @return username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @author Zhang Xiaohan
   * setClientThread 方法的简述.
   * ＜p＞和clientView链接
   * @param cv a ClientView
   * @return 没有返回值
   */
  public void setClientViewThread(ClientView cv) { clientView = cv; }

  /**
   * @author Zhang Xiaohan
   * run 方法的简述.
   * ＜p＞线程运行
   */
  public void run() {
    while (!stopClient) {
      // ...
    }
  }

  /**
   * @author Zhang Xiaohan
   * connect 方法的简述.
   * ＜p＞为GUI连接按钮提供服务
   * @param ip 设置连接ip
   * @param port 设置连接端口
   * @param username 设置用户名
   */
  public void connect(String ip, int port, String username) {
    try {
      this.username = username;
      System.out.println("Client的username" + username + " " + this.username);
      cc = new ChatClient(ip, port, username, this);
      fc = new FileClient(ip, port + 1, username, this);
      chatRecords= Account.getChatRecord(username);
      if (chatRecords.get("GroupChat")==null)
        chatRecords.put("GroupChat", new ArrayList<String>());
      clientView.setChatUser("GroupChat");
      JTextField txt_name=clientView.getTxt_name();
      txt_name.setText("GroupChat");
      clientView.getListModel().addElement("GroupChat");
      clientView.updateMessage();
      Account.login(username);
      cc.sendMessage(username);
    } catch (Exception e) {
      //...
    }
  }

  /**
   * @author Zhang Xiaohan
   * sendMessage 方法的简述.
   * ＜p＞发送消息给chatClient并做出一定反应
   * @param message 传递的消息
   */
  public void sendMessage(String message) {
    System.out.println(message+" send");
    if (message.equals("[OFFLINE]")) {
      cc.sendMessage(message+"[#]" + username);
      return;
    }
    StringTokenizer tokenizer = new StringTokenizer(message, "[#]");
    String command = tokenizer.nextToken();
    String msg = tokenizer.nextToken();
    String from=tokenizer.nextToken();
    System.out.println(command);
    if (command.equals("P2P")) {
      String to = tokenizer.nextToken();
      chatRecords.get(to).add(from + "[#]" + msg);
    }
    else if(command.equals("")) {
    }
    else{
      if (chatRecords.get("GroupChat")==null)
        chatRecords.put("GroupChat", new ArrayList<String>());
      chatRecords.get("GroupChat").add(from + "[#]" + msg);
    }
    cc.sendMessage(message);
    // 匿名消息其他和群消息一样，只不过名称是Anonymous
  }

  /**
   * @author Zhang Xiaohan
   * disconnect 方法的简述.
   * ＜p＞为退出按钮作服务，下线
   */
  public void disconnect() {
    cc.stopChatThread();
    fc.stopFileThread();
    stopClient = true;
  }

  /**
   * @author Zhang Xiaohan
   * getChatRecord 方法的简述.
   * ＜p＞获取和某个人的聊天记录
   * @param username 关键字
   * @return 消息记录
   */
  public List<String> getChatRecord(String username) {
    return chatRecords.get(username);
  }

  /**
   * @author Zhang Xiaohan
   * sendFile 方法的详细描述
   * ＜p＞为发送文件按钮服务
   * @param info 消息
   * @param filename 名称
   */
  public void sendFile(String info, String filename) {
    fc.sendFile(info, filename);
  }

  /**
   * @author Zhang Xiaohan
   * receiveMessage 方法的简述.
   * ＜p＞为收到消息服务
   * @param message 消息内容
   */
  // 为聊天线程收到新消息提供服务，即接受到服务器上的消息
  public synchronized void receiveMessage(String message) {
    System.out.println(message);
    StringTokenizer tokenizer = new StringTokenizer(message, "[#]");
    String command = tokenizer.nextToken();
    String user = tokenizer.nextToken();
    String msg;
    String from;
    if (command.equals("INFO") || command.equals("ONLINE")) { // 在线用户及有人上线
      if (user.equals(username))
        return;
      String ip = tokenizer.nextToken();
      String port = tokenizer.nextToken();
      System.out.println(user);
      if(chatRecords.get(user) == null)
        chatRecords.put(user, new ArrayList<String>());
      if (!clientView.sameToOtherName(user))
          clientView.getListModel().addElement(user);
      clientView.updateGUI("ONLINE", user, "");
    }
    else if (command.equals("GROUP")) {// 群聊消息
      msg = tokenizer.nextToken();
      from = tokenizer.nextToken();;
      if(tokenizer.hasMoreElements()) {  // 在群聊中被@，需要在用户client 页面中进行操作
        String notice=tokenizer.nextToken();
        if(notice.equals(username)){
          clientView.bementioned(user);
        }
      }
      chatRecords.get("GroupChat").add(from + "[#]" + msg);
   //   Account.writerecord(user, "GroupChat", msg);
      clientView.updateGUI("GROUP", msg, from);
    }
    else if (command.equals("P2P")) {// 私聊消息
      msg = tokenizer.nextToken();
      chatRecords.get(user).add(user + "[#]" + msg);
    //  Account.writerecord(user, username, msg);
      clientView.updateGUI("P2P", msg, user);
    }
    else if (command.equals("OFFLINE")) {// 有人下线
      String ip = tokenizer.nextToken();
      String port = tokenizer.nextToken();
      clientView.updateGUI("OFFLINE", user, "");
    }
    else if (command.equals("FILE")) {
      String filename = tokenizer.nextToken();
      String username = tokenizer.nextToken();
      clientView.updateGUI("FILE", username + "向你发送了文件" + filename, "");
    }
  }
}