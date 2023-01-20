package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    String start = null;
    if(this.lastBlock == null)
    {
      start = start_string;
    }else
    {
      start = this.lastBlock.dgst;
    }
    long n = 1000000000;
    while(true)
    {
      if( obj.Fn(start + "#" + newBlock.trsummary + "#" + n).startsWith("0000") )
      {
        newBlock.nonce = String.valueOf(n);
        break;
      }
      n++;
    }
    newBlock.dgst = obj.Fn(start + "#" + newBlock.trsummary + "#" + newBlock.nonce);
    newBlock.previous = this.lastBlock;
    this.lastBlock = newBlock;
  }
}
