package client;

import java.awt.*;

/**
 * GBC的描述
 * ＜p＞前端布局管理器
 */
public class GBC extends GridBagConstraints
{

    /**
     * 构造方法 的描述
     * @param gridx 左上角横坐标
     * @param gridy 左上角纵坐标
     */
    public GBC(int gridx, int gridy)
    {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    /**
     * 构造方法 的描述
     * @param gridx 左上角横坐标
     * @param gridy 左上角纵坐标
     * @param gridwidth 宽度
     * @param gridheight 高度
     */
    public GBC(int gridx, int gridy, int gridwidth, int gridheight)
    {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
    }

    /**
     * @author Zhang Xiaohan
     * setAnchor 方法的简述.
     * ＜p＞设置对齐方式
     */
    public GBC setAnchor(int anchor)
    {
        this.anchor = anchor;
        return this;
    }

    /**
     * @author Zhang Xiaohan
     * setFill 方法的简述.
     * ＜p＞设置是否拉伸以及拉伸方向
     * @param fill 拉伸参数
     * @return 修改后的GBC
     */
    public GBC setFill(int fill)
    {
        this.fill = fill;
        return this;
    }

    /**
     * @author Zhang Xiaohan
     * sendMessage 方法的简述.
     * ＜p＞设置x y方向上的增量
     * @param weightx x方向上的增量
     * @param weighty y方向上的增量
     * @return 修改后的GBC
     */
    public GBC setWeight(double weightx, double weighty)
    {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    /**
     * @author Zhang Xiaohan
     * setInsets 方法的简述.
     * ＜p＞设置外部填充
     * @param distance 填充大小
     * @return 修改后的GBC
     */
    public GBC setInsets(int distance)
    {
        this.insets = new Insets(distance, distance, distance, distance);
        return this;
    }

    /**
     * @author Zhang Xiaohan
     * setInsets 方法的简述.
     * ＜p＞设置外部填充
     * @param top 头部填充
     * @param left 左部填充
     * @param bottom 底部填充
     * @param right 右部填充
     * @return 修改后的GBC
     */
    public GBC setInsets(int top, int left, int bottom, int right)
    {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    /**
     * @author Zhang Xiaohan
     * setInsets 方法的简述.
     * ＜p＞设置内部填充
     * @param ipadx x方向填充
     * @param ipady y方向填充
     * @return 修改后的GBC
     */
    public GBC setIpad(int ipadx, int ipady)
    {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}