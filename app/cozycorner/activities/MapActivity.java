package com.example.cozycorner.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.cozycorner.R;
import com.example.cozycorner.model.Post;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Objects;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("f84366ff-048c-48f3-9579-efb1011c5ea9");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map);
        Intent intent=getIntent();
        ArrayList<Post> posts= (ArrayList<Post>) intent.getSerializableExtra("posts");
        mapView = findViewById(R.id.mapView);
        mapView.getMapWindow().getMap().move(
                new CameraPosition(
                        new Point(59.923550, 30.347839),
                        /* zoom = */ 10.0f,
                        /* azimuth = */ 0.0f,
                        /* tilt = */ 0.0f
                )
        );
        //CameraListener cameraListener= (map, cameraPosition, cameraUpdateReason, b) -> map.move(cameraPosition);
        //mapView.getMapWindow().getMap().addCameraListener(cameraListener);
        Bitmap bitmap= getBitmap(R.drawable.ic_pin);
        ImageProvider imageProvider=ImageProvider.fromBitmap(bitmap);
        for (Post post:posts) {
            PlacemarkMapObject placemarkMapObject = mapView.getMapWindow().getMap().getMapObjects().addPlacemark();
            String[] mas=post.getCoords().split(", ");
            placemarkMapObject.setGeometry(new Point(Float.parseFloat(mas[0]), Float.parseFloat(mas[1])));
            placemarkMapObject.setIcon(imageProvider);
            String s=post.getType()+"\n"+post.getCost();
            placemarkMapObject.setUserData(s);
            placemarkMapObject.addTapListener(mapObjectTapListener);
        }
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }
    private final MapObjectTapListener mapObjectTapListener = (mapObject, point) -> {
        Dialog dialog=new Dialog(MapActivity.this);
        dialog.setContentView(R.layout.dialog_content);
        dialog.setTitle(point.getLatitude()+", "+point.getLongitude());
        String[] mas=((String) Objects.requireNonNull(mapObject.getUserData())).split("\n");
        TextView main=dialog.findViewById(R.id.dialogMain);
        main.setText(mas[1]);
        TextView sub=dialog.findViewById(R.id.dialogSubContent);
        sub.setText(mas[0]);
        dialog.show();
        return true;
    };
    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
}