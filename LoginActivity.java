package com.loadbazar.app;
import android.os.*;import android.content.*;import android.view.*;import android.widget.*;
public class LoginActivity extends BaseActivity{
 public void onCreate(Bundle b){super.onCreate(b);setup("লগইন");
  content.addView(tv("LOAD BAZAR",30));content.addView(tv("আপনার অ্যাকাউন্টে লগইন করুন",15));
  EditText phone=input("মোবাইল নম্বর");content.addView(phone);EditText pass=input("৬ ডিজিট পাসওয়ার্ড");pass.setInputType(2);content.addView(pass);
  Button login=btn("লগইন");content.addView(login);login.setOnClickListener(v->go(MainActivity.class));
  Button reg=btn("নতুন অ্যাকাউন্ট খুলুন");content.addView(reg);reg.setOnClickListener(v->go(RegisterActivity.class));
  content.addView(tv("পাসওয়ার্ড ভুলে গেছেন?",14));
 }
}
