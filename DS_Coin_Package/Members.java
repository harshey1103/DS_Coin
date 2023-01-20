package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans = new Transaction[200];

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Collections.sort(mycoins, Comparator.comparing(p -> Integer.parseInt(p.get_first())));
    Transaction tobj = new Transaction();
    for(int i = 0; i < DSobj.memberlist.length; i++)
    {
      if(DSobj.memberlist[i].UID == destUID)
      {
        // setting destination
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }
    // setting coinID, source block, source
    Pair<String, TransactionBlock> temp = this.mycoins.get(0);
    tobj.coinID = temp.first;
    tobj.coinsrc_block = temp.second;
    tobj.Source = this;
    this.mycoins.remove(0);
    int idx = 0;
    while(in_process_trans[idx] != null){idx++;}
    in_process_trans[idx] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Collections.sort(mycoins, Comparator.comparing(p -> Integer.parseInt(p.get_first())));
    Transaction tobj = new Transaction();
    for(int i = 0; i < DSobj.memberlist.length; i++)
    {
      if(DSobj.memberlist[i].UID == destUID)
      {
        // setting destination
        tobj.Destination = DSobj.memberlist[i];
        break;
      }
    }
    // setting coinID, source block, source
    Pair<String, TransactionBlock> temp = this.mycoins.get(0);
    tobj.coinID = temp.first;
    tobj.coinsrc_block = temp.second;
    tobj.Source = this;
    this.mycoins.remove(0);
    int idx = 0;
    while(in_process_trans[idx] != null){idx++;}
    this.in_process_trans[idx] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    TransactionBlock lb = DSObj.bChain.lastBlock;
    Boolean found = false;
    int idx=-1;
    while(lb!=null)
    {
      for(int i = 0; i < lb.trarray.length; i++)
      {
        if(lb.trarray[i] == tobj)
        {
          idx = i;
          found = true;
          break;
        }
      }
      if(found)break;
      lb = lb.previous;
    }
    if(lb == null)throw new MissingTransactionException();
    TransactionBlock trblock = lb;
    List<Pair<String, String>> path = lb.Tree.pathtoroot(idx); 
    lb = lb.previous;
    if(lb == null)throw new MissingTransactionException();
    TransactionBlock currb = DSObj.bChain.lastBlock;
    ArrayList<Pair<String,String>> blockpath = new ArrayList<Pair<String,String>>(); 
    while(currb != lb)
    {
      Pair<String, String> temp = new Pair<String, String>(currb.dgst, currb.previous.dgst + '#' + currb.trsummary + '#' + currb.nonce);
      blockpath.add(temp);
      currb = currb.previous;
    }
    if(lb.previous!=null){
      Pair<String, String> temp = new Pair<String, String>(lb.dgst, null);
      blockpath.add(temp);
    }else{
      Pair<String, String> temp = new Pair<String, String>(DSObj.bChain.start_string,null);
      blockpath.add(temp);
    }
    ArrayList<Pair<String,String>> reversepath = new ArrayList<Pair<String,String>>();
    int c = blockpath.size()-1;
    while(c >= 0){
      reversepath.add(blockpath.get(c));
      c -= 1;
    }
    
    int i = 0;
	  while(i < tobj.Destination.mycoins.size() && Integer.valueOf(tobj.Destination.mycoins.get(i).first)< Integer.valueOf(tobj.coinID)){
		  i++;
	  }
    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(tobj.coinID,trblock);
	  tobj.Destination.mycoins.add(i,coin);
	  int j = 0;
	  if(!tobj.Source.UID.equals("Moderator")){
		  while(tobj.Source.in_process_trans[j] != tobj){
        j++;
      }
		  tobj.Source.in_process_trans[j] = null;
	  }
    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(path,reversepath);

  }

  public void MineCoin(DSCoin_Honest DSObj) {
    try{
    int c = 0;
      int num = DSObj.bChain.tr_count; 
      Transaction[] trans = new Transaction[num];
      while (c < num - 1) {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        if (DSObj.bChain.lastBlock.checkTransaction(t)) {
          int k = 0;
          for (int i = 0; i < c; ++i) {
            if (trans[i].coinID.equals(t.coinID)) {
              k++;
              break;
            }
          }
          if (k == 0) {
            trans[c] = t;
            c++;
          }
        }
      }

      DSObj.latestCoinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);

      Transaction minerRewardTransaction = new Transaction();
      minerRewardTransaction.coinID = DSObj.latestCoinID;
      minerRewardTransaction.Destination = this;
      trans[num - 1] = minerRewardTransaction;
      TransactionBlock mine_tB = new TransactionBlock(trans);
      mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, mine_tB));
      DSObj.bChain.InsertBlock_Honest(mine_tB);
    }catch(Exception e){System.out.println(e);}
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    try{
    int c = 0;
      int num = DSObj.bChain.tr_count;
      Transaction[] trans = new Transaction[num];
      TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();
      while (c < num - 1) {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        if (lastBlock.checkTransaction(t)) {
          int k = 0;
          for (int i = 0; i < c; ++i) {
            if (trans[i].coinID.equals(t.coinID)) {
              k++;
              break;
            }
          }
          if (k == 0) {
            trans[c] = t;
            c++;
          }
        }
      }
      DSObj.latestCoinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);

      Transaction minerRewardTransaction = new Transaction();
      minerRewardTransaction.coinID = DSObj.latestCoinID;
      minerRewardTransaction.Destination = this;
      trans[num - 1] = minerRewardTransaction;
      TransactionBlock mine_tB = new TransactionBlock(trans);
      mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, mine_tB));
      DSObj.bChain.InsertBlock_Malicious(mine_tB);
    }catch(Exception e){System.out.println(e);}
  }  
}
