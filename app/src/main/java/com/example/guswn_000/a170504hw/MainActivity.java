package com.example.guswn_000.a170504hw;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    EditText et;
    WebView webView;
    Animation animtop;
    LinearLayout urllayout,biglayout;
    ListView listView;
    ArrayList<String> urllist = new ArrayList<>();
    ProgressDialog dialog;
    ArrayList<Bookmark> bookmarks = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Web View");
        et = (EditText)findViewById(R.id.editText);
        webView = (WebView)findViewById(R.id.WebView);
        urllayout = (LinearLayout)findViewById(R.id.linear);
        biglayout = (LinearLayout)findViewById(R.id.biglinear);
        listView = (ListView)findViewById(R.id.listview);
        dialog = new ProgressDialog(this);

        //adapter달기
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,urllist);
        listView.setAdapter(adapter);

        //아이템클릭리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                webView.loadUrl(bookmarks.get(position).getUrl());
                listView.setVisibility(View.INVISIBLE);
                biglayout.setVisibility(View.VISIBLE);
                urllayout.setVisibility(View.VISIBLE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("삭제 확인")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                bookmarks.remove(position);
                                urllist.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소",null)
                        .setMessage("정말 삭제하시겠습니까?")
                        .show();
                return true;
            }
        });

        //애니메이션
        animtop = AnimationUtils.loadAnimation(this,R.anim.translate_top);
        animtop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                urllayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new JavaScriptMethods() , "MyApp");
        webView.loadUrl("http://mengkkimon.tistory.com");


        //로딩

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress >= 100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0,1,0,"즐겨찾기목록"); //2번쨰가 아이디,3번째가 순서 4번쨰가 이름
        menu.add(0,2,1,"즐겨찾기추가");

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 1)
        {
//            urllayout.setAnimation(animtop);
            animtop.start();
            biglayout.setVisibility(View.INVISIBLE);
            urllayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

        }
        else if(item.getItemId() == 2)
        {
            biglayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);

            webView.loadUrl("file:///android_asset/bbb/urladd.html");
            urllayout.setAnimation(animtop);
            animtop.start();
        }
        return super.onOptionsItemSelected(item);
    }

    Handler myhandler = new Handler();
    class JavaScriptMethods
    {
        JavaScriptMethods(){}

        @JavascriptInterface
        public void addbookmark(final String name,final String url)
        {

            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(bookmarks.size() == 0)
                    {
                        urllist.add("< "+name+" >  "+url);
                        bookmarks.add(new Bookmark(name,url));
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        //중복되는 url 체크
                        boolean isexist = false;
                        for(int i = 0; i < urllist.size() ; i++)
                        {
                            if(bookmarks.get(i).getUrl().equals(url))
                            {
                                isexist = true;
                            }
                        }
                        if(isexist)
                        {
                            webView.loadUrl("javascript:displayMsg()");
                        }
                        else
                        {
                            urllist.add("< "+name+" >  "+url);
                            bookmarks.add(new Bookmark(name,url));
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            });
        }

        @JavascriptInterface
        public void showurllayout()
        {
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    urllayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    //Go 버튼 누르면 에딧텍스트에 입력한 주소로 이동함
    public void onClick(View v)
    {
        if (v.getId() == R.id.button)
        {
            webView.loadUrl(et.getText().toString());
        }
    }
}
