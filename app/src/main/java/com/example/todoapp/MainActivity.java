package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView todoListView;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoItems;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoListView = findViewById(R.id.todoListView);
        todoItems = new ArrayList<>();
        
        // Add some sample items
        todoItems.add(new TodoItem("Buy groceries"));
        todoItems.add(new TodoItem("Finish homework"));
        todoItems.add(new TodoItem("Call mom"));

        todoAdapter = new TodoAdapter(this, todoItems);
        todoListView.setAdapter(todoAdapter);

        // Register for context menu
        registerForContextMenu(todoListView);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.menu_new) {
            showAddTodoDialog();
            return true;
        } else if (id == R.id.menu_delete) {
            deleteSelectedItems();
            return true;
        } else if (id == R.id.menu_select_all) {
            selectAllItems();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.todoListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedPosition = info.position;
            getMenuInflater().inflate(R.menu.context_menu, menu);
            menu.setHeaderTitle("Todo Options");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.context_edit) {
            if (selectedPosition >= 0 && selectedPosition < todoItems.size()) {
                showEditTodoDialog(selectedPosition);
            }
            return true;
        } else if (id == R.id.context_delete) {
            if (selectedPosition >= 0 && selectedPosition < todoItems.size()) {
                todoItems.remove(selectedPosition);
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Todo deleted", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        
        return super.onContextItemSelected(item);
    }

    private void showAddTodoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Todo");

        final EditText input = new EditText(this);
        input.setHint("Enter todo text");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String todoText = input.getText().toString().trim();
            if (!todoText.isEmpty()) {
                todoItems.add(new TodoItem(todoText));
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Todo added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditTodoDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Todo");

        final EditText input = new EditText(this);
        input.setText(todoItems.get(position).getText());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String todoText = input.getText().toString().trim();
            if (!todoText.isEmpty()) {
                todoItems.get(position).setText(todoText);
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteSelectedItems() {
        List<TodoItem> itemsToRemove = new ArrayList<>();
        for (TodoItem item : todoItems) {
            if (item.isSelected()) {
                itemsToRemove.add(item);
            }
        }

        if (itemsToRemove.isEmpty()) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
        } else {
            todoItems.removeAll(itemsToRemove);
            todoAdapter.notifyDataSetChanged();
            Toast.makeText(this, itemsToRemove.size() + " item(s) deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectAllItems() {
        for (TodoItem item : todoItems) {
            item.setSelected(true);
        }
        todoAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All items selected", Toast.LENGTH_SHORT).show();
    }
}
