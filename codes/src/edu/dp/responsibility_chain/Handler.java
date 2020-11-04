package edu.dp.responsibility_chain;

public abstract class Handler{



    public enum RequestType {
        TYPE1, TYPE2
    }

    protected Handler successor;    // 下一个处理者


    public Handler(Handler successor){
        this.successor = successor;
    }
    protected abstract void handleRequest(Request request);


}