import server.ChatServer;
import server.FileServer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {
  /**
   * ServerMain的描述
   * ＜p＞ 服务端主代码
   */

  /**
   * @author Hu Pengfei
   * main 方法的简述.
   * ＜p＞初始化服务端资源
   */
  public static void main(String args[]) {
    try {
      ChatServer cs = new ChatServer(8090);
      FileServer fs = new FileServer(8091);
      cs.start();
      fs.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
