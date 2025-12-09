package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.MenuItem;

public class EmployeeActivity extends AppCompatActivity implements EmployeeAdapter.OnDeleteClickListener {
    private static final String TAG = "EmployeeActivity";
    private ListView listView;
    private ProgressBar progressBar;
    private Button btnAdd;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Employees");
        }

        listView = findViewById(R.id.employeeListView);
        progressBar = findViewById(R.id.progressBar);
        btnAdd = findViewById(R.id.btnAddEmployee);

        apiService = RetrofitClient.getApiService();
        adapter = new EmployeeAdapter(this, employeeList, this);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Employee emp = employeeList.get(position);
            Intent intent = new Intent(this, EmployeeDetailActivity.class);
            intent.putExtra("employee_id", emp.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getAllRaw().enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        Log.d(TAG, "API Response: " + json);
                        
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        
                        // API returns array directly, not wrapped in object
                        List<Employee> employees = gson.fromJson(json, 
                            new com.google.gson.reflect.TypeToken<List<Employee>>(){}.getType());
                        
                        employeeList.clear();
                        if (employees != null) {
                            employeeList.addAll(employees);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error", e);
                        Toast.makeText(EmployeeActivity.this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EmployeeActivity.this, "Error loading employees", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load employees", t);
                Toast.makeText(EmployeeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteClick(Employee employee) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.deleteEmployee(employee.getId()).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(EmployeeActivity.this, "Employee deleted", Toast.LENGTH_SHORT).show();
                    loadEmployees();
                } else {
                    Toast.makeText(EmployeeActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmployeeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
