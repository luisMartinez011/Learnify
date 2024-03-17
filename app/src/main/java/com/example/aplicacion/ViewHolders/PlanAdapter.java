package com.example.aplicacion.ViewHolders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;
import com.example.aplicacion.cal;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<cal> plans = new ArrayList<>();

    public void addPlan(cal plan) {
        plans.add(plan);
        notifyItemInserted(plans.size() - 1);
    }

    public void clearPlans() {
        plans.clear();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cal, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        cal c = plans.get(position);
        holder.bind(c);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView planTextView;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            planTextView = itemView.findViewById(R.id.tc);
        }

        public void bind(cal plan) {
            planTextView.setText(plan.getData());
        }
    }
}

