package com.example.healthhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientViewHolder> {

    private Context context;
    private List<AddUserToLocal.UserInfo> patientList;

    public PatientListAdapter(Context context, List<AddUserToLocal.UserInfo> patientList) {
        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        AddUserToLocal.UserInfo patient = patientList.get(position);
        holder.usernameTextView.setText(patient.getUsername());
        holder.phoneTextView.setText(patient.getMobile());
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, phoneTextView;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user_name);
            phoneTextView = itemView.findViewById(R.id.last_consulted);
        }
    }
}
