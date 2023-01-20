package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
{

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) 
  {
    Members mod = new Members();
    mod.UID = "Moderator";
    int num_trans = DSObj.bChain.tr_count;
    int num_members = DSObj.memberlist.length;
    int num_blocks = coinCount/num_trans;

    for(int i = 0; i < num_blocks; i++)
    {
      Transaction[] trans_array = new Transaction[num_trans];
      for(int j = 0; j < num_trans; j++)
      {
        Transaction tr = new Transaction();
        tr.coinID = Integer.toString(i*num_trans + j + 100000);
        tr.Source = mod;
        tr.Destination = DSObj.memberlist[(i*num_trans+j)%num_members];
        tr.coinsrc_block = null;
        trans_array[j] = tr;
      }
      TransactionBlock tB = new TransactionBlock(trans_array);
      DSObj.bChain.InsertBlock_Honest(tB);
      for(int j = 0; j < num_trans; j++)
      {
        Pair<String,TransactionBlock> coin = new Pair<String,TransactionBlock>(Integer.toString(i*num_trans+j + 100000),tB);
        DSObj.memberlist[(i*num_trans+j)%num_members].mycoins.add(coin);
      }
    }
    DSObj.latestCoinID = Integer.toString(100000 + coinCount -1);
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) 
  {
    Members mod = new Members();
    mod.UID = "Moderator";
    int num_trans = DSObj.bChain.tr_count;
    int num_members = DSObj.memberlist.length;
    int num_blocks = coinCount/num_trans;

    for(int i = 0; i < num_blocks; i++)
    {
      Transaction[] trans_array = new Transaction[num_trans];
      for(int j = 0; j < num_trans; j++)
      {
        Transaction tr = new Transaction();
        tr.coinID = Integer.toString(i*num_trans+j + 100000);
        tr.Source = mod;
        tr.Destination = DSObj.memberlist[(i*num_trans+j)%num_members];
        tr.coinsrc_block = null;
        trans_array[j] = tr;
      }
      TransactionBlock tB = new TransactionBlock(trans_array);
      DSObj.bChain.InsertBlock_Malicious(tB);
      for(int j = 0; j < num_trans; j++)
      {
        Pair<String,TransactionBlock> coin = new Pair<String,TransactionBlock>(Integer.toString(i*num_trans+j + 100000),tB);
        DSObj.memberlist[(i*num_trans+j)%num_members].mycoins.add(coin);
      }
    }
    DSObj.latestCoinID = Integer.toString(100000 + coinCount -1);
  }
}

