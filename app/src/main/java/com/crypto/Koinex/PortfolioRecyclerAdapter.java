package com.crypto.Koinex;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crypto.Koinex.R;

import java.util.ArrayList;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.ViewHolder> {

    private ArrayList<String> mCoinNames = new ArrayList<>();
    private ArrayList<Float> mCoinQuants = new ArrayList<>();
    private ArrayList<Float> mCoinBPs = new ArrayList<>();
    private ArrayList<String> mCoinCurrPrice = new ArrayList<>();
    private ArrayList<String> mCoinImages = new ArrayList<>();
    private ArrayList<String> mCoinIDs = new ArrayList<>();
    private Context mContext;
    private OnPortfolioCoinClickListener monPortfolioCoinClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_portfolio_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view, monPortfolioCoinClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.coinName.setText(mCoinNames.get(position).substring(1, mCoinNames.get(position).length() - 1));

        Float newValue = mCoinQuants.get(position) * Float.parseFloat(mCoinCurrPrice.get(position));
        Float origValue = mCoinBPs.get(position) * mCoinQuants.get(position);

        viewHolder.coinTotalValue.setText(newValue.toString());

        if(newValue - origValue < 0) {
            viewHolder.coinTotalProfit.setText("-$" + String.valueOf(newValue - origValue).substring(1, String.valueOf(newValue - origValue).length() - 1));
        } else {
            viewHolder.coinTotalProfit.setText("$" + String.valueOf(newValue - origValue));
        }

        viewHolder.coinBP.setText("$" + String.valueOf(mCoinBPs.get(position)));
        viewHolder.coinQuantity.setText(String.valueOf(mCoinQuants.get(position)));


        Glide.with(mContext)
                .asBitmap()
                .load(mCoinImages.get(position))
                .into(viewHolder.coinImage);

    }

    @Override
    public int getItemCount() {
        return mCoinQuants.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView coinName;
        TextView coinTotalProfit;
        TextView coinTotalValue;
        ImageView coinImage;
        TextView coinRanking;
        TextView coinBP;
        TextView coinQuantity;
        OnPortfolioCoinClickListener onPortfolioCoinClickListener;

        public ViewHolder(@NonNull View itemView, OnPortfolioCoinClickListener onPortfolioCoinClickListener) {
            super(itemView);

            coinName = itemView.findViewById(R.id.portfolioCoinName);
            coinTotalProfit = itemView.findViewById(R.id.portfolioTotalProfitv);
            coinTotalValue = itemView.findViewById(R.id.portfolioTotalValuev);
            coinImage = itemView.findViewById(R.id.portfolioCoinImage);
            coinRanking = itemView.findViewById(R.id.ranking);
            coinBP = itemView.findViewById(R.id.portfolioCoinsBPv);
            coinQuantity = itemView.findViewById(R.id.portfolioCoinsOwnedv);
            this.onPortfolioCoinClickListener = onPortfolioCoinClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPortfolioCoinClickListener.OnPortfolioCoinClick(getLayoutPosition(), mCoinIDs);
            Log.d("ra", mCoinIDs.get(getAdapterPosition()));
            Log.d("ra", String.valueOf(getAdapterPosition()));
            Log.d("ra", mCoinIDs.toString());
        }
    }

    public PortfolioRecyclerAdapter(ArrayList<String> coinNames,
                                    ArrayList<Float> coinQuants,
                                    ArrayList<Float> coinBPs,
                                    ArrayList<String> coinCurrPrice,
                                    ArrayList<String> coinImages,
                                    ArrayList<String> coinIDs,
                                    Context mContext,
                                    OnPortfolioCoinClickListener monPortfolioCoinClickListener) {
        mCoinNames = coinNames;
        mCoinQuants = coinQuants;
        mCoinBPs = coinBPs;
        mCoinCurrPrice = coinCurrPrice;
        mCoinImages = coinImages;
        mCoinIDs = coinIDs;

        Log.d("help", "in adapte" + coinIDs.toString());
        Log.d("help", "in adapte" + coinNames.toString());
        this.mContext = mContext;
        this.monPortfolioCoinClickListener = monPortfolioCoinClickListener;

    }

    public interface OnPortfolioCoinClickListener {
        void OnPortfolioCoinClick(int position, ArrayList<String> ids );
    }

}


