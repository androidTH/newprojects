package me.nereo.multi_image_selector.utils.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;

import me.nereo.multi_image_selector.bean.FolderImage;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.callback.DataCallback;

/**
 * Created by dmcBig on 2017/7/3.
 */
public class VideosLoader extends LoaderM implements LoaderManager.LoaderCallbacks{

    private final String[] VIDEO_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID };

//    MediaStore.Files.FileColumns.DISPLAY_NAME,
//    MediaStore.Files.FileColumns.DATE_ADDED,

    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID};

    Context mContext;
    DataCallback mLoader;
    public VideosLoader(Context context, DataCallback loader){
        this.mContext=context;
        this.mLoader=loader;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                VIDEO_PROJECTION,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
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

            String path = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
//            String name = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
//            long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
//            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));

            if (size < 1) continue;
            String dirName= getParent(path);
            Image media=new Image(path,"",0,3,size);
            media.id = id;
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