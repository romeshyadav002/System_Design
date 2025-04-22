package logger;

public class InfoLogProcessor extends LogProcessor{
    InfoLogProcessor(LogProcessor nextLogProcessor ){
        super(nextLogProcessor);
    }

    public void log(int loglevel, String message){
        if(loglevel == INFO){
            System.out.println("INFO: " + message);
        }else{
            super.log(loglevel,message);
        }
    }
}