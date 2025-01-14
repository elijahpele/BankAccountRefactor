import java.text.DecimalFormat;

public class TransactionManager {

    private Customer currUser;
    private Customer userForTransaction;
    private String transactionType;
    private IAccount currUserAccountType;
    private IAccount userToPayAccountType;
    private double amount;

    /**
     *  @param currUser
     * @param transactionType
     * @param accountType
     *
     */
    public TransactionManager(Customer currUser, String transactionType, IAccount accountType) {
        this.currUser = currUser;
        this.transactionType = transactionType;
        this.currUserAccountType = accountType;
    }

    /**
     *  @param currUser
     * @param transactionType
     * @param accountType
     * @param amount
     *
     */
    public TransactionManager(Customer currUser, String transactionType, IAccount accountType, double amount) {
        this.currUser = currUser;
        this.transactionType = transactionType;
        this.currUserAccountType = accountType;
        this.amount = amount;
    }

    /**
     *  @param currUser
     * @param userForTransaction
     * @param transactionType
     * @param accountType
     * @param userToPayAccountType
     * @param amount
     *
     */

    public TransactionManager(Customer currUser, Customer userForTransaction, String transactionType,
                              IAccount accountType, IAccount userToPayAccountType , double amount) {
        this.currUser = currUser;
        this.userForTransaction = userForTransaction;
        this.transactionType = transactionType;
        this.currUserAccountType = accountType;
        this.userToPayAccountType = userToPayAccountType;
        this.amount = amount;
    }

    /**
     *
     * @return an explanation of the transaction that was executed
     */
    public String executeAndLogTransaction() {
        boolean transSuccessful = true;
        String logMessage = "";

        if(this.transactionType.equalsIgnoreCase("BALANCE") ||
                this.transactionType.equalsIgnoreCase("INQUIRES")) {
            logMessage += printTransactionResultBalance();
        }
        else if(this.transactionType.equalsIgnoreCase("WITHDRAW") ||
                this.transactionType.equalsIgnoreCase("WITHDRAWS")) {
            transSuccessful = currUserAccountType.withdraw(amount);
            logMessage += printTransactionResultWithdraw(transSuccessful);
        }
        else if(this.transactionType.equalsIgnoreCase("DEPOSIT") ||
                this.transactionType.equalsIgnoreCase("DEPOSITS")) {
            transSuccessful = currUserAccountType.deposit(amount);
            logMessage += printTransactionResultDeposit(transSuccessful);
        }
        else if(this.transactionType.equalsIgnoreCase("TRANSFER") ||
                this.transactionType.equalsIgnoreCase("TRANSFERS")) {
            if(currUserAccountType == userToPayAccountType)
                transSuccessful = false;
            else
                transSuccessful = pay();
            logMessage += printTransactionResultTransfer(transSuccessful);
        }
        else if(this.transactionType.equalsIgnoreCase("PAY") ||
                this.transactionType.equalsIgnoreCase("PAYS")) {
            transSuccessful = pay();
            logMessage += printTransactionResultPAY(transSuccessful);
        }
        currUser.printAllInfo();
        if(userForTransaction != null)
            userForTransaction.printAllInfo();
        return logMessage;
    }

    /**
     *
     * @return whether or not the pay transaction was successful
     *
     * Withdraws from one account, deposits into second account
     * Pay method handled here since, two different users are needed to complete the transaction
     * Also used as transfer method
     */
    public boolean pay() {
        boolean transSuccessful = true;
        //If account the paying account is credit, then the balance isn't relevant
        //If account isn't credit, then the amount being paid cannot exceed the current balance
        if(!currUserAccountType.getClass().getName().equals("Credit") && currUserAccountType.getBalance() < this.amount) {
            return false;
        }
        //Cannot pay more than the current balance to a credit account
        if(userToPayAccountType.getClass().getName().equals("Credit") && userToPayAccountType.getBalance()+this.amount > 0) {
            return false;
        }
        //Payment is technically a withdraw from one user account
        //and a deposit into the second user account
        boolean currUserWithdraw = currUserAccountType.withdraw(this.amount);
        boolean userForTransactionDeposit = userToPayAccountType.deposit(this.amount);
        return transSuccessful;
    }

