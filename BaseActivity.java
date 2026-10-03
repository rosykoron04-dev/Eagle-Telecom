package com.loadbazar.app;

import android.app.*;import android.os.*;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.view.*;import android.widget.*;import java.util.*;

public class BaseActivity extends Activity {
    int blue=Color.rgb(25,118,210), dark=Color.rgb(23,32,42), bg=Color.rgb(245,247,250), muted=Color.rgb(102,112,133);
    LinearLayout root, content;
    TextView title;
    TextView tv(String s,int sp){ TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(dark); t.setPadding(16,12,16,12); return t; }
    Button btn(String s){ Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(15); b.setTextColor(Color.WHITE); b.setBackground(round(blue,14)); b.setPadding(10,8,10,8); return b; }
    GradientDrawable round(int c,int r){ GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g; }
    EditText input(String hint){ EditText e=new EditText(this);e.setHint(hint);e.setTextSize(15);e.setSingleLine(true);e.setPadding(18,12,18,12);e.setBackground(round(Color.WHITE,12)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,55);p.setMargins(0,7,0,7);e.setLayoutParams(p);return e; }
    void setup(String screen){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(bg);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(6,4,6,4);bar.setBackgroundColor(blue);
        TextView back=tv("‹",34);back.setTextColor(Color.WHITE);back.setGravity(Gravity.CENTER);back.setOnClickListener(v->finish());bar.addView(back,new LinearLayout.LayoutParams(52,64));
        title=tv(screen,20);title.setTextColor(Color.WHITE);title.setTypeface(null,1);bar.addView(title,new LinearLayout.LayoutParams(0,64,1));
        TextView lang=tv("EN",14);lang.setTextColor(Color.WHITE);lang.setGravity(Gravity.CENTER);bar.addView(lang,new LinearLayout.LayoutParams(48,64));
        root.addView(bar); ScrollView sv=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(16,16,16,24);sv.addView(content);root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    }
    void heading(String s){ TextView h=tv(s,21);h.setTypeface(null,1);h.setPadding(4,12,4,8);content.addView(h); }
    void card(String label,String value){ LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(8,4,8,4);c.setBackground(round(Color.WHITE,16));TextView a=tv(label,13);a.setTextColor(muted);TextView b=tv(value,18);b.setTypeface(null,1);c.addView(a);c.addView(b);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,6,0,6);content.addView(c,p); }
    void go(Class<?> c){startActivity(new android.content.Intent(this,c));}
}
