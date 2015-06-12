package com.example.newspicks01.mytweeter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class MainActivity extends ListActivity {
    private TweetAdapter mAdapter;
    private Twitter mTwitter;

    int callcount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
            /*このfinish()で一旦MainActivityが終了し、TwitterOAuthActivityに遷移する。
            Twitter認証後、再びMainctivityが起動し、OnCreateが呼ばれる。
             その時はAccsessTokenを所持しているので、else処理になる。*/
        } else {
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);

            mTwitter = TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }
    }

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {



            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    Log.d("callcount=", String.valueOf(callcount++));
                    return mTwitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    mAdapter.clear();
                    for (twitter4j.Status status : result) {
                        mAdapter.add(status);
                    }
                    getListView().setSelection(0);
                } else {
                    showToast("タイムラインの取得に失敗しました。。。");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {

        private LayoutInflater mInflater;

        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_tweet, null);//templateかな？
            }
            Status item = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText(item.getText());
            SmartImageView icon=(SmartImageView)convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getProfileImageURL());
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                reloadTimeLine();
                return true;
            case R.id.menu_tweet:
                Intent intent = new Intent(this, TweetActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
