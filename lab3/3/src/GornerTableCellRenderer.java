import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
public class GornerTableCellRenderer implements TableCellRenderer//Gorner 表单元格渲染器 表格单元渲染器
{
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();
    private String needle = null;
    private Boolean ssimple=false;
    private DecimalFormat formatter =(DecimalFormat)NumberFormat.getInstance();

    private boolean isSimple(int n)
    {
        double d=(double)n;
        for(int i=2;i<n;i++)
        {
            double id=(double)i;
            double t=(d/id)-(int)(d/id);
            if(t==0)
            {
                return false;
            }
        }
        return true;
    }

    public GornerTableCellRenderer()//Gorner 表单元格渲染器
    {
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble =	formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);
        panel.add(label);
    }
    //获取表格单元格渲染器组件
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int col)
    {
        String formattedDouble = formatter.format(value);
        label.setText(formattedDouble);
        boolean simple=false;
        double d=Double.parseDouble(formattedDouble);
        if(ssimple)
        {
            float y=(float)(d-(int)(d));
            int s=0;
            if(y>=(float)0.9)
                s=(int)((d)+1);
            if(y<=(float)0.1)
                s=(int)d;
            if(s>1 && isSimple(s))
            {
                simple=true;
            }
        }
        if ((col==1) && needle!=null && needle.equals(formattedDouble))
        {
            panel.setBackground(Color.RED);
        }
        else
        {
            panel.setBackground(Color.WHITE);
        }
        if(simple)
            panel.setBackground(Color.GREEN);
        if(d>0)
        {
            panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        }
        else
        {
            if(d==0)
            {
                panel.setLayout(new FlowLayout(FlowLayout.CENTER));
            }else{
                panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            }
        }
        return panel;
    }

    public void setNeedle(String needle) {
        this.needle = needle;
    }

    public void setSSimple(boolean ssimple) {
        this.ssimple = ssimple;
    }
}