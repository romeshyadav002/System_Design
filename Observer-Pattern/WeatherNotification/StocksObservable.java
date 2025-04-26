public interface StocksObservable {
    public void add(NotifAlertObserver obj);
    public void remove(NotifAlertObserver obj);
    public void notifySubscriber();
    public void setStock(int stocks);
    public int getStockCount();
}
