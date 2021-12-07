
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

//主要应用类，又名框架类
public class la2  extends JFrame {

    //应用程序窗口尺寸作为常量

    private static final int WIDTH = 540;
    private static final int HEIGHT = 300;
    private static final String Sum = null;

    //用于读取数字值的文本字段，
    //作为在不同方法中共享的组件
    private JTextField textFieldX;
    private JTextField textFieldY;
    private JTextField textFieldZ;
    //显示结果的文本框，
    // 作为不同方法共享的组件
    private JTextField textFieldResult;
    private JTextField textFieldMplus;

    //一组单选按钮，保证组内选择的唯一性
    private ButtonGroup radioButtons = new ButtonGroup();
    //用于显示单选按钮的容器
    private Box hboxFormulaType = Box.createHorizontalBox();
    private int formulaId = 1;

    //(формула №1:)
    public Double calculate1(Double x, Double y, Double z)
    {
        return (1/Math.sqrt(x)+Math.cos(Math.exp(y)+Math.cos(Math.pow(z,2))))/ (Math.pow(Math.sqrt(Math.log(Math.exp( Math.pow(1 + z, 2))+Math.sqrt(Math.exp(Math.cos(y))+Math.pow(Math.sin(Math.PI*x),2)))),1.0/3));
    }

    //(формула №2)

    public Double calculate2 (Double x, Double y, Double z)
    {
        return (Math.pow((Math.sqrt(y+Math.pow(x,3))),1.0/z))/(Math.log(Math.exp(z)));
    }

