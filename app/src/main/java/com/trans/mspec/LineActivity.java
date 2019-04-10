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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class LineActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 1;         //线条的数量
//    private int maxNumberOfLines = 4;     //最大的线条数据
    private int numberOfPoints = 76;     //点的数量
    private int jianju;

    private List<Integer> grays = new ArrayList<>();
    private List<List<PointValue>> imagePoints = new ArrayList<>();

    float[] mGray = new float[numberOfPoints]; //二维数组，线的数量和点的数量

    private boolean hasAxes = true;       //是否有轴，x和y轴
    private boolean hasAxesNames = false;   //是否有轴的名字
    private boolean hasLines = true;       //是否有线（点和点连接的线）
    private boolean hasPoints = false;       //是否有点（每个值的点）
    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
    private boolean isFilled = false;                //是否是填充
    private boolean hasLabels = false;               //每个点是否有名字
    private boolean isCubic = true;                 //是否是立方的，线条是直线还是弧线
    private boolean hasLabelForSelected = false;       //每个点是否可以选择（点击效果）
    private boolean pointsHaveDifferentColor=false;           //线条的颜色变换
    private boolean hasGradientToTransparent = false;      //是否有梯度的透明

    private ImageView mImageView;
    private Button mBtn_capture;
    private Button mBtn_album;
    private Button mBtn_source;
    private Button mBtn_absorb;
    private Button mBtn_save;

    private Uri imgUri;
    private Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        //实例化
        chart = findViewById(R.id.lc_Chart);

        mBtn_absorb = findViewById(R.id.btn_absorb);
        mBtn_album = findViewById(R.id.btn_album);
        mBtn_capture = findViewById(R.id.btn_capture);
//        mBtn_source = findViewById(R.id.btn_source);
//        mBtn_save = findViewById(R.id.btn_save);

        mBtn_capture.setOnClickListener(LineActivity.this);
//        mBtn_save.setOnClickListener(LineActivity.this);
//        mBtn_source.setOnClickListener(LineActivity.this);
        mBtn_album.setOnClickListener(LineActivity.this);
        mBtn_absorb.setOnClickListener(LineActivity.this);


        mImageView = findViewById(R.id.img_picture);
    }

    private void initData() {
        // Generate some random values.
//        generateValues();   //设置四条线的值数据
//        generateData();    //设置数据

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
        resetViewport();   //设置折线图的显示大小
    }

    private void initEvent() {
        chart.setOnValueTouchListener(new ValueTouchListener());

    }

    /**
     * 图像显示大小
     */
    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 310;
        v.left = 350;
        v.right = 570;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 设置四条线条的数据
     */
    private void generateValues() {
        grays.clear();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        jianju=width/76;
        for (int k = 0; k < width; k += jianju) {
            int y = height / 2;
            int x = k;
            Log.e("zbyzby", "onActivityResult: " + x);
            int color = bitmap.getPixel(x, y);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
            grays.add(gray);
        }
        for (int j = 0; j < numberOfPoints&&j<grays.size(); ++j) {
            Log.e("zbyzby", "generateValues: "+grays.get(j)+"      "+grays.size());
            mGray[j] = grays.get(j);
        }
    }

    /**
     * 配置数据
     */
    private void generateData() {
        //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();
        //把数据设置到线条上面去
        for (int i = 0; i < numberOfLines; ++i) {
            int k=380;
            List<PointValue> values = new ArrayList();
//            values.add(new PointValue(380,30));
            for (int j = 1;j< numberOfPoints; j++) {
                k=k+jianju;
                values.add(new PointValue(k, mGray[j]));
            }

            imagePoints.add(values);

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setTextColor(Color.BLACK);//设置x轴字体的颜色
                axisY.setTextColor(Color.BLACK);//设置y轴字体的颜色
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imgUri = FileProvider.getUriForFile(LineActivity.this, "com.trans.mspec.fileprovider", outputImage);
                } else {
                    imgUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.btn_album:
                if (ContextCompat.checkSelfPermission(LineActivity.this, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LineActivity.this, new String[]{Manifest
                            .permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
//            case R.id.btn_save:
//                break;
            case R.id.btn_absorb:
                List<Float> yOnes=new ArrayList<>();
                List<Float> yTwos=new ArrayList<>();
                if (imagePoints.get(0)==null){
                    Toast.makeText(LineActivity.this,"请放入第一张图片",Toast.LENGTH_SHORT);
                }else {
                    List<PointValue> pointValuesOne = imagePoints.get(0);
                    for (int i = 0; i <pointValuesOne.size() ; i++) {
                        PointValue pointValueone = pointValuesOne.get(i);
                        float yOne = pointValueone.getY();
                        yOnes.add(yOne);
                    }
                }
                if (imagePoints.get(1)==null){
                    List<PointValue> pointValuesTwo = imagePoints.get(1);
                    for (int i = 0; i <pointValuesTwo.size() ; i++) {
                        PointValue pointValueTwo = pointValuesTwo.get(0);
                        float yTwo = pointValueTwo.getY();
                        yTwos.add(yTwo);
                    }
                }
                for (int i = 0; i <yOnes.size()&&i<yTwos.size() ; i++) {
                    float v1 = yOnes.get(i) / yTwos.get(i);
                    float v2 = (float) Math.log10(v1);
                    Log.e("zbyzby", "onClick: "+v2+"   "+yOnes.get(i)+"  "+yTwos.get(i) );
                    mGray[i]=v2;
                }
                Viewport viewport = new Viewport(chart.getMaximumViewport());
                viewport.bottom = -1;
                viewport.top = 1;
                viewport.left = 380;
                viewport.right = 790;
                chart.setMaximumViewport(viewport);
                chart.setCurrentViewport(viewport);
                generateData();

                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
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
                Log.d("zbyzby", "onActivityResult: 返回成功");
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(imgUri));
                    mImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                generateValues();
                generateData();
                break;
            case CHOOSE_PHOTO:
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
                generateValues();
                generateData();
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
            bitmap = BitmapFactory.decodeFile(imagePath);
            mImageView.setImageBitmap(bitmap);
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

    /**
     * 触摸监听类
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(LineActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {


        }

    }

}
