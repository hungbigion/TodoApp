package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.interfaces.RecycleViewClickListener;
import com.example.todoapp.model.TodoModel;

import java.util.ArrayList;
import java.util.Random;

public class FinishTaskAdapter  extends RecyclerView.Adapter<FinishTaskAdapter.MyViewHolder> {
    ArrayList<TodoModel> arrayList;
    Context context;
    final private RecycleViewClickListener clickListener;

    public FinishTaskAdapter(Context context, ArrayList<TodoModel> arrayList,RecycleViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public FinishTaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.finished_task_item_holder,parent,false);

        final FinishTaskAdapter.MyViewHolder myViewHolder = new FinishTaskAdapter.MyViewHolder(view);

        int[] androidcolors = view.getResources().getIntArray(R.array.androidcolors);
        int randomcolors = androidcolors[new Random().nextInt(androidcolors.length)];

        myViewHolder.accordian_title.setBackgroundColor(randomcolors);
        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myViewHolder.accordian_body.getVisibility()==View.VISIBLE){
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                } else {
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FinishTaskAdapter.MyViewHolder holder, int position) {
        final String title = arrayList.get(position).getTitle();
        final String description = arrayList.get(position).getDescription();
        final String id= arrayList.get(position).getId();

        holder.titleTv.setText(title);
        if(!description.equals("")){
            holder.descriptionTv.setText(description);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView titleTv,descriptionTv;
        RelativeLayout accordian_body;
        ImageView arrow, deleteBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.task_title);
            descriptionTv = (TextView) itemView.findViewById(R.id.task_description);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.accordian_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });




            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDeleteButtonClick(getAdapterPosition());
                }
            });



        }
    }
}
