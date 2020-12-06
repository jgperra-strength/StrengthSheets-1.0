package com.example.powersheet.CustomAdapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.powersheet.APIDataStructures.PhotosAlbum;
import com.example.powersheet.R;
import com.example.powersheet.Training.Block;
import com.example.powersheet.UserData.User;
import com.example.powersheet.Util.FileSystemUtil;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter {
    ArrayList<PhotosAlbum> albums;
    User user;
    Context context;

    public AlbumAdapter(Context c, ArrayList<PhotosAlbum> e, User u, int position) {
        super(c, position);
        context = c;
        albums = e;
        user = u;
    }

    public ArrayList<PhotosAlbum> getAlbums() {
        return albums;
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
        return albums.size();
    }

    @Override
    public Object getItem(int i) {
        return albums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return albums.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PhotosAlbum album = albums.get(i);
        final int idx = i;
        final ListView parent = (ListView) viewGroup;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.genric_entry_view, null);

            final View v = view;

            TextView albumTitle = (TextView) view.findViewById(R.id.titleTextView);
            albumTitle.setText(album.getTitle());

            Button optionsButton = (Button) view.findViewById(R.id.optionsButton);
            optionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(v.getContext(), view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.exercise_attribute_menu, popup.getMenu());
                    popup.getMenu().add(0,0, Menu.NONE, "Edit");
                    popup.getMenu().add(0, 1, Menu.NONE, "Delete");
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case 0 :
                                    //editDay(idx, parent);
                                    break;
                                case 1:

                                    albums.remove(idx);
                                    FileSystemUtil.serializeUserObject(user);
                                    if (!albums.isEmpty()) parent.setAdapter(AlbumAdapter.this);
                                    else parent.setAdapter(null);
                                    break;
                            }

                            return false;
                        }
                    });
                }
            });
        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return albums.size();
    }

    @Override
    public boolean isEmpty() {
        return albums.isEmpty();
    }
}
