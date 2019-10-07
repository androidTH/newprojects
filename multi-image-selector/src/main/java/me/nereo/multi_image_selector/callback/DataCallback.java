package me.nereo.multi_image_selector.callback;


import java.util.ArrayList;

import me.nereo.multi_image_selector.bean.FolderImage;


/**
 * Created by dmcBig on 2017/7/3.
 */

public interface DataCallback {


     void onData(ArrayList<FolderImage> list);

}
