package com.example.newspicks01.mytweeter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by newspicks01 on 15/06/11.
 */
public class TweetActivity extends FragmentActivity {

    private EditText mInputText;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        mTwitter = TwitterUtils.getTwitterInstance(this);

        mInputText = (EditText) findViewById(R.id.input_text);


        findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });

    }

    private void tweet(){
        AsyncTask<String, Void,Boolean> task= new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {//非同期で行う処理
                try {
                    mTwitter.updateStatus(params[0]);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

        @Override
        protected void onPostExecute(Boolean result){//doInBackgroundメソッドの実行後にメインスレッドで実行される
            if(result){
                showToast("ツイートが完了しました。");
                finish();
            }else{
                showToast("ツイートが失敗しました。");
            }
        }

        };
        task.execute(mInputText.getText().toString());
    }

    private void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }
}
