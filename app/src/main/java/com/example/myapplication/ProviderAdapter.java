package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder>{

    List<ProviderModel> val;
    Context context;

    Button sendRequest;


    ProviderAdapter(Context ctx,List<ProviderModel> val1){
        val=val1;
        context=ctx;

    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.provider_list_row,parent,false);
        return new ProviderViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {

        ProviderModel mdl=val.get(holder.getAdapterPosition());

        holder.rating.setText(mdl.rating);
        holder.distance.setText(""+mdl.distance);
        holder.providerID.setText(mdl.providerID);
        holder.phone.setText(mdl.phone);

        holder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DatabaseReference ProviderLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");

                ProviderLocationRef.child(holder.providerID.getText().toString())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                holder.btnSend.setEnabled(false);
                Toast.makeText(context,"Your request has been sent",Toast.LENGTH_LONG).show();

            }
        });

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.phone.getText().toString()));
                context.startActivity(intent);
            }
        });


        holder.btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chat Message by Naveed Start from here
            }
        });

    }

    @Override
    public int getItemCount() {
        return val.size();
    }



    // Below is class of View Holding

    public class ProviderViewHolder extends RecyclerView.ViewHolder{

        TextView rating;
        TextView distance;
        TextView providerID;
        TextView phone;
        Button btnSend;
        Button btnCall;
        Button btnSMS;

        LinearLayoutCompat constraintLayout;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            rating=itemView.findViewById(R.id.SPratingValue);
            distance=itemView.findViewById(R.id.SPdistanceValue);
            providerID=itemView.findViewById(R.id.SPnameValue);
            btnSend=itemView.findViewById(R.id.sendrequest);
            btnCall=itemView.findViewById(R.id.call);
            phone=itemView.findViewById(R.id.SPphoneValue);
            btnSMS=itemView.findViewById(R.id.sms);

            constraintLayout=itemView.findViewById(R.id.provider_list_row);
        }
    }

}
