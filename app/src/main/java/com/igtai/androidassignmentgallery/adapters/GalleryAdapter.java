package com.igtai.androidassignmentgallery.adapters;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.igtai.androidassignmentgallery.R;

import java.io.IOException;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private Context context;
    private List<String> imagePaths;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String imagePath);
    }

    public GalleryAdapter(Context context, List<String> imagePaths, OnImageClickListener listener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(context).load(imagePath).into(holder.imageView);

        // Handle image click event
        holder.imageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(imagePath);
            }
        });

        //Handle long press click event
        holder.imageView.setOnLongClickListener(view -> {
            showPopupMenu(view, imagePaths.get(position));
            return true;
        });
    }

    private void showPopupMenu(View view, String imagePath) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.image_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_set_wallpaper) {
                setAsWallpaper(imagePath);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void setAsWallpaper(String imagePath) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(context, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to Set Wallpaper", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
