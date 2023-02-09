package com.example.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoapp.Adapters.FinishTaskAdapter;
import com.example.todoapp.Adapters.TodoListAdapter;
import com.example.todoapp.UtilsService.SharedPreferenceClass;
import com.example.todoapp.interfaces.RecycleViewClickListener;
import com.example.todoapp.model.TodoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinishedTaskFrag extends Fragment implements RecycleViewClickListener {

    SharedPreferenceClass sharedPreferenceClass;
    RecyclerView recyclerView;
    TextView empty_tv;
    ProgressBar progressBar;
    FinishTaskAdapter finishTaskAdapter;
    ArrayList<TodoModel> arrayList;
    String token;

    public FinishedTaskFrag() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finished_task, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_string("token");
        recyclerView = view.findViewById(R.id.recycle_view);
        empty_tv = view.findViewById(R.id.empty_tv);
        progressBar = view.findViewById(R.id.progress_bar);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        getTask();

        return view;
    }

    private void getTask() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url ="https://todo-app-with-node-js.vercel.app/api/todo/finished";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        JSONArray jsonArray = response.getJSONArray("todos");

                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            TodoModel todoModel = new TodoModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description")
                            );
                            arrayList.add(todoModel);
                        }
                        finishTaskAdapter = new FinishTaskAdapter(getActivity(),arrayList,FinishedTaskFrag.this);
                        recyclerView.setAdapter(finishTaskAdapter);
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error == null || error.networkResponse == null) {
                    return;
                }
                String body;
                // final String statusCode = String.valueOf(error.networkResponse.statusCode);
                try{
                    body = new String(error.networkResponse.data, "UTF-8");
                    JSONObject errorObject = new JSONObject(body);
                    if(errorObject.getString("msg").equals("Token not valid")){
                        sharedPreferenceClass.clear();
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                        Toast.makeText(getActivity(), "Session expired", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(), body, Toast.LENGTH_SHORT).show();
                }catch (UnsupportedEncodingException | JSONException e){

                }
                progressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization",token);
                return headers;
            }
        };
        // set retry policy
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                ,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {

    }

    @Override
    public void onEditButtonClick(int position) {

    }

    @Override
    public void onDeleteButtonClick(int position) {
        showDeleteDialog(arrayList.get(position).getId(),position);
    }

    private void showDeleteDialog(String id, int position) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout,null);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you want to delete this task ?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("Cancel",null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteTask(id,position);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void deleteTask(String id, int position) {

        String url ="https://todo-app-with-node-js.vercel.app/api/todo/"+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){

                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        finishTaskAdapter.notifyItemRemoved(position);
                        getTask();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response !=null){
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(getActivity(), obj.getString("msg"),Toast.LENGTH_LONG).show();
                    } catch (JSONException | UnsupportedEncodingException js){
                        js.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("Content-Type","application/json");
                params.put("Authorization",token);

                return params   ;
            }
        };
        // set retry policy
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                ,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onDoneButtonClick(int position) {

    }
}