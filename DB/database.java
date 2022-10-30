package DB;
import java.util.ArrayList;
import java.time.LocalDate;

import mypack.Customer;
import mypack.User;

public class database
{
    final private ArrayList<User> accounts = new ArrayList<User>();
    final private ArrayList<Transaction> transactions = new ArrayList<Transaction>();


    public boolean userExists(String login)
    {
        for(User user : accounts)
        {
            if(user.login.equals(login))
            {
                return true;
            }
        }
         return false;
    }

    public User getUser(String login)
    {
        User currentUser = accounts.get(0);
        for(User user : accounts)
        {
            if(user.login.equals(login))
            {
                currentUser = user;
            }
        }
        return currentUser;
    }

    public User getUser(int accNo)
    {
        User currentUser = accounts.get(0);
        for(User user : accounts)
        {
            if(user.accNo == accNo)
            {
                currentUser = user;
            }
        }
        return currentUser;
    }

    public void addUser(User newUser)
    {
        accounts.add(newUser);
    }

    public void remove(int accNo)
    {
        int index = getUserIndex(accNo);
        accounts.remove(index);
    }

    private int getUserIndex(int accNo)
    {
        int index = 0;
        for(User user : accounts)
        {
            if(user.accNo == accNo)
            {
                index = accounts.indexOf(user);
            }
        }
        return index;
    }

    public ArrayList<User> searchAccounts(String accNo, String login, String title, String balance)
    {
        ArrayList<User> results = new ArrayList<User>(accounts);
        if(!accNo.equals("")){
            for(User user : results)
            {
                if(user.accNo != (Integer.parseInt(accNo)))
                {
                    results.remove(user);
                }
            }
        }

        if(!login.equals("")){
            for(User user : results)
            {
                if(!user.login.equals(login))
                {
                    results.remove(user);
                }
            }
        }

        if(!title.equals("")){
            for(User user : results)
            {
                if(!user.title.equals(title))
                {
                    results.remove(user);
                }
            }
        }

        if(!balance.equals("")){
            for(User user : results)
            {
                Customer customer = (Customer)user;
                if((customer.getBalance()) != Integer.parseInt(balance))
                {
                    results.remove(user);
                }
            }
        }

        return results;
    }

    public void addTransaction(Transaction transaction)
    {
        transactions.add(transaction);
    }

    public ArrayList<Transaction> searchTransactions(LocalDate from, LocalDate upto)
    {
        ArrayList<Transaction> results = new ArrayList<Transaction>();
        for(Transaction transaction : transactions)
        {
            if(transaction.date.isAfter(from) && transaction.date.isBefore(upto))
            {
                results.add(transaction);
            }
        }
        return results;
    }

    public ArrayList<Customer> searchTransactions(double min, double max)
    {
        ArrayList<Customer> results = new ArrayList<Customer>();
        for(User user : accounts)
        {
            Customer customer;
            if(user.isAdmin){
                continue;
            } else {
                customer = (Customer)user;
            }
            if(customer.getBalance() >= min && customer.getBalance() <= max)
            {
                results.add(customer);
            }
        }
        return results;
    }

    public int getNewAccNo()
    {
        if(accounts.isEmpty()) return 1;
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for(User user : accounts)
        {
            numbers.add(user.accNo);
        }
        Integer[] accNos = new Integer[accounts.size()];
        accNos = numbers.toArray(accNos);
        return accNos[accNos.length] + 1;
    }

    public ArrayList<User> getAccounts()
    {
        return this.accounts;
    }
}