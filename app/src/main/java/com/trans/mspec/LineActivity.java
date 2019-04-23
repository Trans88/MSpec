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
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private static final String TAG = "LineActivity";
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CHOOSE_PHOTO_FROMCOLOR = 3;

    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 1;         //线条的数量
//    private int maxNumberOfLines = 4;     //最大的线条数据
    private int numberOfPoints = 255;     //点的数量
    private float jianju;
    private float[] xRmd={0.0000f,0.0000f,0.0001f,0.0002f,0.0003f,0.0005f,0.0008f,
            0.0014f,0.0021f,0.0027f,0.0021f,0.0027f,0.0022f,0.004f,-0.0026f,-0.0067f,
            -0.0121f,-0.0187f,-0.0261f ,0.0021f,0.0027f,0.0022f,0.0004f,-0.0026f,
            -0.0067f,-0.0121f,-0.0187f,-0.0261f,-0.0332f,-0.0393f,-0.0447f,-0.0494f,
            -0.0536f,-0.0581f,-0.0614f,-0.0717f,-0.0812f,-0.0890f,-0.0936f,-0.0926f,
            -0.0847f,-0.0710f,-0.0514f,-0.0315f,-0.0061f,0.0228f,0.0906f,0.1284f,0.1677f,
            0.2072f,0.2453f,0.2799f,0.3093f,0.3318f,0.3443f,0.3476f,0.3397f,0.3227f,0.2971f,
            0.2635f,0.2268f,0.1923f,0.1597f,0.1291f,0.1017f,0.0786f,0.0593f,0.0437f,0.0315f,
            0.0229f,0.0169f,0.0119f,0.0082f,0.0057f,0.0041f,0.0029f,0.0021f,0.0015f,0.0011f,
            0.0007f,0.0005f,0.0004f,0.0003f,0.0002f,0.0001f,0.0001f,0.0001f,0f,0f,0f,0f};
    private float[] yRmd={0f,0f,0f,-0.0001f,-0.0002f,-0.0002f,-0.0004f,-0.0007f,-0.0011f,-0.0014f,-0.0012f,-0.0002f,0.0015f,0.0038f,0.0068f,0.0104f,0.0149f,0.0198f,0.0254f,0.0318f,0.0391f,0.0471f,0.0569f,0.0695f,0.0854f,0.1059f,0.1286f,0.1526f,0.1765f,0.1911f,0.2072f,0.2108f,0.2147f,0.2149f,0.2118f,0.1970f,0.1852f,0.1709f,0.1543f,0.1361f,0.1169f,0.0975f,0.0791f,0.0625f,0.0477f,0.0356f,0.0258f,0.0183f,0.0125f,0.0083f,0.0054f,0.0033f,0.0020f,0.0012f,0.0007f,0.0004f,0.0002f,0.0001f,0.0001f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f, };
    private float[] zRmd={0.0012f,0.0019f,0.0036f,0.0065f,0.0121f,0.0197f,0.0371f,0.0664f,
            0.1154f,0.01857f,0.2476f,0.2901f,0.3123f,0.3186f,0.3167f,0.3117f,0.2982f,0.2730f,
            0.2299f,0.1859f,0.1449f,0.1097f,0.0826f,0.0625f,0.0478f,0.0369f,0.0270f,0.0184f,
            0.0122f,0.0083f,0.0055f,0.0032f,0.0015f,0.0002f,-0.0006f,-0.0013f,-0.0014f,-0.0014f,
            -0.0012f,-0.0011f,-0.0009f,-0.0008f,-0.0006f,-0.0005f,-0.0004f,-0.0003f,-0.0002f,-0.0002f,
            -0.0001f,-0.0001f,-0.0001f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,
            0f,0f,0f,0f,0f,0f,0f,0f,0f};

    private List<Integer> grays = new ArrayList<>();
    private List<List<PointValue>> imagePoints = new ArrayList<>();

    //色坐标
    private List<Float> daXs=new ArrayList<>();
    private List<Float> daYs=new ArrayList<>();
    private List<Float> daZs=new ArrayList<>();

    float[] mGray = new float[numberOfPoints]; //二维数组，线的数量和点的数量

    private boolean hasAxes = true;       //是否有轴，x和y轴
    private boolean hasAxesNames = false;   //是否有轴的名字
    private boolean hasLines = true;       //是否有线（点和点连接的线）
    private boolean hasPoints = true;       //是否有点（每个值的点）
    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
    private boolean isFilled = false;                //是否是填充
    private boolean hasLabels = false;               //每个点是否有名字
    private boolean isCubic = true;                 //是否是立方的，线条是直线还是弧线
    private boolean hasLabelForSelected = true;       //每个点是否可以选择（点击效果）
    private boolean pointsHaveDifferentColor=false;           //线条的颜色变换
    private boolean hasGradientToTransparent = false;      //是否有梯度的透明
    private boolean isInteractive=true;//设置图标是否可以交互
    private boolean isZoomEnabled=true;//手势缩放

    private ImageView mImageView;
    private Button mBtn_capture;
    private Button mBtn_album;
    private Button mBtn_source;
    private Button mBtn_absorb;
    private Button mBtn_save;
    private Button mBtn_queding;
    private Button mBtn_colorXY;
    private EditText mEdtChaZhi;
    private TextView mColorX;
    private TextView mColorY;
    private TextView mSeWen;

    private Uri imgUri;
    private Bitmap bitmap = null;
    int bochangcha = 0;

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
        mBtn_queding=findViewById(R.id.btn_queding);
        mBtn_colorXY=findViewById(R.id.btn_colorXY);
        mEdtChaZhi=findViewById(R.id.edt_chazhi);
        mSeWen=findViewById(R.id.sewen);

        mBtn_capture.setOnClickListener(LineActivity.this);
