package com.example.hikermanagementapp.Observation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.Database.Observation;
import com.example.hikermanagementapp.R;

import java.io.Serializable;
import java.util.List;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private final List<Observation> observations;

    ObservationAdapter(Activity activity, Context context, List<Observation> observations) {
        this.activity = activity;
        this.context = context;
        this.observations = observations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_observation_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Observation observation = observations.get(position);
        String name = observation.getObservationName();
        String date = observation.getTime();
        String comment = observation.getComments();

        // set value to form
        holder.observation_name.setText(name);
        holder.observation_date.setText(date);

        holder.editObservation.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateObservationActivity.class);
            intent.putExtra("selectedObservation", observation);
            activity.startActivityForResult(intent, 1);
        });
        holder.observationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateObservationActivity.class);
            intent.putExtra("selectedObservation", observation);
            activity.startActivityForResult(intent, 1);
        });
        holder.deleteObservation.setOnClickListener(v -> deleteObservation(observation, observation.getId())); // delete observation
    }

    @Override
    public int getItemCount() {
        return observations.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView editObservation, deleteObservation;
        TextView observation_name, observation_date;
        LinearLayout observationLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            observation_name = itemView.findViewById(R.id.observationName);
            observation_date = itemView.findViewById(R.id.observationDate);
            editObservation = itemView.findViewById(R.id.editObservation);
            deleteObservation = itemView.findViewById(R.id.deleteObservation);
            observationLayout = itemView.findViewById(R.id.observationLayout);

            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            observationLayout.setAnimation(translate_anim);
        }
    }

    private void deleteObservation(Observation observation, Integer id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + observation.getObservationName() + " ?");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDbHelper myDB = new MyDbHelper(context);
            boolean result = myDB.deleteObservation(id);
            if (!result) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete is successfully!", Toast.LENGTH_SHORT).show();
                activity.finish();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                context.startActivity(activity.getIntent());
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        builder.create().show();
    }
}