package design.prototype;

public class MyProtoType implements Cloneable {
    public MyProtoType() {
        System.out.println("调用了构造");
    }

    @Override
    protected MyProtoType clone() {
        try {
            return (MyProtoType) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
