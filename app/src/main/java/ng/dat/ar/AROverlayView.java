package ng.dat.ar;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ng.dat.ar.helper.LocationHelper;
import ng.dat.ar.model.ARPoint;
import ng.dat.ar.model.FirebasePoint;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View {

    Context context;
    Activity thisActivity;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;
    private List<ARPoint> arPointsFiltered;
    private Object [][] pointsXY;

    //FireBase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference sitesRef = database.getReference("Sites");

    //Para dibujar canvas
    private String locationStr, timeStr, distanceeStr;
    //private Float distanceFloat;
    private final float DEFAULT_SCREEN_WIDTH = 1440;
    private final float DEFAULT_SCREEN_HEIGHT = 2560;
    private float widthDiff, heightDiff;
    float timeDistanceIconTop;
    float timeDistanceIconBottom;
    float paddingEnd;
    float timeDistanceTextHeight;
    float whiteBackgroundHeight;
    float lineHeight;
    float timeDistanceIconWidth;
    float directionIconWidth;
    float directionIconTop;
    float directionIconBottom;
    float timeIconStart;
    float timeIconEnd;
    float nameTextY;
    float stripWidth;
    float textWidth;
    private double latitude, longitude;
    Bitmap icon1;
    Paint paint,paint2, paint3, paint4, paint5, paint6;
    TextPaint textPaint;


    public AROverlayView(final Context context, final Location location) {
        super(context);

        this.context = context;
        this.thisActivity = (Activity) context;
        arPoints = new ArrayList<ARPoint>();
        String nameCity = "";

        Geocoder geocoder = new Geocoder(context);
        try {
            nameCity = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1).get(0).getLocality();
        } catch (IOException e) {
            Toast.makeText(context, "No se pudo conseguir la ciudad", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(context, "Obteniendo Puntos...", Toast.LENGTH_LONG).show();

        sitesRef.child(nameCity).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot element : dataSnapshot.getChildren()) {
                    FirebasePoint point = element.getValue(FirebasePoint.class);
                    arPoints.add(new ARPoint(element.getKey(), point.getDescripcion(), point.getLatitud(), point.getLongitud(), point.getAltitud()));
                }

                updateCurrentLocation(location);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        Point size = new Point();
        thisActivity.getWindowManager().getDefaultDisplay().getSize(size);

        final float width = size.x;
        float height = size.y;

        widthDiff = width / DEFAULT_SCREEN_WIDTH;
        heightDiff = height / DEFAULT_SCREEN_HEIGHT;

        //Calculos de Render
        timeDistanceIconTop = 190*heightDiff;
        timeDistanceIconBottom = 270*heightDiff;
        paddingEnd = 20*widthDiff;
        timeDistanceTextHeight = 250*heightDiff;
        whiteBackgroundHeight = 150*heightDiff;
        lineHeight = 170*heightDiff;
        timeDistanceIconWidth = 70*widthDiff;
        directionIconWidth = 180*widthDiff;
        directionIconTop = 100*heightDiff;
        directionIconBottom = 200*heightDiff;
        timeIconStart = 40*widthDiff;
        timeIconEnd = 110*widthDiff;
        nameTextY = 20*heightDiff;
        stripWidth = 10*widthDiff;
        textWidth = 600*widthDiff;

        icon1 =
                BitmapFactory.decodeResource(getContext().getResources(),
                        R.mipmap.ic_launcher);

        paint2 = new Paint();
        paint2.setColor(Color.parseColor("#8a8da6"));
        paint2.setTextSize(getResources().getDimension(R.dimen.ar_text_size));
        paint2.setTypeface(Typeface.DEFAULT_BOLD);

        paint3 = new Paint();
        paint3.setColor(Color.WHITE);
        paint3.setStrokeWidth(300*widthDiff);

        paint4 = new Paint();
        paint4.setColor(Color.parseColor("#4c4c4c"));

        paint6 = new Paint();
        paint6.setColor(Color.parseColor("#f17f0e"));
        paint6.setStrokeWidth(300*widthDiff);

        textPaint = new TextPaint();
        textPaint.setTextSize(getResources().getDimension(R.dimen.ar_text_size));
        textPaint.setColor(getResources().getColor(R.color.colorPrimary));
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        arPointsFiltered = new ArrayList<ARPoint>();
        for (ARPoint point: arPoints) {
            if( (Math.abs(currentLocation.getLongitude()) - 0.0015 < Math.abs(point.getLocation().getLongitude())) && (Math.abs(currentLocation.getLongitude()) + 0.0015 > Math.abs(point.getLocation().getLongitude())) )
            {
                if( (Math.abs(currentLocation.getLatitude()) - 0.0015 < Math.abs(point.getLocation().getLatitude())) && (Math.abs(currentLocation.getLatitude()) + 0.0015 > Math.abs(point.getLocation().getLatitude())) )
                {
                    arPointsFiltered.add(point);
                }
            }
        }

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        pointsXY = new Object[arPointsFiltered.size()][3];
        //final int radius = 30;
        //Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.WHITE);
        //paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        //paint.setTextSize(60);

        for (int i = 0; i < arPointsFiltered.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPointsFiltered.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                pointsXY[i][0] = x;
                pointsXY[i][1] = y;
                pointsXY[i][2] = arPointsFiltered.get(i);

                float timeDistanceIconTop = y +this.timeDistanceIconTop;
                float timeDistanceIconBottom = y +this.timeDistanceIconBottom;
                float paddingEnd = this.paddingEnd;
                float timeDistanceTextHeight = y +this.timeDistanceTextHeight;
                float whiteBackgroundHeight = y +this.whiteBackgroundHeight;
                float lineHeight = y+this.lineHeight;
                float timeDistanceIconWidth = this.timeDistanceIconWidth;
                float directionIconWidth = this.directionIconWidth;
                float directionIconTop = y + this.directionIconTop;
                float directionIconBottom = y + this.directionIconBottom;
                float nameTextY = y + this.nameTextY;
                float stripWidth = this.stripWidth;

                //white background
                canvas.drawLine(x - ((4*paddingEnd) + 100
                                +timeDistanceIconWidth),whiteBackgroundHeight,
                        x + (4*paddingEnd + 100
                                + timeDistanceIconWidth), whiteBackgroundHeight, paint3);

                //orange line
                canvas.drawLine(x - ((4*paddingEnd) + 100 + timeDistanceIconWidth+stripWidth),whiteBackgroundHeight,
                        x - ((4*paddingEnd) + 100 + timeDistanceIconWidth), whiteBackgroundHeight, paint6);

                //line
                canvas.drawLine(x - ((4*paddingEnd) + 100 + timeDistanceIconWidth),lineHeight,
                        x + ((4*paddingEnd) + 100  + timeDistanceIconWidth), lineHeight, paint4);

                StaticLayout sl = new StaticLayout(((ARPoint)pointsXY[i][2]).getName(), textPaint,
                        (int)textWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.0f, 0.0f, false);
                canvas.save();
                canvas.translate(x - ((3*paddingEnd) + 100  + timeDistanceIconWidth), nameTextY);
                sl.draw(canvas);
                canvas.restore();

                //3rd - distance icon
                canvas.drawText("40 mts",x - (paddingEnd), timeDistanceTextHeight, paint2);

                //4th - distance text
                canvas.drawBitmap(icon1, null, new RectF(x - ((2*paddingEnd)  + 100 ),
                        timeDistanceIconTop,x - (((2*paddingEnd)  + 100 ) +timeDistanceIconWidth), timeDistanceIconBottom), null);

                //canvas.drawCircle(x, y, radius, paint);
                //canvas.drawText(arPointsFiltered.get(i).getName(), x - (30 * arPointsFiltered.get(i).getName().length() / 2), y - 80, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        for(int i = 0; i < pointsXY.length; i++){
            if(pointsXY[i][0] != null && pointsXY[i][1] != null) {
                float xPoint = (float) pointsXY[i][0];
                float yPoint = (float) pointsXY[i][1];
                if(x > xPoint - 150 && x < xPoint + 150){
                    if(y > yPoint - 150 && y < yPoint + 150){
                        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        InfoPointFragment fragment = InfoPointFragment.newInstance(((ARPoint)pointsXY[i][2]).getName(), ((ARPoint)pointsXY[i][2]).getDescription());
                        fragmentTransaction.add(R.id.camera_container_layout, fragment);
                        fragmentTransaction.commit();
                        break;
                    }
                }
            }
        }
        return false;
    }

}