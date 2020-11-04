package edu.dp.command;

public class Invoker {
    private Command[] onCommands;
    private final int slotNum = 7;
    public Invoker() {
        this.onCommands = new Command[slotNum];
    }

    public void setOnCommand(Command command, int slot) {
        onCommands[slot] = command;
    }


    public void onButtonWasPushed(int slot) {
        onCommands[slot].execute();
    }
}