//        mBtn_save.setOnClickListener(LineActivity.this);
//        mBtn_source.setOnClickListener(LineActivity.this);
        mBtn_album.setOnClickListener(LineActivity.this);
        mBtn_absorb.setOnClickListener(LineActivity.this);
        mBtn_queding.setOnClickListener(LineActivity.this);
        mBtn_colorXY.setOnClickListener(LineActivity.this);

        mColorX=findViewById(R.id.sezuobiaoX);
        mColorY=findViewById(R.id.sezuobiaoY);

        mImageView = findViewById(R.id.img_picture);
    }

    private void initData() {
        // Generate some random values.
//        generateValues();   //设置四条线的值数据
//        generateData();    //设置数据

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);
        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
        resetViewport(0,310,350,800);   //设置折线图的显示大小
    }

    private void initEvent() {
        chart.setOnValueTouchListener(new ValueTouchListener());

    }

    /**
     * 图像显示大小
     */
    private void resetViewport(float bottom,float top,float left,float right) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = bottom;
        v.top = top;//310
        v.left = left;//350
        v.right = right;//800
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
        Log.e(TAG, "jianju: "+jianju);
        for (int k = 0; k < width; k ++) {
            int y = height / 2;
            int x = k;
            Log.e("zbyzby", "onActivityResult: " + x);
            int color = bitmap.getPixel(x, y);
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
            Log.e(TAG, "generateValues: "+gray);
            grays.add(gray);
        }
        for (int j = 0; j < numberOfPoints&&j<grays.size(); ++j) {
            Log.e("zbyzby", "generateValues: "+grays.get(j)+"      "+grays.size());
            mGray[j] = grays.get(j);
            Log.e("zbyzby", "j: "+j);
        }
    }

    /**
     * 配置数据
     */
    private void generateData() {
        //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();
        //把数据设置到线条上面去
        jianju=1.58f;
//        for (int i = 0; i < numberOfLines; i++) {
//
//        }
        float k=378.42f;//初始点横坐标

        List<PointValue> values = new ArrayList();
//            values.add(new PointValue(380,30));
        for (int j = 1;j< mGray.length; j++) {
            Log.e(TAG, "length: "+mGray.length );
            k=k+jianju;
//            k=k+bochangcha;
//                k= (int) (k+bochangcha);
            PointValue pointValue = new PointValue(k, mGray[j]);
            Log.e(TAG, "generateDatazby: "+pointValue );
            values.add(pointValue);
        }

        imagePoints.add(values);

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[1]);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        if (pointsHaveDifferentColor) {
            line.setPointColor(ChartUtils.COLORS[(2) % ChartUtils.COLORS.length]);
        }
        lines.add(line);

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
    private void setColorLine(){
        float pingjunX=0;
        float pingjunY=0;
        float sumX=0;
        float sumY=0;
        float sumZ=0;
        float n=0;
        float cct=0;
        resetViewport(0,1,0,1);
        generateValues();
        getColorXY();
        //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();
        //把数据设置到线条上面去
        for (int i = 0; i < numberOfLines; ++i) {
            List<PointValue> values1 = new ArrayList();
            List<PointValue> values2 = new ArrayList();
            values2.add(new PointValue(0.649f,0.347f));
            values2.add(new PointValue(0.623f,0.37f));
            values2.add(new PointValue(0.597f,0.389f));
            values2.add(new PointValue(0.572f,0.402f));
            values2.add(new PointValue(0.549f,0.412f));
            values2.add(new PointValue(0.527f,0.417f));
            values2.add(new PointValue(0.506f,0.42f));
            values2.add(new PointValue(0.487f,0.419f));
            values2.add(new PointValue(0.47f,0.417f));
            values2.add(new PointValue(0.454f,0.414f));
            values2.add(new PointValue(0.439f,0.409f));
            values2.add(new PointValue(0.425f,0.404f));
            values2.add(new PointValue(0.413f,0.399f));
            values2.add(new PointValue(0.402f,0.393f));
            values2.add(new PointValue(0.392f,0.388f));
            values2.add(new PointValue(0.383f,0.382f));
            values2.add(new PointValue(0.374f,0.376f));
            values2.add(new PointValue(0.367f,0.371f));
            values2.add(new PointValue(0.36f,0.366f));
            values2.add(new PointValue(0.353f,0.361f));
            values2.add(new PointValue(0.347f,0.356f));
            values2.add(new PointValue(0.342f,0.351f));
            values2.add(new PointValue(0.337f,0.347f));
            values2.add(new PointValue(0.332f,0.343f));
            values2.add(new PointValue(0.328f,0.339f));
            values2.add(new PointValue(0.324f,0.335f));
            values2.add(new PointValue(0.321f,0.332f));
            values2.add(new PointValue(0.317f,0.328f));
            values2.add(new PointValue(0.315f,0.327f));
            values2.add(new PointValue(0.314f,0.325f));
            values2.add(new PointValue(0.311f,0.322f));
            values2.add(new PointValue(0.308f,0.319f));
            values2.add(new PointValue(0.306f,0.317f));
            values2.add(new PointValue(0.303f,0.317f));
            values2.add(new PointValue(0.301f,0.312f));
            values2.add(new PointValue(0.299f,0.309f));
            values2.add(new PointValue(0.297f,0.307f));
            values2.add(new PointValue(0.292f,0.301f));
            values2.add(new PointValue(0.289f,0.297f));
            values2.add(new PointValue(0.285f,0.293f));
            values2.add(new PointValue(0.282f,0.29f));
//            values.add(new PointValue(380,30));
            for (int j = 1;j< daXs.size(); j++) {
//                PointValue pointValue = new PointValue(daXs.get(j), daYs.get(j));
                Log.e(TAG, "daXsize: "+daXs.size());
                Log.e(TAG, "daXs: "+daXs.get(j));
                Log.e(TAG, "daYs: "+daYs.get(j));
                sumX=sumX+daXs.get(j);
                sumY=sumY+daYs.get(j);
                sumZ=sumZ+daZs.get(j);
                Log.e(TAG, "sumX: "+sumX);
                Log.e(TAG, "sumY: "+sumY);
//                Log.e(TAG, "generateData: "+pointValue );
//                values.add(pointValue);
            }
            pingjunX=sumX/(sumX+sumY+sumZ);
            pingjunY=sumY/(sumX+sumY+sumZ);
            mColorX.setText("色坐标X： "+String.valueOf(pingjunX));
            mColorY.setText("色坐标Y： "+String.valueOf(pingjunY));
            PointValue pointValue = new PointValue(pingjunX,pingjunY);
            values1.add(pointValue);
            Log.e(TAG, "pingjunX: "+pingjunX);
            Log.e(TAG, "pingjunY: "+pingjunY);

            n= (float) ((pingjunX-0.3320)/(0.1858-pingjunY));
            cct= (float) ((float) (437*Math.pow(n,3))+(3601*Math.pow(n,2))+(6831*n)+5517);
            mSeWen.setText(String.valueOf(cct));

            imagePoints.add(values1);

            Line line1 = new Line(values1);
            line1.setColor(ChartUtils.COLORS[i]);
            line1.setShape(shape);
            line1.setCubic(isCubic);
            line1.setFilled(isFilled);
            line1.setHasLabels(hasLabels);
            line1.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line1.setHasLines(hasLines);
            line1.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor) {
                line1.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            Line line2 = new Line(values2);
            line2.setColor(ChartUtils.COLORS[i+2]);
            line2.setShape(shape);
            line2.setCubic(isCubic);
            line2.setFilled(isFilled);
            line2.setHasLabels(hasLabels);
            line2.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line2.setHasLines(hasLines);
            line2.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor) {
                line1.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line2);
            lines.add(line1);
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
            case R.id.btn_colorXY:
                openAlbumColor();
                break;
            case R.id.btn_queding:
                int i1 = Integer.parseInt(mEdtChaZhi.getText().toString());
                bochangcha=i1/76;
                generateData();
                break;
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
    private void openAlbumColor() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO_FROMCOLOR);
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
                resetViewport(0,310,350,800);
                break;
            case CHOOSE_PHOTO_FROMCOLOR:
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                    setColorLine();
                } else {
                    handleImageBeforeKitKat(data);
                    setColorLine();
                }
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
    public void getColorXY(){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        List<Double>colorRGB=new ArrayList();
        double r=0;
        double g=0;
        double b=0;
        float x=0;
        float y=0;
        float z=0;
        float daX=0;
        float daY=0;
        for (int k = 0; k < 80; k++) {
            int yZou = height / 2;
            int xZou = k;
            int color = bitmap.getPixel(xZou, yZou);
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            Log.e(TAG, "mGraySize: "+mGray.length );
            Log.e(TAG, "mGrayz: "+mGray[k] );
            x= (float) ((-1.455+(0.051*mGray[k]))-(0.00025*Math.pow(mGray[k],2))*xRmd[k]*5);//A=-1.455+0.051B-0.00025B²
            y= (float) ((-1.455+(0.051*mGray[k]))-(0.00025*Math.pow(mGray[k],2))*yRmd[k]*5);
            z=(float) ((-1.455+(0.051*mGray[k]))-(0.00025*Math.pow(mGray[k],2))*zRmd[k]*5);
            daXs.add(x);
            daYs.add(y);
            daZs.add(z);
        }
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
