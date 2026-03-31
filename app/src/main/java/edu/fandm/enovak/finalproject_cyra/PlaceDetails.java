package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;

public class PlaceDetails extends AppCompatActivity {

    private ImageView placeImage;
    private TextView placeName;
    private TextView placeDescription;
    private Button btnReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details3);

        placeImage = findViewById(R.id.image_place);
        placeName = findViewById(R.id.text_place_name);
        placeDescription = findViewById(R.id.text_place_description);
        btnReview = findViewById(R.id.btn_review);
        placeName.setText("Central Park");
        placeDescription.setText("An iconic urban park in New York City. This text will dynamically update once connected to the API.");


//        String imageUrl = "https://images.unsplash.com/photo-1510257323136-22797e88fb98";
//        Glide.with(this)
//                .load(imageUrl)
//                .into(placeImage);


        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlaceDetails.this, "Opening Review Page...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
