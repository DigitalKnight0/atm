package DB;
import java.time.LocalDate;

public class Transaction {
    public String type;
    public int accNo;
    public String title;
    public LocalDate date;
    public double amount;

    public Transaction(int accNo, String type, String title, LocalDate date, double amount)
    {
        this.accNo = accNo;
        this.type = type;
        this.title = title;
        this.date = date;
        this.amount = amount;
    }
}
