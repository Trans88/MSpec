package com.trans.mspec;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private LineChartView mLineChartView;
    private List<Line> mLineList=new ArrayList<>();
    private List<PointValue> mPointViewList=new ArrayList<>();
    private List<AxisValue> mAxisValueList=new ArrayList<>();
    private List <Integer> mGray=new ArrayList<>(); //灰度值
    private ImageInfo imageInfo=new ImageInfo();
    float[]xDate={400,500,600,700};

    private ImageView mImageView;
    private Button mBtn_capture;
    private Button mBtn_album;
    private Button mBtn_source;
    private Button mBtn_absorb;
    private Button mBtn_save;

    private Uri imgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
//        getAxisXLables();//获取x轴的标注
//        getAxisPoints();//获取坐标点
//        初始化图表
    }

    private void initView() {
        mBtn_absorb = findViewById(R.id.btn_absorb);
        mBtn_album = findViewById(R.id.btn_album);
        mBtn_capture = findViewById(R.id.btn_capture);
        mBtn_source = findViewById(R.id.btn_source);
        mBtn_save = findViewById(R.id.btn_save);

        mBtn_capture.setOnClickListener(MainActivity.this);
        mBtn_save.setOnClickListener(MainActivity.this);
        mBtn_source.setOnClickListener(MainActivity.this);
        mBtn_album.setOnClickListener(MainActivity.this);
        mBtn_absorb.setOnClickListener(MainActivity.this);


        mImageView = findViewById(R.id.img_picture);

        mLineChartView=findViewById(R.id.lc_Chart);
    }

    /**
     * 初始化图表
     */
    private void setLineChart() {
        int x=400;
        int y=0;
        List<List<Integer>> lists = imageInfo.getmGrayList();
        Log.e("zbyzby", "setLineChart: +lists"+lists.size());
        List<Integer> grays=new ArrayList<>();
        for (int i=0;i<lists.size();i++){
            grays= lists.get(i);
        }
        for (int i=0;i<grays.size()&&i<20;i++){
            Log.e("zbyzby", "setLineChart: +integers"+grays.size());
            y=grays.get(i);
            PointValue pointValue=new PointValue(x,y);
            mPointViewList.add(pointValue);
            x+=100;
        }
        Line line=new Line(mPointViewList);
        line.setColor(Color.BLUE);
        line.setCubic(true);
        mLineList.add(line);
        //设置横坐标
        for (int i=0;i<xDate.length;i++){
            mAxisValueList.add(new AxisValue(i).setValue(xDate[i]));
        }

        Axis axisX=new Axis(mAxisValueList);

        LineChartData data=new LineChartData();
        data.setAxisXBottom(axisX);
        data.setLines(mLineList);
        mLineChartView.setLineChartData(data);
    }

    /**
     * 图表每个点的显示
     */
    private void getAxisPoints() {
//        for (int i = 0; i < score.length; i++) {
//            mPointValues.add(new PointValue(i, score[i]));
//        }
    }

    /**
     * 设置X轴显示
     */
    private void getAxisXLables() {
//        for (int i = 0; i < date.length; i++) {
//            mAxisValues.add(new AxisValue(i).setLabel(date[i]));
//        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                //清空数据
                LineChartData data=new LineChartData();
                data.setLines(mLineList);
                mLineChartView.setLineChartData(data);
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imgUri = FileProvider.getUriForFile(MainActivity.this,"com.trans.mspec.fileprovider",outputImage);
                } else {
                    imgUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent,TAKE_PHOTO);
                break;
            case R.id.btn_album:
                mLineList.clear();
                LineChartData dataAlbun=new LineChartData();
                dataAlbun.setLines(mLineList);
                mLineChartView.setLineChartData(dataAlbun);
                mGray.clear();
                mPointViewList.clear();
                mAxisValueList.clear();

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest
                            .permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.btn_save:
                imageInfo.addImage(mGray);
                setLineChart();
                break;
            case R.id.btn_absorb:
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == 1) {
                    Log.d("zbyzby", "onActivityResult: 返回成功");
                    try {

                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imgUri));
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        for (int i = 0; i < width; i++) {
                            int y=height/2;
                            int x=i;
                            Log.e("zbyzby", "onActivityResult: "+x);
                            int color=bitmap.getPixel(x,y);
                            int r=Color.red(color);
                            int g=Color.green(color);
                            int b=Color.blue(color);
                            int gray = (int) (0.3*r+0.59*g+0.11*b);
                            mGray.add(gray);
                        }

                        //Log.d("zbyzby", "height: "+height+"width"+width+"r  g  b"+r+", "+g+", "+b+"");
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (requestCode == 2) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                Log.d("zbyzby", "onActivityResult: 返回失败");
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的ID
                String selection = MediaStore.Images.Media._ID + "=" + id;//todo
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse
                        ("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        Log.d("zbyzby", "displayImage: " + imagePath);
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mImageView.setImageBitmap(bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            for (int i = 0; i < width; i++) {
                int y = height / 2;
                int x = i;
                int color = bitmap.getPixel(x, y);
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                mGray.add(gray);
            }
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
