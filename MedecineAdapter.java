package com.pilling.kakaologin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MedecineAdapter extends RecyclerView.Adapter<MedecineAdapter.ViewHolder> {
    private static List<MedecineData> dataList;
    private Context context;
    private OnItemClickListener listener;

    public MedecineAdapter(List<MedecineData> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(MedecineData item);
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        TextView efficacyView;
        TextView shapeView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.prescription);
            textView = itemView.findViewById(R.id.drug_name);
            efficacyView = itemView.findViewById(R.id.efficacy);
            shapeView = itemView.findViewById(R.id.shape);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MedecineData item = dataList.get(position);
                listener.onItemClick(item);
            }
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_more_info_recycler, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedecineData data = dataList.get(position);
        System.out.println("온바인드뷰홀더에서 url :"+data.getThumbnail());
        Glide.with(holder.itemView.getContext())
                .load(data.getThumbnail()) // 이미지 URL
                .apply(new RequestOptions()
//                        .placeholder(R.drawable.placeholder) // 로드 중에 표시할 이미지
//                        .error(R.drawable.error)// 로드 실패 시 표시할 이미지
                )
//                .transition(DrawableTransitionOptions.withCrossFade()) // 이미지 전환 애니메이션
                .into(holder.imageView);
        holder.textView.setText(data.getMedecine());
        holder.efficacyView.setText(data.getEfficacy());
        holder.shapeView.setText(data.getShape());
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}