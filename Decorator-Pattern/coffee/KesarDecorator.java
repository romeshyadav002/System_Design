public class KesarDecorator extends ToppingDecorator{
    BaseCoffee bc;

    public KesarDecorator(BaseCoffee bc){
        this.bc = bc;
    }

    @Override
    public int cost() {
        return this.bc.cost() + 30;
    }
}
