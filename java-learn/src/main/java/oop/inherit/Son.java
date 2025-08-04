package oop.inherit;

public class Son extends Father{
    private String name;
    private int age;
    private String toys;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    public String getToys() {
        return toys;
    }

    public void setToys(String toys) {
        this.toys = toys;
    }

    // 没有重写toString会怎么样
//    @Override
//    public String toString() {
//        return "Son{" +
//                "name='" + name + '\'' +
//                ", age=" + age +
//                ", toys='" + toys + '\'' +
//                '}';
//    }
}