package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import android.content.Intent;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int READ_CONTACTS_REQUEST_CODE = 100;
    private static final int CONTACT_LOADER = 1;

    private ListView todoListView;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoItems;
    private List<ContactItem> contacts;
    private TodoRepo todoRepo;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoListView = findViewById(R.id.todoListView);
        todoItems = new ArrayList<>();
        contacts = new ArrayList<>();
        todoRepo = new TodoRepo(this);
        
        // Load todos from database
        loadTodosFromDatabase();

        todoAdapter = new TodoAdapter(this, todoItems);
        todoListView.setAdapter(todoAdapter);

        // Register for context menu
        registerForContextMenu(todoListView);

        // Request contacts permission
        checkContactsPermission();
    }

    private void loadTodosFromDatabase() {
        todoItems.clear();
        todoItems.addAll(todoRepo.loadAll());
    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_REQUEST_CODE);
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
                Toast.makeText(this, "Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == CONTACT_LOADER) {
            String[] SELECTED_FIELDS = new String[]{
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            };
            return new CursorLoader(this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    SELECTED_FIELDS,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACT_LOADER) {
            contacts.clear();
            if (data != null) {
                while (!data.isClosed() && data.moveToNext()) {
                    String phone = data.getString(1);
                    String name = data.getString(2);
                    contacts.add(new ContactItem(name, phone));
                }
                data.close();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Loader reset
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
        } else if (id == R.id.menu_employees) {
            startActivity(new Intent(this, EmployeeActivity.class));
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
            menu.setHeaderTitle("Todo Options");
            menu.add(0, 1, 0, "Edit");
            menu.add(0, 2, 1, "Assign Contact");
            menu.add(0, 3, 2, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == 1) { // Edit
            if (selectedPosition >= 0 && selectedPosition < todoItems.size()) {
                showEditTodoDialog(selectedPosition);
            }
            return true;
        } else if (id == 2) { // Assign Contact
            if (selectedPosition >= 0 && selectedPosition < todoItems.size()) {
                showContactPickerDialog(selectedPosition);
            }
            return true;
        } else if (id == 3) { // Delete
            if (selectedPosition >= 0 && selectedPosition < todoItems.size()) {
                TodoItem item1 = todoItems.get(selectedPosition);
                todoRepo.delete(item1.getId());
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
                TodoItem newItem = new TodoItem(todoText);
                long id = todoRepo.addNew(newItem);
                newItem.setId(id);
                todoItems.add(newItem);
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
                TodoItem item = todoItems.get(position);
                item.setText(todoText);
                todoRepo.update(item);
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Todo updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showContactPickerDialog(int position) {
        if (contacts.isEmpty()) {
            Toast.makeText(this, "No contacts available", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Contact");

        String[] contactNames = new String[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            contactNames[i] = contacts.get(i).toString();
        }

        builder.setItems(contactNames, (dialog, which) -> {
            ContactItem selectedContact = contacts.get(which);
            TodoItem todoItem = todoItems.get(position);
            todoItem.setContactName(selectedContact.getName());
            todoItem.setContactPhone(selectedContact.getPhone());
            todoRepo.update(todoItem);
            todoAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Contact assigned", Toast.LENGTH_SHORT).show();
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
            for (TodoItem item : itemsToRemove) {
                todoRepo.delete(item.getId());
            }
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
