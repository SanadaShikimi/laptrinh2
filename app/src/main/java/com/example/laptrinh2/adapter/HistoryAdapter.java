package com.example.laptrinh2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.laptrinh2.R;
import com.example.laptrinh2.model.QuizResult;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private List<QuizResult> historyList;
    private LayoutInflater inflater;

    public HistoryAdapter(Context context, List<QuizResult> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_history, parent, false);
            holder = new ViewHolder();
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.tvScore = convertView.findViewById(R.id.tv_score);
            holder.tvResult = convertView.findViewById(R.id.tv_result);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvCategory = convertView.findViewById(R.id.tv_category);
            holder.tvPerformance = convertView.findViewById(R.id.tv_performance);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QuizResult result = historyList.get(position);

        // Set data
        holder.tvDate.setText(result.getDate());
        holder.tvScore.setText(result.getScoreText());
        holder.tvResult.setText(result.getResultText());
        holder.tvTime.setText(result.getFormattedTime());
        holder.tvCategory.setText(result.getCategory() != null ? result.getCategory() : "Tổng hợp");
        holder.tvPerformance.setText(result.getPerformanceLevel());

        // Set score color based on performance
        int scoreColor = getScoreColor(result.getScore());
        holder.tvScore.setTextColor(scoreColor);
        holder.tvPerformance.setTextColor(scoreColor);

        return convertView;
    }

    private int getScoreColor(int score) {
        if (score >= 90) {
            return context.getResources().getColor(android.R.color.holo_green_dark);
        } else if (score >= 80) {
            return context.getResources().getColor(android.R.color.holo_blue_dark);
        } else if (score >= 70) {
            return context.getResources().getColor(android.R.color.holo_orange_dark);
        } else if (score >= 60) {
            return context.getResources().getColor(android.R.color.holo_orange_light);
        } else {
            return context.getResources().getColor(android.R.color.holo_red_dark);
        }
    }

    static class ViewHolder {
        TextView tvDate;
        TextView tvScore;
        TextView tvResult;
        TextView tvTime;
        TextView tvCategory;
        TextView tvPerformance;
    }
}