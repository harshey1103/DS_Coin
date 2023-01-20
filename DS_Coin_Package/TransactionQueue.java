package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions = 0;

  public void AddTransactions (Transaction transaction) {
    if(numTransactions==0)
    {
        this.firstTransaction = this.lastTransaction = transaction;
    }else 
    {
      this.lastTransaction.next = transaction;
      this.lastTransaction = transaction;
    }
    this.numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(this.numTransactions==0){throw new EmptyQueueException();}
    Transaction tr = this.firstTransaction;
    if(this.numTransactions==1)
    {
      this.firstTransaction = this.lastTransaction = null;
    }else
    {
      this.firstTransaction = tr.next;
    }

    this.numTransactions--;
    return tr;
  }

  public int size() {
    return this.numTransactions;
  }
}
