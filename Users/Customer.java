package Users;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;

import DB.Transaction;
import Exceptions.*;

public class Customer extends User{
    private double balance;
    public String type;

    public Customer(String login, String pin, String type, String title, double balance)
    {
        this.accNo = db.getNewAccNo();
        this.login = login;
        this.pin = pin;
        this.title = title;
        this.type = type;
        this.balance = balance;
        this.isAdmin = false;
    }

    public Customer(int accNo, String login, String pin, String type, String title, double balance)
    {
        this.accNo = accNo;
        this.login = login;
        this.pin = pin;
        this.type = type;
        this.title = title;
        this.balance = balance;
        this.isAdmin = false;
    }

    public void displayMenu()
    {
        cls();
        if(this.isDisabled)
        {
            printDisabledMessage();
            return;
        }
        boolean reprintMain = true;
        while(reprintMain)
        {
            cls();
            System.out.println("1 -- Withdraw Cash");
            System.out.println("2 -- Cash Transfer");
            System.out.println("3 -- Deposit Cash");
            System.out.println("4 -- Display Balance");
            System.out.println("Any Other Number -- Exit");
            int input;
            try{
                input = sc.nextInt();
            } catch (InputMismatchException e)
            {
                System.out.println("You Entered an Invalid Number");
                continue;
            }

            switch(input)
            {
                case 1:
                    reprintMain = withdrawCash();
                    break;
                case 2:
                    reprintMain =transferCash();
                    break;
                case 3:
                    reprintMain =depositCash();
                    break;
                case 4:
                    reprintMain =displayInfo();
                    break;
                default:
                    return;
            }
        }
    }

    private boolean withdrawCash()
    {
        cls();
        System.out.println("1 -- Fast Cash");
        System.out.println("2 -- Normal Cash");
        try{
            int input = sc.nextInt();

            if(input == 1){
                fastCash();
            } else {
                normalCash();
            }
        } catch (InputMismatchException e)
        {
            System.out.println("You Entered an Invalid Number");
        }
        return returnToMain();
    }

    private void fastCash()
    {
        cls();
        try{
            System.out.println("Please Select an Option");
            for(int i = 1; i <= 7; i++)
            {
                System.out.println(i + " " + (i * 500));
            }
            double input = sc.nextInt();
            if(input < 1 || input > 7){
                System.out.println("Only options one to 7 are allowed");
                return;
            }
            input *= 500;
            System.out.println("Are you sure you want to withdraw Rs. " + input + "? (Y/N)");
            sc.nextLine();
            String input2 = sc.nextLine();
            if(!(input2.equals("Y") || input2.equals("y"))) return;
        
            tryAndWithdraw(input);
            Transaction transaction = new Transaction(this.accNo, "Withdrawal", this.title, LocalDate.now(), input);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("2 -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (LowBalance e)
        {
            System.out.println(e);
        } catch (LimitExceed e)
        {
            System.out.println(e);
        } catch (InputMismatchException e)
        {
            System.out.println("You have entered an invalid amount");
        }
        
    }

    private void normalCash()
    {
        cls();
        try{
            System.out.println("Please Select Amount to withdraw");
            int input = sc.nextInt();
            System.out.println("Are you sure you want to withdraw Rs. " + input + "? (Y/N)");
            sc.nextLine();
            String input2 = sc.nextLine();
            if(!(input2.equals("Y") || input2.equals("y"))) return;

            tryAndWithdraw(input);
            Transaction transaction = new Transaction(this.accNo, "Withdrawal", this.title, LocalDate.now(), input);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("2 -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (LowBalance e)
        {
            System.out.println(e);
        } catch (LimitExceed e)
        {
            System.out.println(e);
        } catch (InputMismatchException e)
        {
            System.out.println("You have entered an invalid amount");
        }
    }

    private void tryAndWithdraw(double amount) throws LowBalance, LimitExceed
    {
        if(this.balance < amount) throw new LowBalance("Your Balance is too low");
        double withdrawalsSinceYesterday = db.withdrawalsSinceYesterday(this.accNo);
        if((withdrawalsSinceYesterday + amount) > 20000)
        {
            throw new LimitExceed("You cannot withdraw more than 20000 in 24 hours");
        }
        this.balance -= amount;
    }

    private boolean transferCash()
    {
        cls();
        try{
            double amount;
            int accNo;
            System.out.println("Please enter the amount to transfer in multiples of 500");
            amount = sc.nextDouble();
            if(amount % 500 != 0)
            {
                System.out.println("Entered amount can only be in multiples of 500");
                return returnToMain();
            }
            if(amount > this.balance)
            {
                System.out.println("Not Enough Balance in your account");
                throw new LowBalance("Not enough Balance");
            }
            System.out.println("Please Enter the Account Number to tranfser to");
            accNo = sc.nextInt();
            sc.nextLine();
            if(!db.customerExists(accNo)) throw new UserNotFound("Could not find a customer with given account no");

            Customer account = (Customer)db.getUser(accNo);
            System.out.println("Transfer Rs." + amount + "To " + account.title + "?");
            System.out.println("Please re-enter the account number to proceed");
            int input2 = sc.nextInt();
            sc.nextLine();
            if(!(accNo != input2)) return returnToMain();
            this.balance -= amount;
            account.depositCash(amount);
            Transaction transaction = new Transaction(this.accNo, "Transfer", this.title, LocalDate.now(), amount);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("Any Other Number -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (LowBalance e)
        {
            System.out.println(e);
        } catch (UserNotFound e)
        {
            System.out.println(e);
        } catch (InputMismatchException e)
        {
            System.out.println("You have entered an invalid amount");
        }
        return returnToMain();
    }

    private boolean depositCash()
    {
        cls();
        try{
            System.out.println("Please Enter Amount to Deposit");
            double amount = sc.nextDouble();
            depositCash(amount);
            Transaction transaction = new Transaction(this.accNo, "Deposit", this.title, LocalDate.now(), amount);
            db.addTransaction(transaction);
            System.out.println("Do you want receipt?");
            System.out.println("1 -- Yes");
            System.out.println("Any Other Number -- No");
            int input1 = sc.nextInt();
            if(input1 == 1) printReceipt(transaction);
        } catch (InputMismatchException e)
        {
            System.out.println("You Have Entered an Invalid Number");
        }
        return returnToMain();
    }

    public void depositCash(double amount)
    {
        this.balance += amount;
    }

    private boolean displayInfo()
    {
        cls();
        System.out.println("Title: " + this.title);
        System.out.println("Account No: " + this.login);
        System.out.println("Balance: " + this.balance);
        return returnToMain();
    }

    public double getBalance()
    {
        return this.balance;
    }

    public String getPin()
    {
        return this.pin;
    }

    private void printReceipt(Transaction t)
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        System.out.println("Date: " + t.date.format(format));
        System.out.println("Account Number: " + this.accNo);
        System.out.println("Type: " + t.type);
        System.out.println("Amount: " + t.amount);
        System.out.println("Balance: " + this.balance);
    }

}
