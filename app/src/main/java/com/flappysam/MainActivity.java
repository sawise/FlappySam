package com.flappysam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity implements AlertDialog.OnDismissListener,View.OnClickListener, View.OnTouchListener {
    Obstacle obstacle;
    private RelativeLayout rl;
    private Button startButton;
    private RelativeLayout mainMenu;
    private TextView scoreText;
    private ImageView flappySam;
    private ImageView movingbg;
    private ImageView obstacleIV;
    private ImageView obstacleIV1;
    private int lineid = 1;
    private int position = 99;
    private int backgroundposition = 0;
    private int random,random1 = 0;
    private int width = 0;
    private int obstaclewidth = 0;
    private int obstacleheight = 0;
    private boolean gameOn = false;
    private boolean isFlapping = false;
    private float startflapposition = 0;
    private int flappySamposition = 0;
    private int flappingheight = 50;
    private int rotation = 0;
    private int FPS = 8;
    private int margin = 130;
    private int score = 0;
    private AlertDialog alertDialog;
    private boolean togglebg = true;
    private int mBGFarMoveX = 0;
    private int mBGNearMoveX = 0;
    Bitmap dbm, bm;
    Random r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl = (RelativeLayout) findViewById(R.id.mainlayout);
        flappySam = (ImageView) findViewById(R.id.flappySam);
        movingbg = (ImageView) findViewById(R.id.movingbg);
        startButton = (Button) findViewById(R.id.buttonStart);
        mainMenu = (RelativeLayout) findViewById(R.id.menuContainer);
        scoreText = (TextView) findViewById(R.id.score);
        Bitmap mIcon1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.movingbg);
        Canvas canvas = new Canvas(mIcon1);
        doDrawRunning(canvas);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setOnDismissListener(this);
        r = new Random();

        addObstacle();
        rl.setOnTouchListener(this);
        startButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(view == rl && gameOn) {
            // get pointer index from the event object
            int pointerIndex = motionEvent.getActionIndex();

            // get pointer ID
            int pointerId = motionEvent.getPointerId(pointerIndex);
            int maskedAction = motionEvent.getActionMasked();
            switch (maskedAction)
            //switch(motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN :
                {
                    flapSam();
                }
                break;
            }
        }

        return true;
    }

    public void moveBackground(){
        backgroundposition = (int)movingbg.getX();
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                do {
                    try {
                        Thread.sleep(FPS);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    /*handler.post(new Runnable() {
                        public void run() {
                            movingbg.setX(backgroundposition);
                            backgroundposition++;
                        }
                    });*/
                } while (togglebg);
            }
        };
        new Thread(runnable).start();
    }

    public void moveObstacleIV(){
        width = rl.getWidth();

        position = width;
        random = randomWithRange(200,obstacleIV1.getHeight());//= r.nextInt(obstacleIV.getHeight() - 100);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                do {
                    try {
                        Thread.sleep(FPS);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        public void run() {

                            obstacleIV.setVisibility(View.VISIBLE);
                            obstacleIV1.setVisibility(View.VISIBLE);
                            collisionDetection();
                            obstacleIV.setX(position);
                            obstacleIV.setY(random);
                            obstacleIV1.setX(position);
                            obstacleIV1.setY(obstacleIV.getY()-obstacleIV.getHeight()-margin);
                            if(obstacleIV.getX() <= 0-obstacleIV.getWidth()){
                                position = width;

                                random = randomWithRange(200, obstacleIV.getHeight());
                                obstacleIV.setX(position);
                                obstacleIV.setY(random);
                                obstacleIV1.setX(position);
                                obstacleIV1.setY(obstacleIV.getY()-obstacleIV.getHeight()-margin);
                                score++;
                                scoreText.setText("Score: "+Integer.toString(score));
                                setSpeed();
                            }
                            Log.i("obstacle height"," "+obstacleIV.getHeight());
                            Log.i("obstacle position","random "+random+"-> random1 "+(obstacleIV.getY()-obstacleIV.getHeight()-margin));
                            position--;
                        }
                    });
                } while (position >= (0-obstacleIV.getWidth()) && gameOn);
            }
        };
        new Thread(runnable).start();
    }

    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        Log.i("obstacle random",min+" "+max+" --> "+range);
        Log.i("obstacle random",""+((Math.random() * range) + min));
        return (int)(Math.random() * range) + min;
    }

    public void collisionDetection(){


        float compareX = obstacleIV.getX()-flappySam.getX();
        float compareY = obstacleIV.getY()-flappySam.getY();
        float compareX1 = obstacleIV1.getX()-flappySam.getX();
        float compareY1 = (obstacleIV1.getY()-flappySam.getY())+obstacleIV1.getHeight();


        if(compareY <= flappySam.getWidth() && (compareX < (flappySam.getWidth()/2) && compareX > (-obstacleIV1.getWidth()))){
            gameOver();
        }
        if(compareY1 > 0 && (compareX1 < (flappySam.getWidth()/2) && compareX1 > (-obstacleIV1.getWidth()))){
            gameOver();
        }
    }

    public void flapSam(){
        startflapposition = flappySamposition;
        isFlapping = true;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                do {
                    try {
                        Thread.sleep(FPS/2);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            if(isFlapping){
                                flappySam.setY(flappySamposition);
                                flappySam.setRotation(rotation);
                                Log.i("position flapping", flappySamposition+"-->"+(startflapposition-flappingheight));
                                if(!(flappySamposition >= (startflapposition-flappingheight))){
                                    unflapSam();
                                }
                                flappySamposition--;
                                rotation++;
                            }
                        }
                    });
                } while (isFlapping);//)flappySamposition >= (startflapposition-flappingheight));
            }
        };
        new Thread(runnable).start();
    }

    public void unflapSam(){
        isFlapping = false;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                do {
                    try {
                        Thread.sleep(4);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        public void run() {
                            if(!isFlapping){
                                Log.i("position unflapping", flappySamposition+"-->"+(startflapposition+flappingheight));
                                flappySam.setY(flappySamposition);
                                flappySam.setRotation(rotation);
                                flappySamposition++;
                                rotation--;
                                if(flappySamposition == (rl.getHeight()-flappySam.getHeight())){
                                    gameOver();
                                }
                            }

                        }
                    });
                } while (!isFlapping && gameOn);
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSpeed(){
        if((score > 5 && FPS != 7) || (score > 10 && FPS != 6)){
            FPS--;
        }
    }




    @Override
    public void onClick(View view) {
        if(view == startButton){
            if(!gameOn){
                startGame();
            }
        }
    }

    public void startGame(){

        gameOn = true;moveBackground();
        final Matrix matrix = new Matrix();
        matrix.postScale(2, 2);
        matrix.postTranslate(45, 0);
        mainMenu.setVisibility(View.GONE);
        flappySam.setVisibility(View.VISIBLE);
        flappySamposition = rl.getHeight()/2;
        flappySam.setY(flappySamposition);
        margin = flappySam.getWidth()*2;
        obstacleIV1.setX(rl.getWidth());
        obstacleIV.setX(rl.getWidth());
        moveObstacleIV();
        flapSam();
    }

    private void doDrawRunning(Canvas canvas) {
        // decrement the far background
        mBGFarMoveX = mBGFarMoveX - 1;
        // decrement the near background
        mBGNearMoveX = mBGNearMoveX - 4;
        // calculate the wrap factor for matching image draw
        int newFarX = movingbg.getWidth() - (-mBGFarMoveX);
        // if we have scrolled all the way, reset to start
        Bitmap mIcon1 = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.movingbg);
        if (newFarX <= 0) {
            mBGFarMoveX = 0;
            // only need one draw

            canvas.drawBitmap(mIcon1, mBGFarMoveX, 0, null);
        } else {
            // need to draw original and wrap
            canvas.drawBitmap(mIcon1, mBGFarMoveX, 0, null);
            canvas.drawBitmap(mIcon1, newFarX, 0, null);
        }
    }

    public void gameOver(){
        gameOn = false;
        alertDialog.setTitle("Game over");
        alertDialog.setMessage(scoreText.getText().toString());
        alertDialog.show();
        obstacleIV1.setX(rl.getWidth());
        obstacleIV.setX(rl.getWidth());


    }

    public void addObstacle(){

        String buttonidstr = lineid+"1";
        int idint = Integer.parseInt(buttonidstr);
        int idd = (idint);
        obstacleIV = new ImageView(this);
        obstacleIV1 = new ImageView(this);
        obstacleIV.setImageResource(R.drawable.obstacle);
        obstacleIV1.setImageResource(R.drawable.obstacle);

        rl.addView(obstacleIV, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addView(obstacleIV1, ViewGroup.LayoutParams.WRAP_CONTENT);
        obstacleIV1.setX(rl.getWidth());
        obstacleIV.setX(rl.getWidth());

        obstacleIV.setVisibility(View.GONE);
        obstacleIV1.setVisibility(View.GONE);
        lineid++;
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if(dialogInterface == alertDialog){
            mainMenu.setVisibility(View.VISIBLE);
            score = 0;
            scoreText.setText("Score: " + Integer.toString(score));
            obstacleIV.setVisibility(View.GONE);
            obstacleIV1.setVisibility(View.GONE);
            flappySam.setVisibility(View.GONE);
        }
    }
}
