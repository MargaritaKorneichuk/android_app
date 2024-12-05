package com.example.cozycorner.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cozycorner.Porter;
import com.example.cozycorner.R;
import com.example.cozycorner.fragments.LongTermFragment;
import com.example.cozycorner.fragments.ShortTermFragment;
import com.example.cozycorner.model.Post;
import com.example.cozycorner.utilities.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity {
    private List<Post> home_posts;
    private List<Post> short_list,long_list;
    //ListView posts;
    RadioButton short_term,long_term;
    FrameLayout frame;
    RadioGroup radioGroup;
    ShortTermFragment shortTermFragment;
    LongTermFragment longTermFragment;
    Button filter,clearFilter;
    LinearLayout filterLayout;
    FrameLayout send,mic;
    EditText filterText;
    FloatingActionButton mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageButton chats=findViewById(R.id.chats);
        ImageButton profile=findViewById(R.id.personal_info);
        //posts=findViewById(R.id.home_list_posts);
        frame=findViewById(R.id.home_frame);
        short_term=findViewById(R.id.short_term_radio_btn);
        long_term=findViewById(R.id.long_term_radio_btn);
        radioGroup=findViewById(R.id.radio_group_term);
        filter=findViewById(R.id.filter_btn);
        filterLayout=findViewById(R.id.filterLayout);
        filterText=findViewById(R.id.filterText);
        send=findViewById(R.id.layoutSend);
        mic=findViewById(R.id.layoutMic);
        clearFilter=findViewById(R.id.clearFilterBtn);
        home_posts=new ArrayList<>();
        short_list=new ArrayList<>();
        long_list=new ArrayList<>();
        shortTermFragment=ShortTermFragment.newInstance(short_list);
        longTermFragment=LongTermFragment.newInstance(long_list);
        mapBtn=findViewById(R.id.map_btn);
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PersonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
            }
        });
        load_posts();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case -1:
                    case R.id.short_term_radio_btn:
                        setNewFragment(shortTermFragment);
                        break;
                    case R.id.long_term_radio_btn:
                        setNewFragment(longTermFragment);
                    default:
                        break;
                }
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterLayout.getVisibility()==View.VISIBLE){
                    filterLayout.setVisibility(View.GONE);
                    filter.setText("Открыть фильтер");
                }else{
                    filterLayout.setVisibility(View.VISIBLE);
                    filter.setText("Скрыть фильтер");
                }
            }
        });
        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (s.equals("")){
                //    send.setVisibility(View.GONE);
                //    mic.setVisibility(View.VISIBLE);
                //}else{
                    send.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.GONE);
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(filterText.getText().toString().equals("")){
                    send.setVisibility(View.GONE);
                    mic.setVisibility(View.VISIBLE);
                }else{
                    send.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.GONE);
                }
            }
        });
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filterLayout.setVisibility(View.GONE);
                filterText.setText("");
                //filter.setText("Открыть фильтер");
                filterText.setHint("Введите свои пожелания");
                sepList();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter(filterText.getText().toString());
            }
        });
        checkPermission();
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //displaying the first match
                if (matches != null)
                    filterText.setText(matches.get(0));
                filter(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        findViewById(R.id.layoutMic).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        break;
                }
                return false;
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,MapActivity.class);
                ArrayList<Post> map_posts=new ArrayList<>(long_list);
                map_posts.addAll(short_list);
                intent.putExtra("posts", map_posts);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_posts();
    }

    private void load_posts(){
        String url= Constants.KEY_DB_ADDRESS+"get_home_posts.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            home_posts.clear();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                Post post=new Post();
                                post.setId(product.getInt("id"));
                                post.setType(product.getString("type"));
                                post.setAddress(product.getString("address"));
                                post.setMetres(product.getInt("metres"));
                                post.setCost(product.getString("price"));
                                post.setDescription(product.getString("description"));
                                post.setRequirements(product.getString("requirements"));
                                post.setBeds(product.getInt("beds"));
                                post.setPlaces(product.getInt("places"));
                                post.setRent_type(product.getString("rent_type"));
                                post.setVisible(product.getInt("visibility")!=0);
                                post.setHost_id(product.getInt("host_id"));
                                String post_rating= product.getString("rating");
                                if(!post_rating.equals("null")){
                                    post.setRating(Float.parseFloat(post_rating));
                                }else {
                                    post.setRating(0.0F);
                                }
                                post.setImages(product.getString("images"));
                                post.setCoords(product.getString("coords"));
                                //adding the product to product list
                                home_posts.add(post);
                            }
                            if(filterText.getText().toString().equals("")){
                                sepList();
                            }else{
                                filter(filterText.getText().toString());
                            }
                            //creating adapter object and setting it to recyclerview
                            //HomePostListAdapter adapter = new HomePostListAdapter(HomeActivity.this,R.layout.home_post_list_item, home_posts);
                            //posts.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void setNewFragment(Fragment frag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_frame,frag);
        ft.addToBackStack(null);
        ft.commit();
    }
    private void sepList(){
        short_list.clear();
        long_list.clear();
        for (Post post:home_posts) {
            if(post.getRent_type().equals("Посуточно")){
                short_list.add(post);
            }else{
                long_list.add(post);
            }
        }
        if(shortTermFragment.adapter!=null){
            shortTermFragment.adapter.notifyDataSetChanged();
        }else{
            setNewFragment(shortTermFragment);
        }
        if(longTermFragment.adapter!=null){
            longTermFragment.adapter.notifyDataSetChanged();
        }
    }
    private void filter(String text){
        /*text=text.toLowerCase();
        String[] mas=text.split(" ");
        String pat="я, ты, он, она, мы, вы, они\n" +
                "мой, твой, его, ее, наш, ваш, их\n" +
                "мне, тебе, ему, ей, вам, им, нам\n"+
                "этот, тот, такой\n" +
                "кто, что, чей, чья\n" +
                "который, чей, что\n" +
                "кто-то, что-то, кое-кто, сколько\n" +
                "себя, себе, свой"+
                "в, на, под, перед, после, за, между, над, среди, о, об, от, к, из, у, через"+
                "и, а, но, или, чтобы, чтобы, как, так как, потому что"+
                "бы, ли, же, уж, без, ж, даже, не"+
                "Ах, ой, ура, браво, найди";
        List<String> mas2=new ArrayList<>();
        for (String s: mas) {
            if(!pat.contains(s)){
                mas2.add(s);
            }
        }*/
        List<Post> sorted_short_list=new ArrayList<>();
        List<Post> sorted_long_list=new ArrayList<>();
        List<String> key_words = Arrays.stream(text
                .replaceAll("\"(\\[\"]|.*)?\"", " ")
                .split("[^\\p{Alpha}]+"))
                .filter(s -> !s.trim().isEmpty())
                .map(Porter::stem)
                .filter(s -> s.length() > 2)
                .collect(Collectors.toList());
        Map<String,Boolean> match=new HashMap<>();
        fillMap(match,key_words);
        for (Post post:home_posts) {
            String info=post.getAddress()+" "+post.getDescription()+" "+post.getRequirements()+" "+post.getType();
            info=info.toLowerCase();
            for (Map.Entry<String,Boolean> pair: match.entrySet()) {
                if(info.contains(pair.getKey())){
                    pair.setValue(true);
                }
            }
            int sum=0;
            for (Boolean b:match.values()) {
                if(b){
                    sum+=1;
                }
            }
            if((float)sum/match.size()>0.33){
                if(post.getRent_type().equals("Посуточно")){
                    sorted_short_list.add(post);
                }else{
                    sorted_long_list.add(post);
                }
            }
            match.clear();
            fillMap(match,key_words);
        }
        System.out.println(sorted_short_list);
        System.out.println(sorted_long_list);
        if(text.contains("квартира")||text.contains("квартиру")){
            sorted_short_list.sort(Comparator.comparing(p -> !p.getType().contains("квартира")));
            sorted_long_list.sort(Comparator.comparing(p -> !p.getType().contains("квартира")));
        }
        if(text.contains("комната")||text.contains("комнату")){
            sorted_short_list.sort(Comparator.comparing(p -> !p.getType().contains("комната")));
            sorted_long_list.sort(Comparator.comparing(p -> !p.getType().contains("комната")));
        }
        if(text.contains("дом")||text.contains("коттедж")){
            sorted_short_list.sort(Comparator.comparing(p -> !p.getType().contains("дом")));
            sorted_long_list.sort(Comparator.comparing(p -> !p.getType().contains("дом")));
        }
        short_list.clear();
        long_list.clear();
        long_list.addAll(sorted_long_list);
        short_list.addAll(sorted_short_list);
        if(shortTermFragment.adapter!=null){
            shortTermFragment.adapter.notifyDataSetChanged();
        }else{
            setNewFragment(shortTermFragment);
        }
        if(longTermFragment.adapter!=null){
            longTermFragment.adapter.notifyDataSetChanged();
        }
    }
    private void fillMap(Map<String,Boolean> match,List<String> mas){
        for (String s: mas) {
            //match.put(" "+findStem(s),false);
            match.put(s,false);
        }
    }
    private String findStem(String inputWord) {
        String[] sub_1={"а","я","у","ю","е","и","л"};
        String[] sub_2={"ом","ем","ет","ем","ют","ый","ой","ий","ая","яя","ое","ее","ые","ие"};
        if(inputWord.length()<4){
            return inputWord;
        }else{
            for (String s: sub_2) {
                if(inputWord.endsWith(s))
                    return inputWord.substring(0,inputWord.length()-2);
            }
            for (String s:sub_1) {
                if(inputWord.endsWith(s))
                    return inputWord.substring(0,inputWord.length()-1);
            }
        }
        return inputWord;
    }
    private void checkPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }
}
