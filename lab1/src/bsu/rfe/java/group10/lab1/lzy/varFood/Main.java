package bsu.rfe.java.group10.lab1.lzy.varFood;

public class Main
{
    //主类方法
    public static void main(String[] args) throws Exception//throws是方法可能抛出异常的声明 st节省内存
{
    //对早餐食品进行定义
    Food[] breakfast = new Food[args.length];//l拆分字符串
    //解析命令行参数并为早餐类实例化它们
    for (int i = 0; i < args.length; i++)//a命令行参数
    {
        String[] parts = args[i].split("/");//s拆分字符串 将命令行参数拆分成字符串数组
        if (parts[0].equals("Cheese")) //奶酪没有额外参数 比较两个字符串是否一样
        {
            breakfast[i] = new Cheese();
        }
        else if (parts[0].equals("Apple"))  //苹果有一个参数，并位于1中
        {
            breakfast[i] = new Apple(parts[1]);
        }
        else if (parts[0].equals("Pie"))  //派也有一个
        {
            breakfast[i] = new Pie(parts[1]);
        }
    }
    //遍历数组所有元素
    for (Food item : breakfast)
    {
        item.consume();
    }

    Food food = new Pie("яблочная");
    System.out.println(food + ": " + countFoods(breakfast, food));
    printFoods(breakfast);
    System.out.println("Всего хорошего!");
}

    static Integer countFoods(Food[] breakfast, Food food)//整数类
    {
        Integer count = 0;
        for(int i = 0; i < breakfast.length; i++)
        {
            if(food.equals(breakfast[i]))
            {
                count++;
            }
        }
        return count;
    }

    static void printFoods(Food[] breakfast)
    {
        int c = 0;
        int a = 0;
        int p = 0;
        for(int i = 0; i < breakfast.length; i++)
        {

            if(breakfast[i] instanceof Cheese)
            {
                c++;
            }
            else if (breakfast[i] instanceof Apple)
            {
                a++;
            }
            else if (breakfast[i] instanceof Pie)
            {
                p++;
            }
        }
        System.out.println("CЫР - " + c);
        System.out.println("ЯБЛОКО - " + a);
        System.out.println("ПИРОГ - " + p);
    }
}
