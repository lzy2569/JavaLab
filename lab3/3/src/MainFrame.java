import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.io.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
    static final int WIDTH=940;
    static final int HEIGHT=480;
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();
    private GornerTableModel data;
    private DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance();
    static Double coefficients[]=null;
    private AboutFrame Aframe=new AboutFrame();
    private JTextField t1_1;
    private JTextField t1_2;
    private JTextField t1_3;
    private JLabel l1_1;
    private JLabel l1_2;
    private JLabel l1_3;
    private Box hBoxT1;
    private JButton btnCalc = null;
    private JButton btnClear = null;
    private JFileChooser fileChoose;
    private JMenuItem menuFileToText;
    private JMenuItem menuFileToGraphd;
    private JMenuItem menuFileToCsv;
    private JMenuItem menuTableSimple;
    private JMenuItem menuTableFind;
    private JMenuItem menuHelpAbout;

    public MainFrame()
    {
        super("Лаба3");
        setSize(WIDTH,HEIGHT);
        Toolkit kit=Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2,(kit.getScreenSize().height - HEIGHT)/2);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Файл");//文件
        JMenu menuTable = new JMenu("Таблица");//桌子
        JMenu menuHelp = new JMenu("Справка");//参考

        fileChoose=new JFileChooser();
        menuFileToText =new JMenuItem("Сохранить в текстовый файл");//保存到文本文件
        menuFileToText.addActionListener
        (new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                fileChoose.setCurrentDirectory(new File("~"));
                if(fileChoose.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION){
                    File file=fileChoose.getSelectedFile();
                    try
                    {
                        PrintStream out=new PrintStream(file);
                        out.println("Результат таболурование по схеме горнера");//结果是根据霍纳方案制表
                        out.print("Многочлен: ");//多项式
                        for(int i=0;i<coefficients.length;i++)
                        {
                            out.print(coefficients[i]+"*X^"+(coefficients.length-i-1));
                            if(i!=coefficients.length-1)
                                out.print("+");
                            else
                                out.print("\n");
                        }
                        out.print("Интервал от: ");//间隔从
                        out.print(t1_1.getText());
                        out.print(" до: ");//到
                        out.print(t1_2.getText());
                        out.print(" с шагом: ");//有步骤
                        out.println(t1_3.getText());
                        out.println("=========================");
                        for(int y=0;y<data.getRowCount();y++)
                        {
                            out.print("Значение в точке ");//积分值
                            out.print(formatter.format(data.getValueAt(y, 0)));
                            out.print(" равно ");//等于
                            out.print(formatter.format(data.getValueAt(y, 1)));
                            out.print(", а не по горнеру ");//不是伪造的
                            out.print(formatter.format(data.getValueAt(y, 2)));
                            out.print(" и разница состовляет:");//区别在于：
                            out.print(formatter.format(data.getValueAt(y, 3)));
                            out.print("\n");
                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("Не удалось создать файл");//文件创建失败
                    }

                }
            }
        }
        );
        menuFileToGraphd =new JMenuItem("Сохранить данные для построения графика");//保存绘图数据
        menuFileToGraphd.addActionListener
        (new ActionListener()
        {
          public void actionPerformed(ActionEvent ev)
            {
                fileChoose.setCurrentDirectory(new File("~"));
                if(fileChoose.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                {
                    File file=fileChoose.getSelectedFile();
                    try
                    {
                        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
                        for (int i = 0; i<data.getRowCount(); i++)
                        {
                            out.writeDouble((Double)data.getValueAt(i,0));
                            out.writeDouble((Double)data.getValueAt(i,1));
                        }
                        out.close();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Не удалость создать файл");//文件创建失败
                    }
                }
            }
        }
        );
        menuFileToCsv =new JMenuItem("Сохранить в CSV-файл");//保存到 CSV 文件
        menuFileToCsv.addActionListener
        (new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) {
                fileChoose.setCurrentDirectory(new File("~"));
                if(fileChoose.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                {
                    File file=fileChoose.getSelectedFile();
                    try
                    {
                        PrintStream out=new PrintStream(file);
                        for(int y=0;y<data.getRowCount();y++)
                        {
                            for(int u=0;u<data.getColumnCount();u++){
                                out.print(formatter.format(data.getValueAt(y, u)));
                                if(u!=data.getColumnCount()-1)
                                    out.print(",");
                                else
                                    out.print("\n");
                            }
                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        System.out.println("Не удалось создать файл");//文件创建失败
                    }
                }
            }
        }
        );
        menuTableSimple =new JMenuItem("Найти близкие к простым");//寻找接近简单的
        menuTableSimple.addActionListener
        (new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                renderer.setSSimple(true);
                getContentPane().repaint();
            }
        }
        );
        menuTableFind =new JMenuItem("Найти значения многочлена");//求多项式的值
        menuTableFind.addActionListener
        (new ActionListener()
         {
             public void actionPerformed(ActionEvent ev)
             {
                 String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",//请输入要搜索的值
                         "Поиск значения", JOptionPane.QUESTION_MESSAGE);//寻找意义
                 renderer.setNeedle(value);
                 getContentPane().repaint();
             }
         }
        );

        menuHelpAbout =new JMenuItem("О программе");//关于节目
        menuHelpAbout.addActionListener
        (new ActionListener()
         {
             public void actionPerformed(ActionEvent ev)
             {
                 Aframe.setVisible(true);
             }
         }
        );

        menuFileToText.setEnabled(false);
        menuFileToGraphd.setEnabled(false);
        menuFileToCsv.setEnabled(false);
        menuTableSimple.setEnabled(false);
        menuTableFind.setEnabled(false);
        //menuTableCalc.setEnabled(false);


        menuFile.add(menuFileToText);
        menuFile.add(menuFileToGraphd);
        menuFile.add(menuFileToCsv);
        menuTable.add(menuTableSimple);
        menuTable.add(menuTableFind);
        //menuTable.add(menuTableCalc);
        menuHelp.add(menuHelpAbout);
        menuBar.add(menuFile);
        menuBar.add(menuTable);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
        ///////////
        l1_1 = new JLabel("X изменяется на интервале от:");//X 不同于：
        l1_2 = new JLabel("до:");
        l1_3 = new JLabel("с шагом:");
        t1_1 =new JTextField("0.0",12);
        t1_1.setMaximumSize(t1_1.getPreferredSize());
        t1_2 =new JTextField("1.0",12);
        t1_2.setMaximumSize(t1_2.getPreferredSize());
        t1_3 =new JTextField("0.1",12);
        t1_3.setMaximumSize(t1_3.getPreferredSize());
        Box hBoxUp=Box.createHorizontalBox();
        hBoxUp.add(Box.createHorizontalGlue());
        hBoxUp.add(l1_1);
        hBoxUp.add(t1_1);
        hBoxUp.add(l1_2);
        hBoxUp.add(t1_2);
        hBoxUp.add(l1_3);
        hBoxUp.add(t1_3);
        hBoxUp.add(Box.createHorizontalGlue());

        btnCalc=new JButton("Вычислить");//计算
        btnCalc.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                if((Double.parseDouble(t1_1.getText()))-(Double.parseDouble(t1_2.getText()))>0)
                {
                    System.out.print("Error 1>2");
                    return;
                }
                data = new GornerTableModel(Double.parseDouble(t1_1.getText()),
                        Double.parseDouble(t1_2.getText()),Double.parseDouble(t1_3.getText()),coefficients);
                renderer.setSSimple(false);
                JTable table = new JTable(data);
                table.setDefaultRenderer(Double.class, renderer);
                table.setRowHeight(30);
                hBoxT1.removeAll();
                hBoxT1.add(new JScrollPane(table));
                menuFileToText.setEnabled(true);
                menuFileToGraphd.setEnabled(true);
                menuFileToCsv.setEnabled(true);
                menuTableSimple.setEnabled(true);
                menuTableFind.setEnabled(true);
                getContentPane().validate();
            }
        }
        );

        btnClear=new JButton("Очистить поля");//清除字段
        btnClear.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                t1_1.setText("0.0");
                t1_2.setText("1.0");
                t1_3.setText("0.1");
                menuFileToText.setEnabled(false);
                menuFileToGraphd.setEnabled(false);
                menuFileToCsv.setEnabled(false);
                menuTableSimple.setEnabled(false);
                menuTableFind.setEnabled(false);
                hBoxT1.removeAll();
                hBoxT1.add(new JPanel());
                getContentPane().validate();
            }
        }
        );


        Box hBoxDn=Box.createHorizontalBox();
        hBoxDn.add(Box.createHorizontalGlue());
        hBoxDn.add(btnCalc);
        hBoxDn.add(Box.createHorizontalGlue());
        hBoxDn.add(btnClear);
        hBoxDn.add(Box.createHorizontalGlue());
        hBoxT1 = Box.createHorizontalBox();
        hBoxT1.add(new JPanel());
        getContentPane().add(hBoxUp, BorderLayout.NORTH);
        getContentPane().add(hBoxT1, BorderLayout.CENTER);
        getContentPane().add(hBoxDn, BorderLayout.SOUTH);
    }

    public static void main(String[] args)
    {
        if(args.length==0)
        {
            System.out.print("Незадан многочлен");//多项式未指定
            return;
        }

        coefficients=new Double[args.length];
        int i=0;
        for(String arg:args)
        {
            coefficients[i]=Double.parseDouble(arg);
            i++;
        }
        MainFrame frame=new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}