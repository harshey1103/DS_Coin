package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {
  
  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList ;
  
  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF obj = new CRF(64); 
    if(tB.previous == null)
    {
      if(!(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)).startsWith("0000"))return false;
    }else
    {
      if(!(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)).startsWith("0000"))return false;
    }
    for(int i = 0; i < tB.trarray.length; i++)
    {
      if(!tB.checkTransaction(tB.trarray[i]))return false;
    }
    MerkleTree tree = new MerkleTree();
    String s = tree.Build(tB.trarray);
    if(!tB.trsummary.equals(s))return false;
    return true;
  }
  
  public TransactionBlock FindLongestValidChain () 
  {
    int max = 0;
    TransactionBlock vlb = null;
    int i = 0;
    while(lastBlocksList[i] != null)
    {
      TransactionBlock curr = lastBlocksList[i];
      TransactionBlock tlb=null;
      int n = 0;
      while(curr != null)
      {
        if(n==0)tlb = curr;
        if(checkTransactionBlock(curr))
        {
          n++;
        }else{
          n = 0;
        }
        curr = curr.previous;
      }
      if(n > max)
      {
        max = n;
        vlb = tlb;
      }
      i++;
    }
    return vlb;
  }
  
    public void InsertBlock_Malicious (TransactionBlock newBlock) {
      TransactionBlock lb = FindLongestValidChain();
      CRF obj = new CRF(64);
      if(lb == null)
      {
        long n = 1000000000;
        while(true)
        {
          if( (obj.Fn(start_string + "#" + newBlock.trsummary + "#" + String.valueOf(n))).startsWith("0000"))
          {
            newBlock.nonce = String.valueOf(n);
            break;
          }
          n++;
        }
        newBlock.dgst = obj.Fn(start_string + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        lastBlocksList[0] = newBlock;
      }else
      {
        long n = 1000000000;
        while(true)
        {
          if( (obj.Fn(lb.dgst + "#" + newBlock.trsummary + "#" + String.valueOf(n))).startsWith("0000"))
          {
            newBlock.nonce = String.valueOf(n);
            break;
          }
          n++;
        }
        newBlock.dgst = obj.Fn(lb.dgst + "#" + newBlock.trsummary + "#" + newBlock.nonce);
        newBlock.previous = lb;
        int i = 0;
        while(lastBlocksList[i] != null)
        {
          if(lastBlocksList[i] == lb){
            lastBlocksList[i] = newBlock;
            return;
          }
          i++;
        }
        lastBlocksList[i] = newBlock;
      }
    }
}

