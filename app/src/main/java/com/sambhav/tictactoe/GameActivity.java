package com.sambhav.tictactoe;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class GameActivity extends AppCompatActivity {
    int winner;
    String p1, p2;
    MediaPlayer player;
    Vibrator vibe;
    boolean gameActive = true;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    private AdView mAdView;
    // Player representation
    // 0 - X
    // 1 - O
    int activePlayer = 0;
    int count = 0;
    int[] gameState = {2, 2 , 2, 2, 2, 2, 2, 2, 2};
    //    State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    int[][] winPositions = {{0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}};
    @SuppressLint("ResourceAsColor")
    public void playerTap(View view){
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());
        if(!gameActive){
            gameReset(view);
        }
        else if(gameActive && gameState[tappedImage] != 2){
            vibe.vibrate(100);
            TextView status = findViewById(R.id.status);
            status.setText("The place is already filled");
            status.setTextColor(Color.RED);
        }
        else if(gameActive && gameState[tappedImage] == 2) {
            vibe.vibrate(25);
            count++;
            gameState[tappedImage] = activePlayer;
            img.setTranslationY(-1000f);
            if (activePlayer == 0) {
                img.setImageResource(R.drawable.x);
                activePlayer = 1;
                TextView status = findViewById(R.id.status);
                status.setText(p2 + "'s Turn - Tap to play");
                status.setTextColor(Color.GRAY);
            } else {
                img.setImageResource(R.drawable.o);
                activePlayer = 0;
                TextView status = findViewById(R.id.status);
                status.setText(p1 + "'s Turn - Tap to play");
                status.setTextColor(Color.GRAY);
            }
            img.animate().translationYBy(1000f).setDuration(300);
        }
        // Check if any player has won
        for(int[] winPosition: winPositions){
            if(gameState[winPosition[0]] == gameState[winPosition[1]] &&
                    gameState[winPosition[1]] == gameState[winPosition[2]] &&
                    gameState[winPosition[0]]!=2){
                // Somebody has won! - Find out who!
                String winnerStr;
                gameActive = false;
                count=0;
                if(gameState[winPosition[0]] == 0){
                    winnerStr = p1 + " has won";
                    winner = 0;
                }
                else{
                    winnerStr = p2 + " has won";
                    winner = 1;
                }
                // Update the status bar for winner announcement
                TextView status = findViewById(R.id.status);
                status.setText(winnerStr);
                status.setTextColor(Color.RED);
                vibe.vibrate(100);
                TextView restart = findViewById(R.id.restart);
                restart.setText("Click on any box to Restart");
            }
            else if(count==9){
                vibe.vibrate(100);
                gameActive = false;
                count=0;
                TextView status = findViewById(R.id.status);
                status.setText("It's a Tie");
                status.setTextColor(Color.RED);
                TextView restart = findViewById(R.id.restart);
                restart.setText("Click on any box to Restart");
            }
        }
    }

    public void gameReset(View view) {
        gameActive = true;
        activePlayer = 0;
        for(int i=0; i<gameState.length; i++){
            gameState[i] = 2;
        }
        ((ImageView)findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView)findViewById(R.id.imageView8)).setImageResource(0);

        TextView status = findViewById(R.id.status);
        TextView restart = findViewById(R.id.restart);
        restart.setText("");
        if((!p1.equals("X")) || (!p2.equals("O"))){
            String temp = p1;
            p1 = p2;
            p2 = temp;
        }
        status.setText(p1 + "'s Turn - Tap to Play");
        status.setTextColor(Color.GRAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        player = MediaPlayer.create(this, R.raw.song);
        player.setLooping(true);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = getIntent();
        this.p1 = intent.getStringExtra(MainActivity.P1);
        this.p2 = intent.getStringExtra(MainActivity.P2);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(getBaseContext(), "Touch the back button again to go back", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }
}