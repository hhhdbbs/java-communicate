package server;

import java.io.*;
import java.util.*;

/**
 * Account的描述
 * ＜p＞ 作为程序和文件的中间商，处理信息
 */
public class Account {

    /**  设定存放用户账号密码的文件*/
    public final static String USERLIST_FILE = "D:\\javaproject\\ja5\\src\\resource\\user.txt";

    /**  设定记录用户在线状态的文件*/
    private final static String ONLINE_LIST = "D:\\javaproject\\ja5\\src\\resource\\log.txt";

    /**  设定存放聊天记录的文件*/
    private final static String CHATRECORD= "D:\\javaproject\\ja5\\src\\resource\\record.txt";

    /**
     * @author Hu Pengfei
     * getChatRecord 方法的简述.
     * ＜p＞从record文件获取用户的聊天消息记录
     * @param username 用户名
     * @return 消息记录
     */
    public static HashMap<String, ArrayList<String>> getChatRecord(String username){ // 返回username用户的聊天记录
        HashMap<String, ArrayList<String>> chatRecord = new HashMap<String, ArrayList<String>>();
        String strRead;
        String from, to, msg;
        try {
            FileInputStream inputfile = new FileInputStream(CHATRECORD);
            DataInputStream inputdata = new DataInputStream(inputfile);
            InputStreamReader reader = new InputStreamReader(inputfile, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            while ((strRead = br.readLine()) != null) {
                // strRead = new String(strRead.getBytes("ISO-8859-1"),"UTF-8");
                System.out.println("读取日志文件" + strRead);
                StringTokenizer stUser = new StringTokenizer(strRead, "|");
                from = stUser.nextToken();
                to = stUser.nextToken();
                msg = stUser.nextToken();
     //           System.out.println(from + " " + to + " " + msg);
                if(to.equals("GroupChat")) {
                    if (chatRecord.get("GroupChat") == null)
                        chatRecord.put("GroupChat", new ArrayList<String>());
                    chatRecord.get("GroupChat").add(from + "[#]" + msg);
                }
                else if (from.equals(username)) {           // 该用户发送消息
                    if (chatRecord.get(to) == null)
                        chatRecord.put(to, new ArrayList<String>());
                    chatRecord.get(to).add(from + "[#]" + msg);
                }
                else if(to.equals(username)) {      // 该用户被发送消息
                    if (chatRecord.get(from) == null)
                        chatRecord.put(from, new ArrayList<String>());
                    chatRecord.get(from).add(from + "[#]" + msg);
                }
            }
            System.out.println("用户" + username + "的消息记录是" + chatRecord);
            return chatRecord;

        }catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            System.out.println("warning|读写文件时出错!");
        }  catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            System.out.println("warning|读写文件时出错!");
        }
        return new HashMap<String, ArrayList<String>>();
    }

    /**
     * @author Hu Pengfei
     * writerecord 方法的简述.
     * ＜p＞向record文件写用户的聊天消息记录
     * @param user 发送方
     * @param touser 接收方
     * @param msg 消息
     */
    public static void writerecord(String user, String touser, String msg) {
        try {
            RandomAccessFile userFile = new RandomAccessFile(CHATRECORD, "rw");
            userFile.seek(userFile.length()); // 在文件尾部加入新用户信息
            String str = user + "|" + touser + "|" + msg + "\r\n";
            userFile.write(str.getBytes("UTF-8"));
            //userFile.writeUTF(user + "|" + touser + "|" +  msg +"\r\n");
            System.out.println("日志已添加" + user + " " + msg);
            //userFile.close();
            //     OnlineUsers.add(user);
        }catch (FileNotFoundException e) {
            System.out.println("[ERROR] User File has not exist!" + e);
            System.out.println("读文件出错");
        }catch (IOException e) {
            System.out.println("[ERROR]" + e);
            System.out.println("读文件出错");
        }
    }

    /**
     * @author Hu Pengfei
     * isExitUser 方法的简述.
     * ＜p＞判断是否存在用户名
     * @param name 用户名
     * @return true when exists else false
     */
    @SuppressWarnings({ "resource", "deprecation" })
    public static boolean isExistUser(String name) {
        String strRead;
        try {
            FileInputStream inputfile = new FileInputStream(USERLIST_FILE);
            DataInputStream inputdata = new DataInputStream(inputfile);
            InputStreamReader reader = new InputStreamReader(inputfile,"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            while ((strRead = br.readLine()) != null) {
                System.out.println("检测账户信息为" + strRead);
                StringTokenizer stUser = new StringTokenizer(strRead, "|");
                if (stUser.nextToken().equals(name)) {
                    //inputfile.close();
                    //inputdata.close();
                    return true;
                }
            }

        } catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            System.out.println("warning|读写文件时出错!");
        } catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            System.out.println("warning|读写文件时出错!");
        }
        return false;
    }

    /**
     * @author Hu Pengfei
     * isUserLogin 方法的简述.
     * ＜p＞从user文件中判断是否登录成功
     * @param name 用户名
     * @param password  密码
     * @return 检测情况
     */
    @SuppressWarnings("deprecation")
    public static int isUserLogin(String name, String password) {
        String strRead;
        try {
            FileInputStream inputfile = new FileInputStream(USERLIST_FILE);
            DataInputStream inputdata = new DataInputStream(inputfile);
            InputStreamReader reader = new InputStreamReader(inputfile,"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            boolean has_username=false;
            while ((strRead = br.readLine()) != null) {
                System.out.println("检测账户信息" + strRead);
                String[] strings=strRead.split("\\|");
                if (strings[0].equals(name)){
                    if (password.equals(strings[1])) {
                        inputfile.close();
                        return 0;
                    }
                    else {
                        inputfile.close();
                        return 1;
                    }
                }
            }
            inputfile.close();
            return 2;
        } catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            System.out.println("warning|读写文件时出错!");
            return 3;
        } catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            System.out.println("warning|读写文件时出错!");
            return 3;
        }
    }

    /**
     * @author Hu Pengfei
     * register 方法的简述.
     * ＜p＞向user文件中写入新的用户信息
     * @param user 用户名
     * @param pass 密码
     */
    public static void register(String user, String pass) {
        try {

            @SuppressWarnings("resource")
            RandomAccessFile userFile = new RandomAccessFile(USERLIST_FILE, "rw");
            userFile.seek(userFile.length()); // 在文件尾部加入新用户信息
            String str= user + "|" + pass + "\r\n";
            userFile.write(str.getBytes("UTF-8"));
            //userFile.close();
            System.out.println("注册成功,自动登录");

            //     OnlineUsers.add(user);
        }catch (FileNotFoundException e) {
            System.out.println("[ERROR] User File has not exist!" + e);
            System.out.println("读文件出错");
        }catch (IOException e) {
            System.out.println("[ERROR]" + e);
            System.out.println("读文件出错");
        }
    }

    /**
     * @author Hu Pengfei
     * login 方法的简述.
     * ＜p＞登陆成功，向log文件写入登录状态
     * @param user 用户名
     */
    public static void login(String user) {
        try {
            RandomAccessFile userFile = new RandomAccessFile(ONLINE_LIST, "rw");
            userFile.seek(userFile.length()); // 在文件尾部加入新用户信息
            String str = user + "\r\n";
            userFile.write(str.getBytes("UTF-8"));
            //userFile.close();
            System.out.println("日志已添加" + user + "上线");
            //     OnlineUsers.add(user);
        }catch (FileNotFoundException e) {
            System.out.println("[ERROR] User File has not exist!" + e);
            System.out.println("读文件出错");
        }catch (IOException e) {
            System.out.println("[ERROR]" + e);
            System.out.println("读文件出错");
        }
    }

    /**
     * @author Hu Pengfei
     * exituser 方法的简述.
     * ＜p＞用户下线，向log文件写入下线状态
     * @param user 用户名
     */
    public static void exituser(String user) {
        try {
            RandomAccessFile userFile = new RandomAccessFile(ONLINE_LIST, "rw");
            userFile.seek(userFile.length()); // 在文件尾部加入新用户信息
            String str = user + "\r\n";
            userFile.write(str.getBytes("UTF-8"));
            //userFile.close();
            System.out.println("日志已添加" + user + "退出");
            //     OnlineUsers.add(user);
        }catch (FileNotFoundException e) {
            System.out.println("[ERROR] User File has not exist!" + e);
            System.out.println("读文件出错");
        }catch (IOException e) {
            System.out.println("[ERROR]" + e);
            System.out.println("读文件出错");
        }
    }

    /**
     * @author Hu Pengfei
     *check 方法的简述.
     * ＜p＞从log文件中检测登陆状态
     * @param name 用户名
     * @return 登陆状态
     */
    public static int check(String name) {
        String strRead;
        int sum = 0;
        try {
            FileInputStream inputfile = new FileInputStream(ONLINE_LIST);
            DataInputStream inputdata = new DataInputStream(inputfile);
            InputStreamReader reader = new InputStreamReader(inputfile,"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            while ((strRead = br.readLine()) != null) {
                if (strRead.equals(name)) {
                    sum++;
                }
            }
            inputfile.close();
            inputdata.close();
            return sum;
        } catch (FileNotFoundException fn) {
            System.out.println("[ERROR] User File has not exist!" + fn);
            System.out.println("warning|读写文件时出错!");
        } catch (IOException ie) {
            System.out.println("[ERROR] " + ie);
            System.out.println("warning|读写文件时出错!");
        }
        return sum;
    }

    /**
     * @author Hu Pengfei
     * saveToSpecificUser 方法的简述.
     * ＜p＞写入个人聊天信息
     * @param username 发送方
     * @param  dest 接收方
     * @param  message 消息内容
     */
    public static void saveToSpecificUser(String username,String dest,String message){
        try {
            FileInputStream inputfile = new FileInputStream(USERLIST_FILE);
            DataInputStream inputdata = new DataInputStream(inputfile);
            InputStreamReader reader = new InputStreamReader(inputfile,"UTF-8");
            BufferedReader br = new BufferedReader(reader);
            boolean has_username=false;
            String strRead;
            while ((strRead = br.readLine()) != null) {
                String[] strings=strRead.split("\\|");
                if (strings[0].equals(dest))
                    Account.writerecord(username,dest, message);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @author Hu Pengfei
     * saveToAll 方法的简述.
     * ＜p＞写入群聊消息记录
     * @param user 用户名
     * @param Group  群聊
     * @param msg 消息内容
     */
    public static void saveToAll(String user,String Group,String msg) {
        Account.writerecord(user,Group, msg);
    }
}
