package mypack;

public class LowBalance extends Exception {
    public LowBalance(String message)
    {
        super(message);
    }
}