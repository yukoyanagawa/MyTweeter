package com.example.newspicks01.mytweeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


/**
 * Created by newspicks01 on 15/06/16.
 */
public class StatusActivity  extends Activity implements Serializable {

    private Twitter mTwitter;
    private Status status;
    private FriendAdapter fAdapter;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        Intent intent = getIntent();
        status = (twitter4j.Status) getIntent().getSerializableExtra("status");
        mTwitter = TwitterUtils.getTwitterInstance(this);

        fAdapter = new FriendAdapter(this);
        listView = (ListView) findViewById(R.id.friendlist);
        listView.setAdapter(fAdapter);

        profile();
        reloadTimeLine();


    }

    private void reloadTimeLine() {
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            int flag = -1;
            long Uid = 0L;
            int naga;
            int naga2;


            @Override
            protected List<twitter4j.User> doInBackground(Void... params) {
                try {

                    System.out.println("****** reloadTimeLine");
                    //フォロワー一覧を取得
                    IDs followerIds = mTwitter.getFollowersIDs(status.getUser().getId(), -1L);
                    IDs followIds = mTwitter.getFriendsIDs(status.getUser().getId(), -1L);

//                    List followerid = Arrays.asList((followerIds.getIDs()));
//                    List followid = Arrays.asList((followIds.getIDs()));
                    long[] followerid= followerIds.getIDs();
                    long[] followid = followIds.getIDs();
                    naga = followerid.length;
//                    naga=followerid.size();
//                    long[] LimitId;
//                    if (naga > 50) {
//                        LimitId = new long[50];
//                    } else {
//                        LimitId = new long[followerid.length];
//                    }
//                    naga2 = LimitId.length;
//                    for (int i = 0; i < LimitId.length; i++) {
//                        LimitId[i] = followerid[i];
//                    }
                    List<User> UsersID=new ArrayList<User>();

                    int size=0;
                    if(followerid.length>followid.length){size=followid.length;}
                    else{size=followerid.length;}
                    for(int i=0;i<followerid.length;i++){
                        for(int j=0;j<followid.length;j++){
                            if(followerid[i]==followid[j]){
                                //idが重複しているものをリストに代入
                                UsersID.add(mTwitter.showUser(followid[j]));
                            }
                        }
                    }
                    return UsersID;
                } catch (TwitterException e) {
                    Log.e(getClass().getSimpleName(), e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                if (result != null) {
                    // 表示データのクリア
                    fAdapter.clear();
                    // 表示データの設定
                    for (User id : result) {
                       fAdapter.add(id);

                    }
                    listView.setSelection(0);
                    showToast(String.valueOf("naga:" + naga));
                    showToast(String.valueOf("naga2:"+naga2));
                    showToast(String.valueOf("result:"+result.size()));
                } else {
//                      showToast("フレンドリストの取得に失敗しました。");
                    showToast(String.valueOf(naga));
                    showToast(String.valueOf(naga2));
                }
            }
        };
        task.execute();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    private void profile() {
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(status.getUser().getName());
        TextView screenName = (TextView) findViewById(R.id.screen_name);
        screenName.setText("@" + status.getUser().getScreenName());
        SmartImageView icon = (SmartImageView) findViewById(R.id.icon);
        icon.setImageUrl(status.getUser().getProfileImageURL());
    }

    private class FriendAdapter extends ArrayAdapter<twitter4j.User> {

        private LayoutInflater mInflater;

        public FriendAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_status, null);//templateかな？
            }

            User item = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getName());
            TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getScreenName());
/*
            try {
                System.out.println("****** FriendAdapter");

                User uses_id = mTwitter.showUser(item.getId());
                SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
                icon.setImageUrl(uses_id.getProfileImageURL());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
*/

            return convertView;
        }
    }

}