package client;

import server.Account;
import server.ChatServer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.SerializablePermission;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * ClientView的描述
 * ＜p＞ 前端，显示界面并对用户的作用作出反应，接受消息发送给后端，并接受服务端的消息做出一定反应
 */
public class ClientView extends Thread {

    private ClientView clientView ;
    private JFrame frame;//聊天窗口
    private String anonymousName=String.valueOf((int)(Math.random()*10000))+"(匿名名字)";//匿名名称
    private boolean searching=false;//是否是搜索状态
    private int change_time=0;//更改匿名名称的次数
    private JFrame loginFrame;//登录窗口
    private JTextField loginguser;
    private JTextField loginpass;
    private JTextField noticeName;
    private JButton btn_login;
    private JList userList;
    private JTextArea textArea;
    private JTextArea txt_msg;
    private JTextField txt_user;
    private JTextField txt_port;
    private JTextField txt_hostIP;
    private JTextField txt_name;
    private JTextField txt_search;
    private JTextField txt_changeName;
    private JButton btn_start;
    private JButton btn_stop;
    private JButton btn_send;
    private JButton btn_AnonymousSend;
    private JButton btn_sendFile;
    private JButton btn_zhuce;
    private JButton btn_search;
    private JButton btn_changeName;
    private JPanel loginPanel;
    private JPanel northPanel;
    private JPanel eastPanel;
    private JPanel southPanel;
    private JPanel panel;
    private JPanel inner_north_panel;
    private JScrollPane rightScroll;
    private JScrollPane leftScroll;
    private JScrollPane msgScroll;
    private JSplitPane centerSplit;
    private JSplitPane rightSplit;

    public DefaultListModel getListModel() {
        return listModel;
    }

    private DefaultListModel listModel;

    private int noCheck;

    private String serverIP="127.0.0.1", serverPORT="8090";

    private String chatUser;

    private boolean isGroup;

    private Client client;

    private static String[] DEFAULT_FONT  = new String[] {
        "Table.font"
        , "TableHeader.font"
        , "CheckBox.font"
        , "Tree.font"
        , "Viewport.font"
        , "ProgressBar.font"
        , "RadioButtonMenuItem.font"
        , "ToolBar.font"
        , "ColorChooser.font"
        , "ToggleButton.font"
        , "Panel.font"
        , "TextArea.font"
        , "Menu.font"
        , "TableHeader.font"
        , "OptionPane.font"
        , "MenuBar.font"
        , "Button.font"
        , "Label.font"
        , "PasswordField.font"
        , "ScrollPane.font"
        , "MenuItem.font"
        , "ToolTip.font"
        , "List.font"
        , "EditorPane.font"
        , "Table.font"
        , "TabbedPane.font"
        , "RadioButton.font"
        , "CheckBoxMenuItem.font"
        , "TextPane.font"
        , "PopupMenu.font"
        , "TitledBorder.font"
        , "ComboBox.font"
    };

    /**
     * @author Zhang Xiaohan
     * run 方法的简述.
     * ＜p＞线程运行 初始化登陆界面
     */

    public void run() {
        loginGUI();
        clientView=this;
    }


    /**
     * @author Zhang Xiaohan
     * setClientThread 方法的简述.
     * ＜p＞ 和Client建立链接
     * @param client 要建立链接的Client
     */
       public void setClientThread(Client client) {
       this.client = client;
    }

