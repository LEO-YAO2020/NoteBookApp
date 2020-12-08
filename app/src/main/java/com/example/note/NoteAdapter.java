package com.example.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/*
 *
 * Name: Leo Yao
 * ID: 16287261
 *
 * */
public class NoteAdapter extends BaseAdapter {
    private Context mContext;
    Bitmap bitmap;
    Uri uri;
    private List<NoteObj> noteObjList;


    public NoteAdapter(Context mContext, List<NoteObj> noteObjList) {
        this.mContext = mContext;
        this.noteObjList = noteObjList;
    }

    public class ViewHolder {
        int position;
        TextView textView;
        TextView timeView;
        ImageView imageView;
    }

    @Override
    public int getCount() {
        return noteObjList.size();
    }

    @Override
    public Object getItem(int i) {
        return noteObjList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if (convertView == null){
            convertView  = View.inflate(mContext,R.layout.note_layout,null);
            vh = new ViewHolder();
            vh.textView = convertView.findViewById(R.id.note);
            vh.timeView = convertView.findViewById(R.id.note2);
            vh.imageView = convertView.findViewById(R.id.img);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        String allText = noteObjList.get(i).getContent();
        String path = noteObjList.get(i).getPath();

        //Determine whether there is a path, if there is a path, display otherwise hide the ImageView
        if (path != null ) {
            uri = Uri.parse(path);
            try {
                bitmap = Bitmap.createScaledBitmap(
                MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri),200,200,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            vh.imageView.setImageBitmap(bitmap);
        }else {
            vh.imageView.setVisibility(View.GONE);
        }
        vh.textView.setText(allText);
        vh.timeView.setText(noteObjList.get(i).getTime());
        vh.position=i;

        return convertView;
    }
}
