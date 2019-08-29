package me.nereo.multi_image_selector.utils.data;


import java.util.ArrayList;

import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.FolderImage;

/**
 * Created by dmcBig on 2017/7/20.
 */

public class LoaderM {

    public String getParent(String path) {
        String sp[]=path.split("/");
        return sp[sp.length-2];
    }

    public int hasDir(ArrayList<FolderImage> folders, String dirName){
        for(int i = 0; i< folders.size(); i++){
            FolderImage folder = folders.get(i);
            if( folder.name.equals(dirName)) {
                return i;
            }
        }
        return -1;
    }


}
