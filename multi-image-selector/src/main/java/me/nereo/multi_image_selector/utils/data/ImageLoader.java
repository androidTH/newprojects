package me.nereo.multi_image_selector.utils.data;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.Loader;

import java.util.ArrayList;

import me.nereo.multi_image_selector.bean.FolderImage;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.callback.DataCallback;

/**
 * Created by dmcBig on 2017/7/3.
 */

public class ImageLoader extends LoaderM implements LoaderManager.LoaderCallbacks{

    String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID };

    Context mContext;
    DataCallback mLoader;
    public ImageLoader(Context context, DataCallback loader){
        this.mContext=context;
        this.mLoader=loader;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                IMAGE_PROJECTION,
                null,
                null, // Selection args (none).
                MediaStore.Images.Media.DATE_ADDED + " DESC" // Sort order.
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<FolderImage> folders =new ArrayList<>();
        FolderImage allFolder =new FolderImage("AllImage");
        folders.add(allFolder);
        Cursor cursor=(Cursor) o;
        while (cursor.moveToNext()){
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size= cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            int id= cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

            if (size < 1) continue;
            String dirName=getParent(path);
            Image media=new Image(path,name,dateTime,mediaType,size);
            allFolder.addMedias(media);

            int index=hasDir(folders,dirName);
            if(index!=-1){
                folders.get(index).addMedias(media);
            }else{
                FolderImage folder =new FolderImage(dirName);
                folder.addMedias(media);
                folders.add(folder);
            }
        }
        mLoader.onData(folders);
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}