    /**
     * @author Zhang Xiaohan
     * addListeners 方法的简述.
     * ＜p＞添加聊天窗口额监听器
     */
       private void addListeners() {
           // 断开按钮点击
           btn_stop.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   exitNow();
                   frame.dispose();
                   Account.exituser(client.getUsername());
                   System.exit(0);
               }
           });

           //匿名发送消息
           btn_AnonymousSend.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   if (chatUser.equals("Group"))
                       JOptionPane.showMessageDialog(frame, "只能给群聊发送匿名消息",
                               "登录失败", JOptionPane.WARNING_MESSAGE);
                   else {
                       String message = txt_msg.getText();
                       if (message==null||message.equals("")){
                           JOptionPane.showMessageDialog(frame, "发送消息不能为空",
                                   "发送失败",  JOptionPane.WARNING_MESSAGE);
                           return;
                       }
                       String pattern = ".*[\\|\\[#\\]].*";
                       boolean isMatch = Pattern.matches(pattern,message)|| Pattern.matches(pattern, message);
                       if (isMatch){
                           JOptionPane.showMessageDialog(loginFrame, "发送消息不能包含特殊字符 [ # ] |",
                                   "发送失败", JOptionPane.WARNING_MESSAGE);
                           return;
                       }
                       client.sendMessage("GROUP[#]" + message + "[#]"  +anonymousName);
                       txt_msg.setText("");
                       receiveMessage(anonymousName, message);
                   }
               }
           });

           txt_search.addKeyListener(new KeyListener() {
               @Override
               public void keyPressed(KeyEvent e) {
               }
               @Override
               public void keyReleased(KeyEvent e) {
                   int key = e.getKeyCode();
                   if(key == '\n')
                       btn_search_func();
               }
               @Override
               public void keyTyped(KeyEvent e) {

               }
           });
           //消息输入框快捷键设置
           txt_msg.addKeyListener(new KeyListener() {
               @Override
               public void keyPressed(KeyEvent e) {
               }
               @Override
               public void keyReleased(KeyEvent e) {
                   int key = e.getKeyCode();
                   if(key == '\n')
                       btn_send_message();
               }
               @Override
               public void keyTyped(KeyEvent e) {

               }
           });

           //更改匿名名称输入框快捷键设置
           txt_changeName.addKeyListener(new KeyListener() {
               @Override
               public void keyPressed(KeyEvent e) {
               }
               @Override
               public void keyReleased(KeyEvent e) {
                   int key = e.getKeyCode();
                   if(key == '\n')
                       btn_changeName_func();
               }
               @Override
               public void keyTyped(KeyEvent e) {

               }
           });
           // 发送消息
           btn_send.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   // TODO Auto-generated method stub
                   btn_send_message();
               }
           });

           // 发送文件
           btn_sendFile.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   // TODO Auto-generated method stub
                   JFileChooser fd = new JFileChooser();
                   fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                   fd.showOpenDialog(null);
                   File f = fd.getSelectedFile();
                   if (f != null) {
                       try {
                           String filename = f.getAbsolutePath();
                           if (isGroup) {
                               client.sendFile("GROUP[#]" + filename, filename);
                           } else {
                               client.sendFile("P2P[#]" + filename + "[#]" + chatUser, filename);
                           }
                       } catch (Exception e2) {
                           // TODO: handle exception
                       }
                   }
               }
           });
           // 切换窗口
           userList.addListSelectionListener(new ListSelectionListener() {
               public synchronized void  valueChanged(ListSelectionEvent e) {
                   // TODO Auto-generated method stub
                   String content = (String) userList.getSelectedValue();
                   System.out.println(content);
                   int i = userList.getSelectedIndex();
                   if (content.equals("GroupChat")||content.equals("GroupChat(New Message)")) {
                       isGroup = true;
                       noticeName.setEditable(true);
                   }
                   else{
                       isGroup=false;
                       noticeName.setEditable(false);
                   }
                   if (content==null){
                       textArea.setText("");
                       return;
                   }
                   if (noCheck==100||noCheck==-1){
                       noCheck=0;
                       return;
                   }
                   if (content.contains("(New Message)")) {
                       chatUser = content.substring(0, content.indexOf('('));
                       noCheck=-1;
                       listModel.add(i, chatUser);
                       noCheck=-1;
                       listModel.remove(i + 1);
                       return;
                   }
                   else
                       chatUser = content;
                   System.out.println("hh");
                   updateMessage();
               }
           });

           //搜索按钮事件设置
           btn_search.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  btn_search_func();
               }
           });
           btn_changeName.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   btn_changeName_func();
               }
           });
       }

    /**
     * @author Zhang Xiaohan
     * btn_changeName_func方法的简述.
     * ＜p＞为更改匿名名称按钮做服务
     */
    private void btn_changeName_func() {
        if(change_time>=5){
            JOptionPane.showMessageDialog(frame, "匿名名称只能更改5次",
                    "更改匿名名称失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String txt=txt_changeName.getText();
        if (txt==null||txt.equals("")){
            JOptionPane.showMessageDialog(frame, "匿名名称不能为空",
                    "更改匿名名称失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String pattern = ".*[\\|\\[#\\]].*";
        boolean isMatch = Pattern.matches(pattern,txt);
        if (isMatch){
            JOptionPane.showMessageDialog(loginFrame, "匿名昵称不能包含特殊字符 [ # ] |",
                    "更改匿名名称失败", JOptionPane.WARNING_MESSAGE);
            return;
        }
        change_time++;
        anonymousName=txt+"(匿名名字"+String.valueOf((int)(Math.random()*100))+")";
        JOptionPane.showMessageDialog(loginFrame, "您的匿名名称为"+anonymousName+",还剩下"+(5-change_time)+"次改匿名名称的机会",
                "更改匿名名称更改", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * @author Zhang Xiaohan
     * btn_search_fun方法的简述.
     * ＜p＞为搜索消息按钮做服务
     */
    private void btn_search_func() {
        String txt=txt_search.getText();
        if (txt==""||txt==null){
            if (searching){
                JOptionPane.showMessageDialog(frame, "搜索内容不能为空，消息记录还原",
                        "搜索失败", JOptionPane.WARNING_MESSAGE);
                searching=false;
                updateMessage();
            }
        }
        else {
            searching=true;
            search_message(txt);
        }
    }

    /**
     * @author Zhang Xiaohan
     * btn_send_message 方法的简述.
     * ＜p＞为发送消息按钮做服务
     */
    private void btn_send_message() {
        String message = txt_msg.getText();
        if(!message.isEmpty()){
            String pattern = ".*[\\|\\[#\\]].*";
            boolean isMatch = Pattern.matches(pattern,message)|| Pattern.matches(pattern, message);
            if (isMatch){
                JOptionPane.showMessageDialog(loginFrame, "发送消息不能包含特殊字符 [ # ] |",
                        "发送失败", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isGroup) {
                client.sendMessage("P2P[#]" + message + "[#]" +client.getUsername() +"[#]"+chatUser);
                receiveMessage( client.getUsername(), message);
                txt_msg.setText("");
            }
            else {
                String notice=noticeName.getText();
                boolean flag=false;
                for (int i=0;i<listModel.size();i++) {
                    if(listModel.get(i).equals(notice)){
                        flag=true;
                        break;
                    }
                }
                if ("GroupChat".equals(notice))
                    flag=false;
                if (notice==null)
                    flag=true;
                if (notice==null||notice.equals(""))
                    flag=true;
                if (!flag){
                    JOptionPane.showMessageDialog(frame, "该用户名不存在",
                            "@功能失败", JOptionPane.WARNING_MESSAGE);
                    noticeName.setText("");
                    return;
                }
                if (notice==null||notice.equals(""))
                    client.sendMessage("GROUP[#]" + message + "[#]" + client.getUsername());
                else {
                    client.sendMessage("GROUP[#]" + message + "[#]" + client.getUsername()+"[#]"+notice);
                    noticeName.setText("");
                }
                txt_msg.setText("");
                receiveMessage( client.getUsername(), message);       // 处理message
            }
        }
        else {
            JOptionPane.showMessageDialog(frame, "发送消息不能为空",
                    "发送失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * @author Zhang Xiaohan
     * search_message 方法的简述.
     * ＜p＞为搜索消息记录按钮做服务
     * @param key 搜索关键字
     */
    private void search_message(String key) {
        boolean isGroup=false;
        if(chatUser.equals("GroupChat"))
            isGroup=true;
        textArea.setText("");
        String user;
        String txt;
        List<String> chatRecords = client.getChatRecord(chatUser);
        for (int j = 0; j < chatRecords.size(); j++) {
            System.out.println(chatRecords.get(j));
            StringTokenizer stringTokenizer = new StringTokenizer(chatRecords.get(j), "[#]");
            user=stringTokenizer.nextToken();
            txt=stringTokenizer.nextToken();
            if (txt.contains(key)||isGroup&&user.contains(key))
                receiveMessage(user,txt);
        }
    }

    /**
     * @author Zhang Xiaohan
     * sameToOtherName 方法的简述.
     * ＜p＞当前列表是否有该用户
     * @param user 用户名
     * @return true when exists ,or false
     */
    public boolean sameToOtherName(String user) {
        for(int i=0;i<listModel.size();i++){
            if(user.equals(listModel.get(i)))
                return true;
        }
        return false;
    }

    /**
     * @author Zhang Xiaohan
     * updateMessage 方法的简述.
     * ＜p＞更新消息框显示内容
     */
    public void updateMessage() {
        if(chatUser.equals("GroupChat"))
            txt_name.setText(chatUser);
        else {
            if(Account.check(chatUser) % 2 == 1)
                txt_name.setText(chatUser+"(他在线上)");
            else
                txt_name.setText(chatUser+"(他不在线)");
        }
        textArea.setText("");
        List<String> chatRecords = client.getChatRecord(chatUser);
        try {
            for (int j = 0; j < chatRecords.size(); j++) {
                System.out.println(chatRecords.get(j));
                StringTokenizer stringTokenizer = new StringTokenizer(chatRecords.get(j), "[#]");
                receiveMessage(stringTokenizer.nextToken(), stringTokenizer.nextToken());
            }
        }
        catch (NullPointerException e){

        }

    }

    /**
     * @author Zhang Xiaohan
     * exitNow方法的简述.
     * ＜p＞为退出按钮做服务
     */
    private void exitNow() {
        // TODO Auto-generated method stub
        client.sendMessage("[OFFLINE]");
        client.disconnect();
    }
    /**
     * @author Zhang Xiaohan
     * loginGUI 方法的简述.
     * ＜p＞登录界面初始化
     */
    private void loginGUI() {
        for (int i = 0; i < DEFAULT_FONT.length; i++)
            UIManager.put(DEFAULT_FONT[i], new Font("Microsoft YaHei UI", Font.PLAIN, 15));
        loginPanel = new JPanel();
        GridBagLayout gridBagLayout=new GridBagLayout();
        loginPanel.setLayout(gridBagLayout);
        JLabel label;
        label=new JLabel("用户名");
        loginPanel.add(label,new GBC(0,0).
                setFill(GBC.BOTH).setIpad(30, 10).setWeight(100,0).setInsets(0,40,0,30));
        loginguser=new JTextField();
        loginPanel.add(loginguser,new GBC(1,0).
                setFill(GBC.BOTH).setIpad(100, 10).setWeight(100,0).setInsets(0,40,0,30));
        label=new JLabel("密码");
        loginPanel.add(label,new GBC(0,1).
                setFill(GBC.BOTH).setIpad(30, 10).setWeight(100,0).setInsets(0,40,0,30));
        loginpass=new JTextField();
        loginPanel.add(loginpass,new GBC(1,1).
                setFill(GBC.BOTH).setIpad(100, 10).setWeight(100,0).setInsets(0,40,0,30));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        loginFrame = new JFrame("JAVA聊天小平台");
        loginFrame.setSize(500, 300);
        loginFrame.setResizable(false);
        loginFrame.setIconImage(toolkit.createImage(ClientView.class.getResource("chat.png")));
        loginFrame.setLayout(new BorderLayout());
        loginFrame.add(loginPanel, "Center");
        JPanel panel_Buttons=new JPanel();
        btn_zhuce=new JButton("注册");
        btn_login=new JButton("登录");
        panel_Buttons.add(btn_zhuce);
        panel_Buttons.add(btn_login);
        loginFrame.add(panel_Buttons,"South");
        label=new JLabel("登录");
        loginFrame.add(label,"North");
        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;
        loginFrame.setLocation((screenWidth - loginFrame.getWidth()) / 2,
                (screenHeight - loginFrame.getHeight()) / 2);
        loginFrame.setDefaultCloseOperation(loginFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
        addLoginListeners();
    }

    /**
     * @author Zhang Xiaohan
     * addLoginListeners 方法的简述.
     * ＜p＞设置login界面的监听器
     */
    private void addLoginListeners() {
        btn_zhuce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user, pass;
                user = loginguser.getText();
                pass = loginpass.getText();
                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "用户名和密码不能为空",
                            "注册失败", JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    String pattern = ".*[\\|\\[#\\]].*";
                    boolean isMatch = Pattern.matches(pattern, user)|| Pattern.matches(pattern, pass);
                    if (user.length()<=5||isMatch){
                        JOptionPane.showMessageDialog(loginFrame, "用户名长度必须大于5，用户名和密码不能包含特殊字符 [ # ] |",
                                "注册失败", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (!Account.isExistUser(user)) {
                        System.out.println("该用户不存在，注册账号");
                        Account.register(user, pass);
                        JOptionPane.showMessageDialog(loginFrame, "欢迎"+user+"成为我们新的一员",
                                "注册成功", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(loginFrame, "用户名已存在",
                                "注册失败", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }});
        btn_login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user, pass;
                user = loginguser.getText();
                pass = loginpass.getText();
                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "用户名和密码不能为空",
                            "登录失败", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else {
                    if(Account.check(user) % 2 == 1) {
                        JOptionPane.showMessageDialog(loginFrame, "该用户正在线上",
                                "登录失败", JOptionPane.WARNING_MESSAGE);
                        System.out.println("该用户正在线上");
                        return;
                    }
                    else{
                        int status=Account.isUserLogin(user, pass);
                        if (status == 1){
                            JOptionPane.showMessageDialog(loginFrame, "密码错误",
                                    "登录失败", JOptionPane.WARNING_MESSAGE);
                            System.out.println("密码错误");
                            return;
                        }
                        else if (status==2){
                            JOptionPane.showMessageDialog(loginFrame, "用户名不存在",
                                    "登录失败", JOptionPane.WARNING_MESSAGE);
                            System.out.println("用户名存在");
                            return;
                        }
                        else if (status==3){
                            JOptionPane.showMessageDialog(loginFrame, "后台发生故障",
                                    "登录失败", JOptionPane.WARNING_MESSAGE);
                            System.out.println("密码错误");
                            return;
                        }
                    }
                }
                System.out.println("验证成功");
                login(user);
            }
        });
    }

    /**
     * @author Zhang Xiaohan
     * login 方法的简述.
     * ＜p＞登陆成功初始化
     */
    public void login(String user) {
        initialGUI();
        addListeners();
        loginFrame.dispose();
        client.connect(serverIP, Integer.parseInt(serverPORT), user);
        System.out.println("ClientView的用户名为" + user);
        isGroup = true;
        frame.setVisible(true);
        noCheck=0;
        txt_user.setText(client.getUsername());
    }
    /**
     * @author Zhang Xiaohan
     * bementioned 方法的简述.
     * ＜p＞为被@功能做服务
     * @param user  @你的人的用户名
     */
    public void bementioned(String user) {
        JOptionPane.showMessageDialog(frame, "你被用户" + user + "@了",
                "新消息提醒", JOptionPane.OK_OPTION);
    }

    /**
     * @author Zhang Xiaohan
     * initialGUI 方法的简述.
     * ＜p＞登陆成功 用户界面的初始化
     */
    private void initialGUI() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        UIManager.put("RootPane.setupButtonVisible", false);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setForeground(Color.gray);
        txt_msg = new JTextArea();
        btn_start = new JButton("连接");
        btn_stop = new JButton("退出");
        btn_send = new JButton("发送");
        btn_sendFile = new JButton("发送文件");
        btn_AnonymousSend=new JButton("匿名发送");
        listModel = new DefaultListModel();
        userList = new JList(listModel);
        northPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        northPanel.setLayout(gridBagLayout);

        constraints.insets = new Insets(0, 5, 0, 5);
        constraints.fill = GridBagConstraints.BOTH;

        JLabel label;

        constraints.weightx = 1.0;
        label = new JLabel("用户名：");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        txt_user=new JTextField("");
        txt_user.setEditable(false);
        gridBagLayout.setConstraints(txt_user, constraints);
        northPanel.add(txt_user);

        constraints.weightx = 1.0;
        label=new JLabel("当前对话框：");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        txt_name = new JTextField();
        txt_name.setText("");
        txt_name.setEditable(false);
        gridBagLayout.setConstraints(txt_name, constraints);
        northPanel.add(txt_name);


        gridBagLayout.setConstraints(btn_stop, constraints);
        northPanel.add(btn_stop);
        northPanel.setBorder(BorderFactory.createTitledBorder("连接信息"));

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(BorderFactory.createTitledBorder("聊天消息"));

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(BorderFactory.createTitledBorder("用户列表"));

        msgScroll = new JScrollPane(txt_msg);

        southPanel = new JPanel(new BorderLayout());
        southPanel.add(msgScroll, "Center");
        inner_north_panel = new JPanel();

        gridBagLayout = new GridBagLayout();
        constraints = new GridBagConstraints();

        inner_north_panel.setLayout(gridBagLayout);

        constraints.insets = new Insets(0, 5, 0, 5);
        constraints.fill = GridBagConstraints.BOTH;

        constraints.weightx = 1.0;
        label = new JLabel("搜索聊天记录：");
        gridBagLayout.setConstraints(label, constraints);
        inner_north_panel.add(label);

        constraints.weightx = 3.0;
        txt_search=new JTextField("");
        gridBagLayout.setConstraints(txt_search, constraints);
        inner_north_panel.add(txt_search);

        constraints.weightx = 1.0;
        btn_search=new JButton("搜索");
        gridBagLayout.setConstraints(btn_search, constraints);
        inner_north_panel.add(btn_search);

        constraints.weightx = 1.0;
        label=new JLabel("更改匿名名字：");
        gridBagLayout.setConstraints(label, constraints);
        inner_north_panel.add(label);

        constraints.weightx = 3.0;
        txt_changeName = new JTextField();
        txt_changeName.setText("");
        gridBagLayout.setConstraints(txt_changeName, constraints);
        inner_north_panel.add(txt_changeName);

        constraints.weightx = 1.0;
        btn_changeName=new JButton("更改");
        gridBagLayout.setConstraints(btn_changeName, constraints);
        inner_north_panel.add(btn_changeName);

        southPanel.add(inner_north_panel,"North");

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        label=new JLabel("请输入希望@的人：");
        noticeName=new JTextField();
        noticeName.setMargin(new Insets(5, 1, 5, 120));
        btn_send.setMargin(new Insets(5, 60, 5, 20));
        btn_sendFile.setMargin(new Insets(5, 20, 5, 20));
        panel.add(label);
        panel.add(noticeName);
        panel.add(btn_sendFile);
        panel.add(btn_AnonymousSend);
        panel.add(btn_send);

        southPanel.add(panel, "South");
        southPanel.setBorder(BorderFactory.createTitledBorder("发送"));

        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
        rightSplit.setDividerLocation(350);

        eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(rightSplit, "Center");

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, eastPanel);
        centerSplit.setDividerLocation(250);

        frame = new JFrame("JAVA聊天小平台");
        frame.setSize(1024, 768);

        frame.setIconImage(toolkit.createImage(ClientView.class.getResource("chat.png")));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");

        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;

        frame.setLocation((screenWidth - frame.getWidth()) / 2,
                (screenHeight - frame.getHeight()) / 2);
        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        txt_msg.getInputMap().put(enter, "none");
        txt_search.getInputMap().put(enter, "none");
        txt_changeName.getInputMap().put(enter, "none");
        frame.setResizable(false);
    }

    /**
     * @author Zhang Xiaohan
     * receiveMessage 方法的简述.
     * ＜p＞收到消息 消息框添加记录
     * @param user 发送消息的人
     * @param message 消息内容
     */
    public void receiveMessage(String user, String message) {
        int length=0;
        textArea.append(user + " :\r\n");
        textArea.append("        ");
        int len=message.length(),i=0;
        while (i<len){
            if (message.charAt(i)>=0x4e00&&message.charAt(i)<=0x9fa5)
                length++;
            length++;
            textArea.append(String.valueOf(message.charAt(i)));
            if (length>=45){
                textArea.append("\r\n");
                textArea.append("        ");
                length=0;
            }
            i++;
        }
        if (length==0)
            textArea.append("\r\n");
        else
            textArea.append("\r\n\r\n");
    }

    /**
     * @author Zhang Xiaohan
     * updataIndex 方法的简述.
     * ＜p＞更新listModel 用户名添加后缀（New Message）  表示他收到消息了
     * @param name 发送消息的用户名
     */
    public void updateInndex(String name){
        noCheck = 100;
        for (int i = 0; i < listModel.size(); i++) {
            String s = (String)listModel.get(i);
            if (s.equals(name)||s.equals(name+"(New Message)")) {
                listModel.remove(i);
                noCheck=100;
                if(s.equals(name))
                    listModel.add(0, name + "(New Message)");
                else listModel.add(0, name);
                return;
            }
        }
    }
    /**
     * @author Zhang Xiaohan
     * updateGUI 方法的简述.
     * ＜p＞更具收到的消息更新GUI
     * @param command  消息类型
     * @param  message 消息具体内容
     * @param  sender 发送消息的人
     */
    public void updateGUI(String command, String message, String sender) {
        if (command.equals("GROUP")) {
            if (chatUser.equals("GroupChat"))
                receiveMessage(sender, message);
            else{
                updateInndex("GroupChat");
            }
        }
        else if (command.equals("P2P")) {
            if (chatUser.equals(sender))
                receiveMessage(sender, message);            //GUI直接更新，是对方
            else{
                updateInndex(sender);                       // 没看懂
            }
        }
        else if (command.equals("OFFLINE")) {
            if(message.equals(chatUser)){
                txt_name.setText(message+"（他不在线）");
            }
        }
        else if(command.equals("ONLINE"))    {
            if (message.equals(chatUser)) {
                txt_name.setText(message+"（他在线上）");
            }
        }
        else if (command.equals("FILE")) {
            JOptionPane.showMessageDialog(frame, message, "系统消息", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            System.out.println("收到错误消息命令"+command+" "+message+"" +sender);
        }
        frame.repaint();
    }

    /**
     * @author Zhang Xiaohan
     * setChatUser 方法的简述.
     * ＜p＞更改chatUser
     * @param chatUser 更改的用户
     */
    public void setChatUser(String chatUser) {
        this.chatUser=chatUser;
    }

    /**
     * @author Zhang Xiaohan
     * getTxt_namr 方法的简述.
     * ＜p＞更具收到的消息更新GUI
     * @return 用户窗口
     */
    public JTextField getTxt_name() {
        return txt_name;
    }
}
