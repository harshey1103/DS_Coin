package DSCoinPackage;

import HelperClasses.Pair;
import HelperClasses.MerkleTree;
import java.util.*;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    this.trarray = new Transaction[t.length];
    for(int i = 0;  i<t.length; ++i){
      this.trarray[i] = t[i];
    }
    this.Tree = new MerkleTree();
    this.trsummary = this.Tree.Build(this.trarray);

    this.previous = null;
    this.dgst = null;
  }

  public boolean checkTransaction (Transaction t) 
  {
      if (t.coinsrc_block == null) {
        return true;
      }
      boolean flag = false;
      for (int i = 0; i < t.coinsrc_block.trarray.length; i++) {
        if (t.coinsrc_block.trarray[i].coinID.equals(t.coinID) && t.coinsrc_block.trarray[i].Destination.UID.equals(t.Source.UID)) {
          flag = true;
          break;
        }
      }
      if (flag == false) {
        return false;
      }
  
      TransactionBlock currb = this;
      while (currb != t.coinsrc_block) 
      {
        for (int i = 0; i < currb.trarray.length; i++) {
          if (currb.trarray[i].coinID.equals(t.coinID)) {
            return false;
          }
        }
        currb = currb.previous;
      }
      return true;
    
  }
}


