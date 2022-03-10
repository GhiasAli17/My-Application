package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyOwnAdapter extends RecyclerView.Adapter<MyOwnAdapter.MyOwnHolder> {
    List<MyOwnModel> val;
    Context context;

    Button Inprocess;
    Button Complete;


    MyOwnAdapter(Context ctx,List<MyOwnModel> val1){
        val=val1;
        context=ctx;

    }


    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.layout_row,parent,false);
        return new MyOwnHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOwnHolder holder, int position) {
        MyOwnModel mdl=val.get(holder.getAdapterPosition());

        String[] splits=mdl.name.split("_");
        holder.name.setText(splits[0]);
        holder.service.setText(mdl.service);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,mdl.name+val.get(holder.getAdapterPosition()),Toast.LENGTH_LONG).show();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Inprocess.setVisibility(View.VISIBLE);
                Complete.setVisibility(View.VISIBLE);
                Log.d("accept","accept");

                //
                String customerName=holder.name.getText().toString();



                Intent intent=new Intent(context,Provider1MapsActivity.class);
              //  intent.putExtra("CurrentCustomerID",splits[1]);

                SharedPreferences pref=context.getSharedPreferences("pref",MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("CurrentCustomerID1",splits[1]);
              //  editor.apply();
                editor.commit();


                context.startActivity(intent);


            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("reject","reject");
            }
        });
    }



    @Override
    public int getItemCount() {
        return val.size();
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView service;
        Button btnAccept;
        Button btnReject;

        ConstraintLayout constraintLayout;

        public MyOwnHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            service=itemView.findViewById(R.id.service);
            btnAccept=itemView.findViewById(R.id.accept);
            btnReject=itemView.findViewById(R.id.reject);
            Inprocess = itemView.findViewById(R.id.inprocess);
            Complete = itemView.findViewById(R.id.complete);
            constraintLayout=itemView.findViewById(R.id.layout_row);
            Inprocess.setVisibility(View.INVISIBLE);
            Complete.setVisibility(View.INVISIBLE);
        }
    }
}
