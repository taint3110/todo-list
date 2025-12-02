package com.example.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private Context context;
    private List<TodoItem> todoItems;

    public TodoAdapter(@NonNull Context context, List<TodoItem> todoItems) {
        super(context, 0, todoItems);
        this.context = context;
        this.todoItems = todoItems;
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

        return convertView;
    }
}