    //向面板添加按钮的辅助方法
    private void addRadioButton(String buttonName, final int formulaId)
    {
        JRadioButton button = new JRadioButton(buttonName);

        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                la2.this.formulaId = formulaId;
            }
        });
        radioButtons.add(button);
        hboxFormulaType.add(button);
    }
    //类构造函数
    public la2() {
        super("Вычисление формулы");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        //将应用程序窗口置于屏幕中央
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);
        //在左边添加胶水
        hboxFormulaType.add(Box.createHorizontalGlue());		//声明: private Box hboxFormulaType = Box.createHorizontalBox();
        addRadioButton("Формула 1", 1);
        addRadioButton("Формула 2", 2);
        //从组中设置选定的第一个按钮
        radioButtons.setSelected( radioButtons.getElements().nextElement().getModel(), true);
        //在右侧添加胶水
        hboxFormulaType.add(Box.createHorizontalGlue());
        //使用 BorderFactory 类为框设置边框
        hboxFormulaType.setBorder( BorderFactory.createLineBorder(Color.YELLOW));

        //创建一个包含 X、Y 和 Z 输入字段的区域
        JLabel labelForX = new JLabel("X:");
        textFieldX = new JTextField("0", 7);
        textFieldX.setMaximumSize(textFieldX.getPreferredSize());

        JLabel labelForY = new JLabel("Y:");
        textFieldY = new JTextField("0", 7);
        textFieldY.setMaximumSize(textFieldY.getPreferredSize());

        JLabel labelForZ = new JLabel("Z:");
        textFieldZ = new JTextField("0", 7);
        textFieldZ.setMaximumSize(textFieldZ.getPreferredSize());

        //创建一个容器“水平堆叠的盒子”
        Box hboxVariables = Box.createHorizontalBox();
        //y使用 BorderFactory 类为框设置边框
        hboxVariables.setBorder( BorderFactory.createLineBorder(Color.RED) );
        //添加胶水
        //hboxVariables.add(Box.createHorizontalGlue());
        hboxVariables.add(Box.createHorizontalStrut(5));

        //为变量 X 添加签名
        hboxVariables.add(labelForX);
        hboxVariables.add(Box.createHorizontalStrut(5));
        //添加文本字段本身以输入 X
        hboxVariables.add(textFieldX);
        //为 X 输入文本框和 Y 标题之间的空间添加一个 30 像素的“间隔”
        //hboxVariables.add(Box.createHorizontalStrut(30));
        hboxVariables.add(Box.createHorizontalGlue());
        //为变量 Y 添加签名
        hboxVariables.add(labelForY);
        hboxVariables.add(Box.createHorizontalStrut(5));
        hboxVariables.add(textFieldY);
        //为 Y 输入文本框和 Z 标题之间的空间添加一个 30 像素的“间隔”
        //hboxVariables.add(Box.createHorizontalStrut(30));
        hboxVariables.add(Box.createHorizontalGlue());
        //Z为变量 Z 添加签名
        hboxVariables.add(labelForZ);
        hboxVariables.add(Box.createHorizontalStrut(5));
        hboxVariables.add(textFieldZ);

        //添加“胶水”以获得与右边缘的最大距离
        //hboxVariables.add(Box.createHorizontalGlue());

        //为带有结果的字段创建标题
        JLabel labelForResult = new JLabel("Результат:");
        //创建用于输出结果的文本字段，初始值 - 0
        textFieldResult = new JTextField("0", 100);
        //setMaximumSize - 组件的最大大小等于所需
        textFieldResult.setMaximumSize(	textFieldResult.getPreferredSize() );
        //创建一个容器“水平堆叠的盒子”
        Box hboxResult = Box.createHorizontalBox();
        hboxResult.add(Box.createHorizontalGlue());
        //为结果添加签名
        hboxResult.add(labelForResult);
        hboxResult.add(Box.createHorizontalStrut(10));
        //添加文本框以显示结果
        hboxResult.add(textFieldResult);
        hboxResult.add(Box.createHorizontalGlue());
        hboxResult.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        //为按钮创建区域
        JButton buttonCalc = new JButton("Вычислить");
        //定义并注册按钮单击处理程序
        buttonCalc.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                try
                {
                    //获取 X 值
                    Double x = Double.parseDouble(textFieldX.getText());
                    Double y = Double.parseDouble(textFieldY.getText());
                    Double z = Double.parseDouble(textFieldZ.getText());
                    //计算结果
                    Double result = null;
                    if (formulaId==1)

//当按下用于选择公式的相应按钮时，应出现一个额外的侧窗口，当程序窗口关闭时关闭。
//它应包含所选求解方法的相应公式。
//您只需要创建 3 个窗口（右、左、下/上），而不是 4 个，因为 计算值将显示在主窗口的输入行中。 然而，也可以制作 4 个窗口，其中 4 个窗口代表一个可允许的计算动作表。

                        result = calculate1(x, y, z);
                    else if ( formulaId == 2)
                        result = calculate2(x, y, z);
                    //设置标签文本等于结果
                    textFieldResult.setText(result.toString());
                }
                catch (NumberFormatException ex)
                {
                    //如果出现异常情况，显示一条消息
                    JOptionPane.showMessageDialog(la2.this, "Ошибка в формате записи числа с плавающей точкой",
                            "Ошибочный формат числа", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton buttonReset = new JButton("Очистить поля");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                textFieldX.setText("0");
                textFieldY.setText("0");
                textFieldZ.setText("0");
                textFieldResult.setText("0");
            }
        });

        //为带有结果的字段创建标题
        JLabel labelForMplus = new JLabel(":");
        //创建用于输出结果的文本字段，初始值 - 0
        textFieldMplus = new JTextField("0", 15);
        //setMaximumSize - 组件的最大大小等于所需
        textFieldMplus.setMaximumSize(	textFieldMplus.getPreferredSize() );
        //创建一个容器“水平堆叠的盒子”
        Box hboxMplus = Box.createHorizontalBox();
        hboxMplus.add(Box.createHorizontalGlue());
        //为结果添加签名
        hboxMplus.add(labelForMplus);
        hboxMplus.add(Box.createHorizontalStrut(10));
        //添加文本框以显示结果
        hboxMplus.add(textFieldMplus);
        hboxMplus.add(Box.createHorizontalGlue());


        JButton buttonMplus = new JButton("M+");
        //定义并注册按钮单击处理程序
        buttonMplus.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                Double Sum = Double.parseDouble(textFieldMplus.getText() ) + Double.parseDouble(textFieldResult.getText() );
                //设置标签文本等于结果
                textFieldMplus.setText(Sum.toString());
            }

        });

        JButton buttonMclear = new JButton("MC");
        buttonMclear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) {
                textFieldMplus.setText("0") ;
            }

        });

        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.add(Box.createHorizontalStrut(50) );
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(40));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalStrut(40) );

        Box ButtonsSumResult = Box.createHorizontalBox();
        ButtonsSumResult.add(Box.createHorizontalStrut(20) );
        ButtonsSumResult.add(buttonMplus);
        ButtonsSumResult.add(Box.createHorizontalStrut(30) );
        ButtonsSumResult.add(buttonMclear);
        ButtonsSumResult.add(Box.createHorizontalGlue() );
        ButtonsSumResult.add(hboxMplus);
        ButtonsSumResult.add(Box.createHorizontalStrut(30) );
        ButtonsSumResult.add(Box.createHorizontalGlue() );


        // 在 BoxLayout 中将区域链接在一起

        //创建一个“垂直堆叠的盒子”容器
        Box contentBox = Box.createVerticalBox();
        //在顶部添加胶水 V1
        contentBox.add(Box.createVerticalGlue());
        //添加带有公式选择的容器

        //添加带有变量的容器
        contentBox.add(Box.createVerticalGlue() );
        contentBox.add(hboxVariables);
        //添加带有计算结果的容器
        contentBox.add(Box.createVerticalGlue() );
        contentBox.add(hboxResult);
        //添加带有按钮的容器
        contentBox.add(Box.createVerticalGlue() );
        contentBox.add(hboxButtons);
        contentBox.add(ButtonsSumResult);
        //contentBox.add(ButtonsClear);
        contentBox.add(Box.createVerticalGlue());
        // 在主窗口的内容区域放置“垂直框”
        contentBox.add(hboxFormulaType);
        getContentPane().add(contentBox, BorderLayout.CENTER);
    }

    //主类方法
    public static void main(String[] args)
    {
        la2 frame = new la2();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}


