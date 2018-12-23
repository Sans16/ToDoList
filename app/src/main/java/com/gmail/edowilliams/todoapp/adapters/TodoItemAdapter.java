package com.gmail.edowilliams.todoapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.edowilliams.todoapp.R;
import com.gmail.edowilliams.todoapp.models.TodoItem;

import java.util.List;

public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemAdapter.ViewHolder> {

    private Context mContext;
    private List<TodoItem> mTodoItems;
    private Listener mListener;

    public interface Listener{
        void deleteItem(int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitleTextView;
        Button deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            itemTitleTextView = (TextView) itemView.findViewById(R.id.itemTitle);
        }
    }

    public TodoItemAdapter(Context context, List<TodoItem> todoItems, Listener listener) {
        mTodoItems = todoItems;
        mContext = context;
        mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        TodoItem todoItem = mTodoItems.get(position);
        viewHolder.itemTitleTextView.setText(todoItem.getTitle());
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.deleteItem( viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }
}

