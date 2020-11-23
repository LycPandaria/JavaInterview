package edu.dp.mediator;

public class Alarm extends Colleague {
    @Override
    public void onEvent(Mediator mediator) {
        mediator.doEvent("alarm");
    }

    public void doAlarm(){
        System.out.println("doAlarm()");
    }
}
