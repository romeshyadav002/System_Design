public class Main {
    public static void main(String[] args) {
        StocksObservable iphone = new IphoneObservableImp();
        NotifAlertObserver o1 = new EmailAlertObserverImp("abc@gmail.com", iphone);
        NotifAlertObserver o2 = new EmailAlertObserverImp("xyz@gmail.com", iphone);

        iphone.add(o1);
        iphone.add(o2);
        iphone.setStock(10);
    }
}
