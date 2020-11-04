package edu.dp.responsibility_chain;

public class ConcreteHandler2 extends Handler{
    public ConcreteHandler2(Handler successor){
        super(successor);
    }
    @Override
    protected void handleRequest(Request request){
        if(request.getType()==RequestType.TYPE2) {
            System.out.println(request.getName() + " is handle by ConcreteHandler2");
        }
        if(successor != null)
            successor.handleRequest(request);
    }
}