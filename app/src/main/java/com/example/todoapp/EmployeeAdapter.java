package com.example.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Employee employee);
    }

    public EmployeeAdapter(Context context, List<Employee> employees, OnDeleteClickListener listener) {
        super(context, 0, employees);
        this.deleteListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.employee_item, parent, false);
        }

        Employee employee = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvAge = convertView.findViewById(R.id.tvAge);
        TextView tvSalary = convertView.findViewById(R.id.tvSalary);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(employee.getName());
        tvAge.setText("Age: " + employee.getAge());
        tvSalary.setText("Salary: $" + employee.getSalary());

        btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(employee);
            }
        });

        return convertView;
    }
}
