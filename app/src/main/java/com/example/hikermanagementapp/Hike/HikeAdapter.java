package com.example.hikermanagementapp.Hike;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
//import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hikermanagementapp.Database.Hike;
import com.example.hikermanagementapp.Database.MyDbHelper;
import com.example.hikermanagementapp.Observation.ObservationActivity;
import com.example.hikermanagementapp.R;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.MyViewHolder> implements Filterable {

    private final Context context;
    private final Activity activity;
    private List<Hike> hikes;
    private final List<Hike> hikesOld;

    public HikeAdapter(Activity activity, Context context, List<Hike> hikes) {
        this.activity = activity;
        this.context = context;
        this.hikes = hikes;
        this.hikesOld = new ArrayList<>(hikes);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context); // get layout inflater
        View view = inflater.inflate(R.layout.activity_hike_adapter, parent, false); // inflate layout
        return new MyViewHolder(view); // return view
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Hike hike = hikes.get(position);
        String name = hike.getName();
        String location = hike.getLocation();
        String date = hike.getDate();
        String length = hike.getLength();
        String difficulty = hike.getDifficulty();
        String weatherCondition = hike.getWeatherCondition();
        String terrain = hike.getTerrainType();
        String parkingStatus = hike.getParkingAvailable();


        MyDbHelper myDB = new MyDbHelper(context);


        // set value to form
        holder.hikeName.setText(name);
        holder.hikeLocation.setText(location);
        holder.hikeDate.setText(date);
        holder.hikeLength.setText(length);
        holder.hikeDifficulty.setText(difficulty);
        holder.hikeWeatherCondition.setText(weatherCondition);
        holder.hikeParkingAvailable.setText(parkingStatus);
        holder.hikeTerrain.setText(terrain);
        holder.editHike.setOnClickListener(view -> {
            //passing parameter values
            Intent intent = new Intent(context, UpdateHikeActivity.class);
            intent.putExtra("selectedHike", hike);
            activity.startActivityForResult(intent, 1);
        });

        holder.deleteHike.setOnClickListener(view -> deleteHike(hike.getId(), hike.getName()));

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ObservationActivity.class);
            intent.putExtra("selectedHike", hike);
            activity.startActivityForResult(intent, 1);
        });
    }

    private void deleteHike(int id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete " + name + " ?");
        builder.setMessage("Are you sure you want to delete ?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDbHelper myDB = new MyDbHelper(context);
            boolean result = myDB.deleteHike(id);
            if (!result) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete Successfully!", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, HikeActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return hikes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView editHike, deleteHike;
        TextView hikeName, hikeLocation, hikeDate, hikeLength, hikeDifficulty, hikeTerrain, hikeWeatherCondition, hikeParkingAvailable;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            hikeName = itemView.findViewById(R.id.hikeName);
            hikeLocation = itemView.findViewById(R.id.hikeLocation);
            hikeDate = itemView.findViewById(R.id.hikeDate);
            hikeLength = itemView.findViewById(R.id.hikeLength);
            hikeDifficulty = itemView.findViewById(R.id.hikeDifficulty);
            hikeWeatherCondition = itemView.findViewById(R.id.hikeWeather);
            hikeTerrain = itemView.findViewById(R.id.hikeTerrain);
            hikeParkingAvailable = itemView.findViewById(R.id.hikeParking);
            editHike = itemView.findViewById(R.id.editHike);
            deleteHike = itemView.findViewById(R.id.deleteHike);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    hikes = hikesOld;
                } else {
                    List<Hike> list = new ArrayList<>();
                    for (Hike hike : hikesOld) {
                        if (hike.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(hike);
                        } else if (hike.getLocation().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(hike);
                        }
                    }
                    hikes = list;
                }
                FilterResults result = new FilterResults();
                result.values = hikes;
                return result;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                hikes = (List<Hike>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}