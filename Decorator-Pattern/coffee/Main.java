public class Main {
    public static void main(String[] args) {
        BaseCoffee bc1 = new KesarDecorator(new KeralaCoffee());
        System.out.println(bc1.cost());
    }
}