    /**
     *
     * @param transactionSuccess
     * @return log for the results from a pay transaction
     */
    public String printTransactionResultPAY(boolean transactionSuccess) {
        String currUserTransStatus = currUser.getFullName();
        String userToPayTransStatus = "\n" + userForTransaction.getFullName();

        if(transactionSuccess) {
            currUserTransStatus += " paid ";
            userToPayTransStatus += " received ";
        }
        else {
            currUserTransStatus += " failed to pay ";
            userToPayTransStatus += " failed to receive ";
        }
        currUserTransStatus += userForTransaction.getFullName() +" " +toCurrency(amount) + " from " +
                currUserAccountType.getAccDetails() + ".  " + currUser.getFullName() + "'s New Balance for " +
                currUserAccountType.getAccDetails() + ": " + toCurrency(currUserAccountType.getBalance());;
        userToPayTransStatus += toCurrency(amount) + " from " + currUser.getFullName() + ".  " +
                userForTransaction.getFullName() + "'s " + "New Balance for " +
                userToPayAccountType.getAccDetails() + ": " + toCurrency(userToPayAccountType.getBalance());
        currUser.addTransactions(currUserTransStatus);
        userForTransaction.addTransactions(userToPayTransStatus);

        return currUserTransStatus + userToPayTransStatus;
    }
    /**
     *
     * @param transactionSuccess
     * @return log for the results from a withdraw transaction
     */
    public String printTransactionResultWithdraw(boolean transactionSuccess) {
        String currUserTransStatus = currUser.getFullName();
        if(transactionSuccess) {
            currUserTransStatus += " executed a " ;
        }
        else {
            currUserTransStatus += " failed to execute a ";
        }
        currUserTransStatus += "WITHDRAW"+ " from " + currUserAccountType.getAccDetails() +".  " +
                currUser.getFullName() + "'s Balance for " + currUserAccountType.getAccDetails()+": " +
                toCurrency(currUserAccountType.getBalance());
        currUser.addTransactions(currUserTransStatus);
        return currUserTransStatus;
    }

    /**
     *
     * @param transactionSuccess
     * @return log for the results from a deposit transaction
     */
    public String printTransactionResultDeposit(boolean transactionSuccess) {
        String currUserTransStatus = currUser.getFullName();
        if(transactionSuccess) {
            currUserTransStatus += " executed a " ;
        }
        else {
            currUserTransStatus += " failed to execute a ";
        }
        currUserTransStatus += "DEPOSIT"+ " to " + currUserAccountType.getAccDetails() +".  " +
                currUser.getFullName() + "'s Balance for " + currUserAccountType.getAccDetails()+": " +
                toCurrency(currUserAccountType.getBalance());
        currUser.addTransactions(currUserTransStatus);
        return currUserTransStatus;
    }

    /**
     *
     * @return log for the results from a balance inquiry transaction
     */
    public String printTransactionResultBalance() {
        String currUserTransStatus = currUser.getFullName() + " made a balance inquiry on " +
                currUserAccountType.getAccDetails() + ".  "  + currUser.getFullName() + "'s Balance for " +
                currUserAccountType.getAccDetails() + ": " + toCurrency(currUserAccountType.getBalance());
        currUser.addTransactions(currUserTransStatus);
        return currUserTransStatus;
    }

    /**
     *
     * @param transactionSuccess
     * @return log for the results from a transfer transaction
     */
    public String printTransactionResultTransfer(boolean transactionSuccess) {
        String currUserTransStatus = currUser.getFullName();

        if(transactionSuccess) {
            currUserTransStatus += " transferred ";
        }
        else {
            currUserTransStatus += " failed to transfer ";
        }
        currUserTransStatus += toCurrency(amount) + " from " + currUserAccountType.getAccDetails() +" to " +
                userToPayAccountType.getAccDetails() + ".  " +currUser.getFullName() +"'s New Balance for " +
                currUserAccountType.getAccDetails() + ": " + toCurrency(currUserAccountType.getBalance()) +
                ".  "+ userForTransaction.getFullName() +"'s New Balance for " +
                userToPayAccountType.getAccDetails() + ": " + toCurrency(userToPayAccountType.getBalance());
        currUser.addTransactions(currUserTransStatus);
        return currUserTransStatus;
    }

    /**
     *
     * @param amount
     * @return number in currency format
     */
    public String toCurrency(double amount) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        String amountString = "$"+numberFormat.format(amount);
        return amountString;
    }
}
