package edu.dp.command;

public class Client {
    public static void main(String[] args) {
        Invoker invoker = new Invoker();
        Light light = new Light();
        // 创建指令
        Command lightOnCommand = new LightOnCommand(light);
        // 将指令放在指令队列中
        invoker.setOnCommand(lightOnCommand, 0);
        // Invoker 调用指令
        invoker.onButtonWasPushed(0);

    }
}
