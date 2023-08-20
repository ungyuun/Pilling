package com.pilling.app;

import static android.content.Intent.getIntent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static List<MyData> dataList;
    private Context context;
    private OnItemClickListener listener;
    private Dialog dialog;

    public MyAdapter(List<MyData> dataList, Context context, OnItemClickListener listener) {
        this.dataList = dataList;
        this.context = context;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(MyData item);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageButton imageButton;
        ImageView imageView;
        TextView textView;
        TextView prescriptionnm;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.prescription);
            textView = itemView.findViewById(R.id.date);
            imageButton = itemView.findViewById(R.id.delete2);
            prescriptionnm = itemView.findViewById(R.id.prescription_nm);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MyData item = dataList.get(position);
                listener.onItemClick(item);
            }
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_lobby_recycler, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyData data = dataList.get(position);
        holder.imageView.setImageBitmap(data.getBitmap());
        holder.textView.setText(data.getDate());
        holder.prescriptionnm.setText(data.getPrescription());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context,R.style.Dialog);
                dialog.setContentView(R.layout.prescription_delete_modal);
                Button yes = (Button)dialog.findViewById(R.id.yes);
                Button no = (Button)dialog.findViewById(R.id.no);
                TextView modal_prescription =  dialog.findViewById(R.id.modal_prescription);
                modal_prescription.setText(data.getPrescription()+"를 삭제하겠습니까?");
                yes.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(lobbyActivity.IMAGE_URL) // API의 기본 URL 설정
                        .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 설정
                        .build();

                        MyApi myApi = retrofit.create(MyApi.class);
                        Call<ResponseBody> call = myApi.postPrescription(data.getPrescription());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    System.out.println("디비저장 성공");
                                } else {
                                    System.out.println("디비저장 실패");
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                System.out.println("알람저장중 타 오류"+t.getMessage());
                            }
                        });
                    }
                });
                no.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }

                });
                dialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}