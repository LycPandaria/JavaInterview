package edu.dp.responsibility_chain;

public class Request{
    private Handler.RequestType type;
    private String name;

    public Request(Handler.RequestType requestType, String name){
        this.type = requestType;
        this.name = name;
    }
    public Handler.RequestType getType() {
        return type;
    }

    public void setType(Handler.RequestType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}