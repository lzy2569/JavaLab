package bsu.rfe.java.group10.lab1.lzy.varFood;
//ab抽象类
public class Food implements Consumable //关键字implements是一个类，实现一个接口用的关键字，它是用来实现接口中定义的抽象方法
{
    @Override
    public void consume()
    {

    }
   String name = null;

    public Food(String name)
    {
        this.name = name;
    }

    public boolean equals(Object arg0)//布尔类型
    {
        if(!(arg0 instanceof Food)) return false;//判断其左边对象是否为其右边类的实例，返回的是boolean类型的数据
        if (name == null || ((Food)arg0).name == null) return false;
        return name.equals(((Food)arg0).name);
    }
    public String toString()
    {
        return name;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

}
    //已经从基类Food中去掉了consume()方法的实现，这是可以做到的，因为Food本身是抽象的
//如果一个类中没有包含足够的信息来描绘一个具体的对象，这样的类就是抽象类。
