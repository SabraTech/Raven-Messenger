package com.example.space.chatapp.ui.activities;

/*
Sign uo with new layout
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.StaticConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {
   private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private FloatingActionButton fab;
    private CardView cardView;
    private EditText nameEditText, emailEditText, passwordEditText, repeatPasswordEditText;

   @Override
    protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_signup);
       fab = findViewById(R.id.fab);
       cardView = findViewById(R.id.cv_add);
       nameEditText = findViewById(R.id.et_name);
       emailEditText = findViewById(R.id.et_email);
       passwordEditText = findViewById(R.id.et_password);
       repeatPasswordEditText = findViewById(R.id.et_repeatpassword);
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           showEnterAnimation();
       }
       fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               animateRevealClose();
           }
       });
   }
   private void showEnterAnimation() {
       Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fab_transition);
       getWindow().setSharedElementEnterTransition(transition);
       transition.addListener(new Transition.TransitionListener() {
           @Override
           public void onTransitionStart(Transition transition) {
               cardView.setVisibility(View.GONE);
           }

           @Override
           public void onTransitionEnd(Transition transition) {
               transition.removeListener(this);
               animateRevealShow();
           }

           @Override
           public void onTransitionCancel(Transition transition) {

           }

           @Override
           public void onTransitionPause(Transition transition) {

           }

           @Override
           public void onTransitionResume(Transition transition) {

           }


       });
   }
       public void animateRevealShow() {
           Animator mAnimator = ViewAnimationUtils.createCircularReveal(cardView, cardView.getWidth()/2,0, fab.getWidth() / 2,
                   cardView.getHeight());
           mAnimator.setDuration(500);
           mAnimator.setInterpolator(new AccelerateInterpolator());
           mAnimator.addListener(new AnimatorListenerAdapter() {
               @Override
               public void onAnimationEnd(Animator animation) {
                   super.onAnimationEnd(animation);
               }

               @Override
               public void onAnimationStart(Animator animation) {
                   cardView.setVisibility(View.VISIBLE);
                   super.onAnimationStart(animation);
               }
           });
           mAnimator.start();
       }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cardView,cardView.getWidth()/2,0,
                cardView.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardView.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                SignUpActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

        public void clickRegister(View view) {
            String nameString = nameEditText.getText().toString();
            String emailString = emailEditText.getText().toString();
            String passwordString = passwordEditText.getText().toString();
            String repeatPasswordString = repeatPasswordEditText.getText().toString();
            if(validate(emailString, passwordString, repeatPasswordString)){
                Intent data = new Intent();
                data.putExtra(StaticConfig.STR_EXTRA_NAME, nameString);
                data.putExtra(StaticConfig.STR_EXTRA_USERNAME, emailString);
                data.putExtra(StaticConfig.STR_EXTRA_PASSWORD, passwordString);
                data.putExtra(StaticConfig.STR_EXTRA_ACTION, "register");
                setResult(RESULT_OK, data);
                finish();
            }else {
                Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
            }
        }



    /**
     * Validate email, pass == re_pass
     * @param emailStr
     * @param password
     * @return
     */
    private boolean validate(String emailStr, String password, String repeatPassword) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return password.length() > 6 && repeatPassword.equals(password) && matcher.find();
    }


}





