package com.xuhao.didi.oksocket.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhao.didi.oksocket.R;
import com.xuhao.didi.oksocket.data.LogBean;

import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ItemHolder> {

    private List<LogBean> mDataList = new ArrayList<>();

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        LogBean bean = mDataList.get(position);

        holder.mTime.setText(bean.mTime);
        holder.mLog.setText(bean.mLog);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                LogBean log = mDataList.get(holder.getAdapterPosition());
                String msg = log.mTime + " " + log.mLog;
                cmb.setPrimaryClip(ClipData.newPlainText(null, msg));
                Toast.makeText(v.getContext(), "已复制到剪贴板", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public TextView mTime;
        public TextView mLog;

        public ItemHolder(View itemView) {
            super(itemView);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mLog = (TextView) itemView.findViewById(R.id.logtext);
        }
    }

    public List<LogBean> getDataList() {
        return mDataList;
    }
}