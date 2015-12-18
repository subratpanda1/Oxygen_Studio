package com.subrat.Oxygen.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.subrat.Oxygen.R;
import com.subrat.Oxygen.graphics.HadaGraphicsEngine;
import com.subrat.Oxygen.physics.LiquidFunEngine;
import com.subrat.Oxygen.backendRoutines.UpdateObjectsInAThread;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.physics.PhysicsManager;
import com.subrat.Oxygen.utilities.Configuration;

/**
 * Created by subrat.panda on 07/05/15.
 */
public class OxygenActivity extends Activity {
    private static Context context = null;
    private static int canvasWidth = 0;    // In pixels
    private static int canvasHeight = 0;   // In pixels
	private static float worldWidth = 0;   // In meter
	private static float worldHeight = 0F; // In meter
	
    private int alertSecondsCounter = 0;
    Runnable runnable;
    OxygenView oxygenView;

    UpdateObjectsInAThread updateObjectsInAThread = null;

    Button.OnClickListener onClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oxygen);
        oxygenView = (OxygenView) findViewById(R.id.view);
        oxygenView.oxygenActivity = this;

        try {
        	Thread.sleep(3000);
        } catch (Exception ex) {
        	
        }

        context = this;

        if (updateObjectsInAThread == null) {
            updateObjectsInAThread = new UpdateObjectsInAThread(this, oxygenView);
        }
        
        onClickListener = new Button.OnClickListener() {
            public void onClick(View view) {
                PhysicsManager.getPhysicsManager().addWater();
            }
        };
        
        Button button = (Button) findViewById(R.id.waterButton);
        button.setOnClickListener(onClickListener);
        if (!Configuration.USE_LIQUIDFUN_PHYSICS) {
        	button.setVisibility(View.GONE);
        }
        
        startSimulation();
    }

    public static Context getContext() { return context; }
    
    public void stopSimulation() {
        updateObjectsInAThread.stopThread();
    }

    public void startSimulation() {
        updateObjectsInAThread.startThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HadaGraphicsEngine.getHadaGraphicsEngine().getObjectList().clear();
        HadaGraphicsEngine.getHadaGraphicsEngine().getParticleList().clear();
        context = null;

        PhysicsManager.getPhysicsManager().clearWorld();

        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSimulation();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        oxygenView.invalidate();
        alertSecondsCounter = 6;
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
        alertDialog.show();

        final Handler handler  = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (alertSecondsCounter == 0) {
                    alertDialog.cancel();
                    startSimulation();
                } else {
                    alertDialog.setMessage("Resuming simulation in " + alertSecondsCounter-- + " seconds");
                    handler.postDelayed(runnable, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }
    
    public static int getCanvasWidth() { return canvasWidth; }
    public static int getCanvasHeight() { return canvasHeight; }
    public static float getWorldWidth() { return worldWidth; }
    public static float getWorldHeight() { return worldHeight; }
    
    public static void setCanvasDimensions(int width, int height) { 
    	canvasWidth = width;
    	canvasHeight = height;
    	worldHeight = Configuration.DEFAULT_WORLD_HEIGHT;
    	int pixelsPerMeter = (int)(canvasHeight / worldHeight);
    	worldWidth = (float)canvasWidth / (float)pixelsPerMeter;
    }
}

