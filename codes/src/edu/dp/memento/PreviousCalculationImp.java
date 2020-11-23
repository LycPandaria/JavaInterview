package edu.dp.memento;

/**
 * Memento Object Implementation
 * <p>
 * Note that this object implements both interfaces to Originator and CareTaker
 */

public class PreviousCalculationImp implements PreviousCalculationToOriginator, PreviousCalculationToCareTaker {

    private int firstNumber;
    private int secondNumber;

    PreviousCalculationImp(int firstNumber, int secondNumber){
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
    }

    @Override
    public int getFirstNumber() {
        return this.firstNumber;
    }

    @Override
    public int getSecondNumber() {
        return this.secondNumber;
    }
}
