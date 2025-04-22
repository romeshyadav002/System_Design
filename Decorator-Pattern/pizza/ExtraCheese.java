public class ExtraCheese extends ToppingDecorator{
    BasePizza bp;

    public ExtraCheese(BasePizza bp){
        this.bp = bp;
    }

    @Override
    public int cost() {
        return this.bp.cost() + 10;
    }
}
