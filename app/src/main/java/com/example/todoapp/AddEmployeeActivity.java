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

public class AddEmployeeActivity extends AppCompatActivity {
    private EditText etName, etAge, etSalary, etProfileImage;
    private Button btnSave;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etSalary = findViewById(R.id.etSalary);
        etProfileImage = findViewById(R.id.etProfileImage);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        apiService = RetrofitClient.getApiService();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Employee");
        }

        btnSave.setOnClickListener(v -> saveEmployee());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEmployee() {
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
        btnSave.setEnabled(false);

        apiService.createEmployee(request).enqueue(new Callback<ApiResponse<Employee>>() {
            @Override
            public void onResponse(Call<ApiResponse<Employee>> call, Response<ApiResponse<Employee>> response) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AddEmployeeActivity.this, "Employee created", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEmployeeActivity.this, "Failed to create employee", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Employee>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(AddEmployeeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
