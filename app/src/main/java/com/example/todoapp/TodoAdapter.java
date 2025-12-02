package com.example.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private Context context;
    private List<TodoItem> todoItems;
    private TodoItemClickListener listener;

    public interface TodoItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onAssignContactClick(int position);
    }

    public TodoAdapter(@NonNull Context context, List<TodoItem> todoItems, TodoItemClickListener listener) {
        super(context, 0, todoItems);
        this.context = context;
        this.todoItems = todoItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        }

        TodoItem todoItem = todoItems.get(position);

        CheckBox checkBox = convertView.findViewById(R.id.todoCheckBox);
        TextView textView = convertView.findViewById(R.id.todoTextView);
        TextView contactTextView = convertView.findViewById(R.id.contactTextView);
        ImageButton btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageButton btnDelete = convertView.findViewById(R.id.btnDelete);
        ImageButton btnAssignContact = convertView.findViewById(R.id.btnAssignContact);

        textView.setText(todoItem.getText());
        checkBox.setChecked(todoItem.isSelected());

        if (todoItem.hasContact()) {
            contactTextView.setText("📞 " + todoItem.getContactName() + " - " + todoItem.getContactPhone());
            contactTextView.setVisibility(View.VISIBLE);
        } else {
            contactTextView.setVisibility(View.GONE);
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todoItem.setSelected(isChecked);
        });

        btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(position);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });

        btnAssignContact.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignContactClick(position);
            }
        });

        return convertView;
    }
}
