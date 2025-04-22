public class Main {
    public static void main(String[] args) {
        BasePizza bp = new Mushroom(new ExtraCheese(new Margherita()));
        System.out.println(bp.cost());
    }
}
