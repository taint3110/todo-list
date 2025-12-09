package com.example.todoapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/api/v1/employees")
    Call<ApiResponse<List<Employee>>> getAll();
    
    @GET("/api/v1/employees")
    Call<okhttp3.ResponseBody> getAllRaw();

    @GET("/api/v1/employee/{id}")
    Call<ApiResponse<Employee>> getEmployee(@Path("id") String id);

    @POST("/api/v1/create")
    Call<ApiResponse<Employee>> createEmployee(@Body EmployeeRequest employee);

    @PUT("/api/v1/update/{id}")
    Call<ApiResponse<Employee>> updateEmployee(@Path("id") String id, @Body EmployeeRequest employee);

    @DELETE("/api/v1/delete/{id}")
    Call<ApiResponse<String>> deleteEmployee(@Path("id") String id);
}
