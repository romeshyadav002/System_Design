public class EmailAlertObserverImp implements NotifAlertObserver {

    String emailId;
    StocksObservable observable;
    public EmailAlertObserverImp(String emailId, StocksObservable observable){
        this.emailId = emailId;
        this.observable = observable;
    }
    public void update(){
        sendEmail(this.emailId, "product is in store");
    }

    private void sendEmail(String emailId, String message){
        System.out.println("mail sent to " + emailId + " " + message);
    }
}
