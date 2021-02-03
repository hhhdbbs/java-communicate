import client.Client;
import client.ClientView;
import org.jvnet.substance.*;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.EmeraldDuskSkin;
import org.jvnet.substance.theme.SubstanceTerracottaTheme;
import org.jvnet.substance.watermark.SubstanceBubblesWatermark;

import javax.swing.*;

public class ClientMain {
    /**
     * ClientMain的描述
     * ＜p＞ 客户端主代码
     */

    /**
     * @author Zhang Xiaohan
     * main方法的简述.
     * ＜p＞ 初始化客户端资源
     */
  public static void main(String args[]) {
    try {
      UIManager.setLookAndFeel(new SubstanceLookAndFeel());
      UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
      JFrame.setDefaultLookAndFeelDecorated(true);
      JDialog.setDefaultLookAndFeelDecorated(true);
      SubstanceLookAndFeel.setCurrentTheme(new SubstanceTerracottaTheme());
      SubstanceLookAndFeel.setCurrentTheme("org.jvnet.substance.theme.SubstanceAquaTheme");
      SubstanceLookAndFeel.setCurrentButtonShaper("org.jvnet.substance.button.SubstanceButtonShaper");
    }catch (Exception e) {
      System.err.println("Something went wrong!");
    }
    ClientView cv = new ClientView();
    Client c = new Client();
    cv.setClientThread(c);
    c.setClientViewThread(cv);
    c.start();
    cv.start();
  }
}
