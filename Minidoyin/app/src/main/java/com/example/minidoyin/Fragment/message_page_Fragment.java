package com.example.minidoyin.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minidoyin.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.example.minidoyin.chatroom;
/**
 * A simple {@link Fragment} subclass.
 */
public class message_page_Fragment extends Fragment {

    private List<message> messagess=null;
    private ListView listView;
    public message_page_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_page_, container, false);
        listView = view.findViewById(R.id.listview_message);

        try {
            messagess =pull(getResources().getAssets().open("data.xml"));
            //listview
            setListview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
    private ListViewBaseAdapter listViewBaseAdapte;
    public void setListview(){
        listViewBaseAdapte =new ListViewBaseAdapter(getContext(),messagess.size());
        listView.setAdapter(listViewBaseAdapte);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),chatroom.class);
                startActivity(intent);
            }
        });

    }

    public class ListViewBaseAdapter extends BaseAdapter {
        private Context mContext;
        private int count=0;
        public ListViewBaseAdapter(Context context,int count) {
            mContext = context;
            this.count=count;
        }
        @Override public int getCount() {
            return 30;
        }
        @Override public Object getItem(int i) {
            return null;
        }

        @Override public long getItemId(int i) {
            return 0;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.im_list_item,null);
            } else {
                view = (View) convertView;
            }
            TextView title=view.findViewById(R.id.tv_title);
            TextView describe=view.findViewById(R.id.tv_description);
            TextView time=view.findViewById(R.id.tv_time);
            ImageView imageView=view.findViewById(R.id.robot_notice);

            title.setText(messagess.get(position).getTitle());
            describe.setText(messagess.get(position).getHashtag());
            time.setText(messagess.get(position).getTime());
            if (messagess.get(position).getIcon().equals("TYPE_ROBOT")) {
                imageView.setVisibility(View.VISIBLE);
            } else if (messagess.get(position).getIcon().equals("TYPE_GAME")) {
                imageView.setVisibility(View.VISIBLE);
            } else if (messagess.get(position).getIcon().equals("TYPE_SYSTEM")) {
                imageView.setVisibility(View.VISIBLE);
            }
            else{
                imageView.setVisibility(View.INVISIBLE);
            }


            return view;
        }
    }

    //消息数据格式
    public class message {
        private String title;
        private String hashtag;
        private String time;
        private String icon;
        public void setTitle(String  title){
            this.title=title;
        }
        public void setHashtag(String hashtag  ){
            this.hashtag=hashtag;
        }
        public void setTime(String  time){
            this.time=time;
        }
        public void setIcon(String  icon){
            this.icon=icon;
        }

        public String getTitle(){
            return title;
        }
        public String getHashtag(){
            return hashtag;
        }
        public String getTime(){
            return time;
        }
        public String getIcon(){
            return icon;
        }
    }
    //消息解析
    public List<message> pull(InputStream is) throws Exception{
        List<message> list=new ArrayList<>();
        message message=null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is,"utf-8");
        int type = parser.getEventType();

        while(type!=XmlPullParser.END_DOCUMENT){
            switch (type){
                case XmlPullParser.START_TAG:{
                    if("message".equals(parser.getName())){
                        message=new message();
                    }else if("title".equals(parser.getName())){
                        message.setTitle(parser.nextText());
                    }else if("hashtag".equals(parser.getName())){
                        message.setHashtag(parser.nextText());
                    }else if("time".equals(parser.getName())){
                        message.setTime(parser.nextText());
                    }else if("icon".equals(parser.getName())) {
                        message.setIcon(parser.nextText());
                    }
                    break;
                }
                case XmlPullParser.END_TAG:{
                    if("message".equals(parser.getName())){
                        list.add(message);
                    }
                    break;
                }
            }
            type=parser.next();
        }

        return list;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
