package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.MenuItem;

public class EmployeeDetailActivity extends AppCompatActivity {
    private EditText etName, etAge, etSalary, etProfileImage;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etSalary = findViewById(R.id.etSalary);
        etProfileImage = findViewById(R.id.etProfileImage);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressBar = findViewById(R.id.progressBar);

        apiService = RetrofitClient.getApiService();
        employeeId = getIntent().getStringExtra("employee_id");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Employee Details");
        }

        if (employeeId != null) {
            loadEmployee();
        }

        btnUpdate.setOnClickListener(v -> updateEmployee());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadEmployee() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getEmployee(employeeId).enqueue(new Callback<ApiResponse<Employee>>() {
            @Override
            public void onResponse(Call<ApiResponse<Employee>> call, Response<ApiResponse<Employee>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Employee emp = response.body().getData();
                    if (emp != null) {
                        etName.setText(emp.getName());
                        etAge.setText(String.valueOf(emp.getAge()));
                        etSalary.setText(String.valueOf(emp.getSalary()));
                        etProfileImage.setText(emp.getProfileImage());
                    }
                } else {
                    Toast.makeText(EmployeeDetailActivity.this, "Failed to load employee", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Employee>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EmployeeDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmployee() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String profileImage = etProfileImage.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || salaryStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        int salary = Integer.parseInt(salaryStr);

        EmployeeRequest request = new EmployeeRequest(name, age, salary, profileImage);

        progressBar.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);

        apiService.updateEmployee(employeeId, request).enqueue(new Callback<ApiResponse<Employee>>() {
            @Override
            public void onResponse(Call<ApiResponse<Employee>> call, Response<ApiResponse<Employee>> response) {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(EmployeeDetailActivity.this, "Employee updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EmployeeDetailActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Employee>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnUpdate.setEnabled(true);
                Toast.makeText(EmployeeDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
