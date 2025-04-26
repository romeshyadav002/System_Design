import java.util.ArrayList;
import java.util.List;

public class IphoneObservableImp implements StocksObservable{
    public List<NotifAlertObserver> observerList = new ArrayList<>();
    public int stockCount =0;
    public void add(NotifAlertObserver observer){
        observerList.add(observer);
    }
    public void remove(NotifAlertObserver observer){
        observerList.remove(observer);
    }

    @Override
    public void notifySubscriber() {
        for(NotifAlertObserver observer : observerList){
            observer.update();
        }
    }

    @Override
    public void setStock(int newStocks) {
        if(stockCount ==0){
            notifySubscriber();
        }
        stockCount = stockCount + newStocks;
    }

    @Override
    public int getStockCount() {
        return stockCount;
    }
}
