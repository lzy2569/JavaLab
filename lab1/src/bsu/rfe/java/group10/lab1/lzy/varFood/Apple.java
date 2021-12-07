package bsu.rfe.java.group10.lab1.lzy.varFood;

public class Apple extends Food
{
    @Override // 显式方法覆盖
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Apple))
        {
            return false;
        }
        else if (size == null || ((Apple) obj).size == null)
        {
            return false;
        }
        else if (!(size.equals(((Apple) obj).size)))
        {
            return false;
        }
        return super.equals(obj);//super可以理解为是指向自己超（父）类对象的一个指针，而这个surper类指的是离自己最近的一个父类。"调用父类"
    }

    private String size;

    public String getSize()
    {
        return size;
    }

    public Apple(String size)
    {
        super ("Яблоко");
        this.size = size;
    }

    public void consume()
    {
        System.out.println(this + " съедено");//吃过
    }

    @Override
    public String toString()
    {
        return super.toString() + " размера '" + size.toUpperCase() + "'";
    }
}
