package com.example.realitycheck;

import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class ActivityRating implements Rating {
    Button button;
    RatingBar ratingStars;

    float myRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        button = findViewById(R.id.button);
        ratingStars = findViewById(R.id.ratingBar);

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int rating = (int) v;

                String message = null;

                myRating = ratingBar.getRating();

                switch (rating) {
                    case 1:
                        message = "Sounds Good!";
                        break;
                    case 2:
                        message = "Appreciate the insight!";
                        break;
                    case 3:
                        message = "Thank you for your input!";
                        break;
                    case 4:
                        message = "Thank you for your input!";
                        break;
                    case 5:
                        message = "Sorry to hear that!";
                        break;
                }

                Toast.makeText(ActivityRating.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityRating.this, String.valueOf(myRating), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setContentView(int activity_rating) {
    }
}
