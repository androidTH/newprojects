package me.nereo.multi_image_selector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import me.nereo.multi_image_selector.utils.FinishActivityManager;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class MultiImageSelectorActivity extends AppCompatActivity implements MultiImageSelectorFragment.Callback {

    /** 最大图片选择次数，int类型，默认9 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** 图片选择模式，默认多选 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，默认显示 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
    public static final String EXTRA_RESULT = "select_result";
    /** 默认选择集 */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    public static final String EXTRA_PAYPOINTS = "paypoints";

    /** 图片选择模式，默认选视频和图片 */
    public static final String SELECT_MODE = "select_mode";

    public static final int PICKER_IMAGE = 100;
    public static final int PICKER_IMAGE_VIDEO = 101;
    public static final int PICKER_VIDEO = 102;

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mDefaultCount;
    private int mode;//模式
    private int modeType;
    private boolean mShowPayPoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        // 完成按钮
        mSubmitButton = (Button) findViewById(R.id.commit);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        modeType = intent.getIntExtra(SELECT_MODE,PICKER_IMAGE);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        mShowPayPoint = intent.getBooleanExtra(EXTRA_PAYPOINTS,false);

        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            mSubmitButton.setVisibility(View.VISIBLE);
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }else if(mode==MODE_SINGLE){
            mSubmitButton.setVisibility(View.GONE);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putInt(MultiImageSelectorFragment.SELECT_MODE,modeType);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_PAYPOINTS, mShowPayPoint);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        TextView mTvImageTitle = findViewById(R.id.tv_title);

        if(modeType== PICKER_VIDEO){
            mTvImageTitle.setText("视频");
        }

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        if(resultList == null || resultList.size()<=0){
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        }else{
            updateDoneText();
            mSubmitButton.setEnabled(true);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultList != null && resultList.size() >0){
                    // 返回已选择的图片数据
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

        FinishActivityManager.getManager().addActivity(this);
    }

    private void updateDoneText(){
        mSubmitButton.setText(String.format("%s(%d/%d)",
                getString(R.string.action_done), resultList.size(), mDefaultCount));

    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if(resultList.size() > 0){
            updateDoneText();
            if(!mSubmitButton.isEnabled()){
                mSubmitButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)){
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        if(resultList.size() == 0){
            mSubmitButton.setText(R.string.action_done);
            mSubmitButton.setEnabled(false);
        }
    }

    @Override
    public void onCameraShot(File imageFile) {

        if(imageFile != null) {

            // notify system
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            if (mode == MODE_SINGLE ) {
                Intent data = new Intent();
                resultList.add(imageFile.getAbsolutePath());
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
                finish();
            }else if (mode == MODE_MULTI){

            }
        }
    }
}
