package com.loadbazar.app;
import android.os.*;import android.text.InputType;import android.widget.*;
public class RegisterActivity extends BaseActivity{
 public void onCreate(Bundle b){super.onCreate(b);setup("রেজিস্ট্রেশন");content.addView(tv("নতুন রিসেলার অ্যাকাউন্ট",24));
  content.addView(input("রিসেলার এজেন্ট কোড"));content.addView(input("পূর্ণ নাম"));content.addView(input("ফোন নম্বর (+880)"));EditText p=input("৬ ডিজিট পাসওয়ার্ড");p.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);content.addView(p);EditText c=input("পাসওয়ার্ড নিশ্চিত করুন");c.setInputType(2);content.addView(c);
  Button next=btn("পরবর্তী");content.addView(next);next.setOnClickListener(v->go(MainActivity.class));content.addView(tv("আগেই অ্যাকাউন্ট আছে? লগইন",14));}
}
