package com.loadbazar.app;
import android.os.*;import android.graphics.Color;import android.view.*;import android.widget.*;
public class MainActivity extends BaseActivity{
 LinearLayout grid; TextView balance;
 String[] names={"ব্যালেন্স ডিপোজিট","মোবাইল ব্যাংকিং","ব্যাংক ট্রান্সফার","মোবাইল টপআপ","গ্রুপ চ্যাট","ড্রাইভ অফার","বিল পে","বিশেষ অফার","কাস্টমার কেয়ার","কাস্টমার রিভিউ","ভিডিও টিউটোরিয়াল","আমাদের সম্পর্কে"};
 public void onCreate(Bundle b){super.onCreate(b);setup("Load Bazar");
  LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);head.setPadding(12,12,12,12);head.setBackground(round(Color.WHITE,18));
  TextView n=tv("স্বাগতম, Reseller",17);n.setTypeface(null,1);head.addView(n);balance=tv("৳ 0.00",30);balance.setTypeface(null,1);head.addView(balance);head.addView(tv("বর্তমান ব্যালেন্স",13));content.addView(head);
  heading("সার্ভিসসমূহ"); grid=new LinearLayout(this);grid.setOrientation(LinearLayout.VERTICAL);content.addView(grid);
  for(String s:names){Button btt=btn(s);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,55);p.setMargins(0,5,0,5);grid.addView(btt,p);btt.setOnClickListener(v->open(s));}
  heading("নিচের মেনু");Button tr=btn("লেনদেনের ইতিহাস");content.addView(tr);tr.setOnClickListener(v->transaction());Button pr=btn("প্রোফাইল");content.addView(pr);pr.setOnClickListener(v->profile());
 }
 void open(String s){ if(s.equals("ব্যালেন্স ডিপোজিট"))deposit();else if(s.equals("মোবাইল ব্যাংকিং"))mobile();else if(s.equals("ব্যাংক ট্রান্সফার"))bank();else if(s.equals("মোবাইল টপআপ"))topup();else if(s.equals("ড্রাইভ অফার"))drive();else if(s.equals("বিল পে"))bill();else simple(s); }
 void page(String s){setup(s);}
 void addField(String h){content.addView(input(h));}
 void action(String label){Button b=btn(label);content.addView(b);b.setOnClickListener(v->Toast.makeText(this,"ডেমো: সফলভাবে সম্পন্ন হয়েছে",Toast.LENGTH_SHORT).show());}
 void deposit(){page("ব্যালেন্স ডিপোজিট");heading("প্যাকেজ ডিপোজিট");card("বর্তমান ব্যালেন্স","৳ 0.00");action("মোবাইল ব্যাংকিং দিয়ে ডিপোজিট");action("ব্যাংক ট্রান্সফার দিয়ে ডিপোজিট");}
 void mobile(){page("মোবাইল ব্যাংকিং");heading("সার্ভিস নির্বাচন");action("বিকাশ");action("নগদ");action("রকেট");action("উপায়");addField("মোবাইল নম্বর");addField("পরিমাণ");action("ডিপোজিট করুন");}
 void bank(){page("ব্যাংক ট্রান্সফার");addField("ব্যাংক নির্বাচন");addField("অ্যাকাউন্টের ধরন: সঞ্চয়ী / চলতি");addField("অ্যাকাউন্ট নম্বর");addField("অ্যাকাউন্ট হোল্ডারের নাম");addField("ব্রাঞ্চের নাম");action("ট্রান্সফার করুন");}
 void topup(){page("মোবাইল রিচার্জ");heading("রিচার্জের ধরন");action("প্রিপেইড");action("পোস্টপেইড");addField("মোবাইল নম্বর");addField("রিচার্জের পরিমাণ");action("রিচার্জ করুন");}
 void drive(){page("ড্রাইভ অফার");heading("অপারেটর নির্বাচন");action("গ্রামীণফোন");action("রবি");action("বাংলালিংক");addField("অফার নির্বাচন করুন");action("অফার কিনুন");}
 void bill(){page("বিল পে");heading("বিলের ক্যাটাগরি");String[] a={"ডেসকো","ডিপিডিসি","নেসকো","ওয়াসা","গ্যাস","টিটিএন্ডটি","ইন্টারনেট","মোবাইল","কেবল","ডিশ","সিটি কর"};for(String x:a)action(x);addField("বিল/অ্যাকাউন্ট নম্বর");addField("পরিমাণ");action("বিল পেমেন্ট করুন");}
 void simple(String s){page(s);content.addView(tv("এই অংশটি অ্যাপের ডেমো সংস্করণে প্রস্তুত করা হয়েছে।",16));action("শুরু করুন");}
 void transaction(){page("লেনদেনের ইতিহাস");addField("লেনদেন খুঁজুন");String[] a={"সব লেনদেন","ব্যালেন্স ডিপোজিট","মোবাইল ব্যাংকিং","মোবাইল রিচার্জ","ব্যাংক ট্রান্সফার","ড্রাইভ অফার","বিল"};for(String x:a)action(x);content.addView(tv("কোন লেনদেন পাওয়া যায়নি",16));}
 void profile(){page("প্রোফাইল");card("নাম","Reseller");card("ফোন নম্বর","+8801XXXXXXXXX");card("রেফারার কোড","DEMO001");action("পাসওয়ার্ড পরিবর্তন");action("PIN পরিবর্তন");action("ওয়েবসাইট");action("কাস্টমার কেয়ার");action("আমাদের সম্পর্কে");content.addView(tv("Version 1.0.0",13));action("লগআউট");}
}
