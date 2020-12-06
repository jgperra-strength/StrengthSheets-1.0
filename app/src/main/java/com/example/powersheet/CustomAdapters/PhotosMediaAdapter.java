package com.example.powersheet.CustomAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.powersheet.APIControllers.PhotosAPIController;
import com.example.powersheet.APIDataStructures.PhotosMediaItem;
import com.example.powersheet.PhotosLibraryActivity;
import com.example.powersheet.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhotosMediaAdapter extends ArrayAdapter {
    ArrayList<PhotosMediaItem> items;
    Context context;
    boolean checkable;
    boolean localRemoveOnly;

    public PhotosMediaAdapter(Context c, ArrayList<PhotosMediaItem> e, int position, boolean checkable, boolean localRemoveOnly) {
        super(c, position);
        context = c;
        items = e;
        this.checkable = checkable;
        this.localRemoveOnly = localRemoveOnly;
    }

    public ArrayList<PhotosMediaItem> getItems() {
        return items;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PhotosMediaItem item = items.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.photos_media_entry, null);

            ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setImageBitmap(item.getThumbnail());

            TextView uploadDate = (TextView) view.findViewById(R.id.dateTextView);
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            uploadDate.setText(item.getDate() == null ? "" : sdf.format(item.getDate()));

            if (!checkable) {
                ((Button) view.findViewById(R.id.removeButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        if (!localRemoveOnly) {
                                            PhotosAPIController photosAPIController = new PhotosAPIController(context);
                                            photosAPIController.removeItemFromAlbum(PhotosMediaAdapter.this, (ListView) viewGroup, i);
                                        }
                                        else {
                                            items.remove(i);
                                            if (!items.isEmpty()) ((ListView) viewGroup).setAdapter(PhotosMediaAdapter.this);
                                            else ((ListView) viewGroup).setAdapter(null);
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete this media item?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }
            else {
                ((Button) view.findViewById(R.id.removeButton)).setVisibility(View.GONE);
                ((CheckBox) view.findViewById(R.id.checkBox)).setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
