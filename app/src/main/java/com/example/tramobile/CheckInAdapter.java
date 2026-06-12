package com.example.tramobile;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tramobile.database.CheckIn;

import java.util.List;

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {

    private List<CheckIn> lista;

    public CheckInAdapter(List<CheckIn> lista) {
        this.lista = lista;
    }

    public List<CheckIn> getLista() {
        return lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckIn item = lista.get(position);
        holder.tvNome.setText(item.nome);
        holder.tvCategoria.setText(item.categoria);
        holder.tvCoordenadas.setText("📍 " + item.latitude + ", " + item.longitude);

        holder.itemView.setOnClickListener(v -> {
            Uri uri = Uri.parse("geo:" + item.latitude + "," + item.longitude +
                    "?q=" + item.latitude + "," + item.longitude + "(" + item.nome + ")");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(intent);
            } else {
                Uri browserUri = Uri.parse("https://maps.google.com/?q=" +
                        item.latitude + "," + item.longitude);
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, browserUri));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvCategoria, tvCoordenadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
        }
    }
}