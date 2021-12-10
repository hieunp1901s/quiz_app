package com.example.quiz.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.os.Bundle;

import com.example.quiz.R;
import com.example.quiz.models.Test;
import com.example.quiz.views.dialog.ProgressDialogFragment;
import com.example.quiz.viewmodels.FirebaseViewModel;
import com.example.quiz.viewmodels.QuestionViewModel;

public class MainActivity extends AppCompatActivity {
    DialogFragment dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Activity activity = this;
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        QuestionViewModel questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);

        questionViewModel.getTest().observe(this, new Observer<Test>() {
            @Override
            public void onChanged(Test test) {
                if (test != null)
                    Navigation.findNavController(activity, R.id.main_nav_host_fragment).navigate(R.id.action_homeFragment_to_questionFragment);
            }
        });

        firebaseViewModel.getProgressing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    dialog = new ProgressDialogFragment();
                    dialog.setCancelable(false);
                    dialog.show(getSupportFragmentManager(), "progress");
                }
                else if (!aBoolean) {
                    dialog.dismiss();
                }
            }
        });
    }
}