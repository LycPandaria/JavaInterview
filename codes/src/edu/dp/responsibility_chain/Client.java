package edu.dp.responsibility_chain;

public class Client {

    public static void main(String[] args) {

        //构建责任链
        Handler handler1 = new ConcreteHandler1(null);
        Handler handler2 = new ConcreteHandler2(handler1);

       Request request1 = new Request(Handler.RequestType.TYPE1, "request1");
        handler2.handleRequest(request1);

        Request request2 = new Request(Handler.RequestType.TYPE2, "request2");
        handler2.handleRequest(request2);
    }
}