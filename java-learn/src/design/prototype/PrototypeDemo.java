package design.prototype;

public class PrototypeDemo {
    public static void main(String[] args) {
        MyProtoType myProtoType = new MyProtoType();
        MyProtoType myProtoType1 = myProtoType.clone();
        System.out.println(myProtoType == myProtoType1);
    }
}