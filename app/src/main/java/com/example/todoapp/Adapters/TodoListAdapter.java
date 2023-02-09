package com.example.todoapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.interfaces.RecycleViewClickListener;
import com.example.todoapp.model.TodoModel;

import java.util.ArrayList;
import java.util.Random;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewHolder> {
    ArrayList<TodoModel> arrayList;
    Context context;
    final private RecycleViewClickListener clickListener;

    public TodoListAdapter(Context context, ArrayList<TodoModel> arrayList,RecycleViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item,parent,false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);

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
    public void onBindViewHolder(@NonNull TodoListAdapter.MyViewHolder holder, int position) {
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
        ImageView arrow, editBtn, deleteBtn, doneBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.task_title);
            descriptionTv = (TextView) itemView.findViewById(R.id.task_description);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.accordian_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            editBtn = (ImageView) itemView.findViewById(R.id.editBtn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.deleteBtn);
            doneBtn = (ImageView) itemView.findViewById(R.id.doneBtn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onEditButtonClick(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDeleteButtonClick(getAdapterPosition());
                }
            });

            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onDoneButtonClick(getAdapterPosition());
                }
            });

        }
    }
}
