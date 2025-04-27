public class Main {
    public static void main(String[] args) {
        ShapeFactory factory = new ShapeFactory();
        factory.getShape("CIRCLE").draw();
        factory.getShape("RECTANGLE").draw();
    }
}
