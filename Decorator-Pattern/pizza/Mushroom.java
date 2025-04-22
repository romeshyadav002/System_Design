public class Mushroom extends ToppingDecorator{
    BasePizza bp;

    public Mushroom(BasePizza bp){
        this.bp = bp;
    }

    @Override
    public int cost() {
        return this.bp.cost() + 15;
    }
}
