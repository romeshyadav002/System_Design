public class IcecreamDecorator extends ToppingDecorator{
    BaseCoffee bc;
     public IcecreamDecorator(BaseCoffee bc){
         this.bc = bc;
     }

    @Override
    public int cost() {
        return this.bc.cost() + 50;
    }
}
