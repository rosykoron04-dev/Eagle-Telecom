package com.quickpay.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int BLUE = Color.rgb(8,96,190);
    private static final int GREEN = Color.rgb(0,92,68);
    private static final int YELLOW = Color.rgb(255,190,25);
    private static final int DARK = Color.rgb(35,35,35);

    private SharedPreferences pref;

    private String selectedMobileProvider = "বিকাশ";
    private String selectedAccountType = "পার্সোনাল";
    private String selectedRechargeOperator = "GP";

    /* BILL PAY */
    private String selectedBillType = "";
    private String selectedBillCode = "";
    private int selectedBillColor = BLUE;

    /* =========================================================
       BASIC HELPERS
       ========================================================= */

    private int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    private TextView tv(String s, float size, int color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private GradientDrawable outline(int color, int stroke, float radius) {
        GradientDrawable d = bg(color, radius);
        d.setStroke(dp(1), stroke);
        return d;
    }

    private void space(LinearLayout p, int h) {
        p.addView(
                new Space(this),
                new LinearLayout.LayoutParams(1, dp(h))
        );
    }

    private TextView button(String s, int color, int textColor) {
        TextView b = tv(s, 20, textColor);
        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(bg(color, 12));
        return b;
    }

    /* =========================================================
       CREATE
       ========================================================= */

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        pref = getSharedPreferences(
                "quick_pay",
                Context.MODE_PRIVATE
        );

        if (pref.getBoolean("logged_in", false)) {

            if (pref.getString("pin", "").length() == 8) {
                showPinUnlock();
            } else {
                showPinSetup();
            }

        } else {
            showLogin();
        }
    }

    /* =========================================================
       INPUT
       ========================================================= */

    private EditText input(String hint, boolean password) {

        EditText e = new EditText(this);

        e.setHint(hint);
        e.setTextSize(17);
        e.setSingleLine(true);

        e.setPadding(
                dp(18),
                0,
                dp(18),
                0
        );

        e.setTextColor(DARK);
        e.setHintTextColor(Color.GRAY);
        e.setBackground(bg(Color.WHITE, 12));

        String h = hint == null ? "" : hint;

        boolean numeric =
                password
                || h.contains("ফোন")
                || h.contains("নম্বর")
                || h.contains("টাকার পরিমাণ")
                || h.contains("PIN")
                || h.contains("পিন");

        if (numeric) {

            if (password) {

                e.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_VARIATION_PASSWORD
                );

            } else {

                e.setInputType(
                        InputType.TYPE_CLASS_PHONE
                );
            }

        } else {

            e.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            );
        }

        return e;
    }

    private EditText pinInput(String hint) {

        EditText e = input(hint, true);

        e.setGravity(Gravity.CENTER);
        e.setTextSize(20);

        e.setBackground(
                outline(
                        Color.rgb(248,250,253),
                        Color.rgb(215,225,235),
                        15
                )
        );

        return e;
    }

    /* =========================================================
       LOGIN
       ========================================================= */

    private void showLogin() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(BLUE);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        root.setPadding(
                dp(25),
                dp(18),
                dp(25),
                dp(18)
        );

        root.setBackgroundColor(BLUE);

        ScrollView scroll = new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.addView(root);

        setContentView(scroll);

        TextView lang = tv("বাংলা     EN",16,Color.WHITE);

        lang.setGravity(Gravity.CENTER);
        lang.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        lang.setBackground(bg(Color.rgb(55,130,205),40));

        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        dp(175),
                        dp(50)
                );

        lp.gravity = Gravity.RIGHT;
        root.addView(lang,lp);

        space(root,45);

        TextView logo = tv("Quick Pay",32,BLUE);

        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        logo.setBackground(bg(Color.WHITE,18));

        root.addView(
                logo,
                new LinearLayout.LayoutParams(
                        dp(300),
                        dp(95)
                )
        );

        TextView sub =
                tv(
                        "বাংলাদেশের সেরা রিচার্জ ব্যবসা প্ল্যাটফর্ম",
                        16,
                        Color.WHITE
                );

        sub.setGravity(Gravity.CENTER);

        root.addView(
                sub,
                new LinearLayout.LayoutParams(-1,dp(45))
        );

        space(root,15);

        EditText phone = input("ফোন",false);

        root.addView(
                phone,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        space(root,14);

        LinearLayout passBox = new LinearLayout(this);

        passBox.setGravity(Gravity.CENTER_VERTICAL);
        passBox.setPadding(dp(5),0,dp(5),0);
        passBox.setBackground(bg(Color.WHITE,12));

        EditText pass = input("৬ ডিজিট পাসওয়ার্ড",true);
        pass.setBackgroundColor(Color.TRANSPARENT);

        passBox.addView(
                pass,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView eye = tv("◉",23,BLUE);
        eye.setGravity(Gravity.CENTER);

        passBox.addView(
                eye,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        root.addView(
                passBox,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        eye.setOnClickListener(v -> {

            boolean hidden =
                    (pass.getInputType()
                            & InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                            != 0;

            if (hidden) {
                pass.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                pass.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_VARIATION_PASSWORD
                );
            }

            pass.setSelection(pass.length());
        });

        space(root,20);

        TextView login = button("লগইন",Color.WHITE,BLUE);

        root.addView(
                login,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        login.setOnClickListener(v -> {

            String p = phone.getText().toString().trim();
            String pw = pass.getText().toString().trim();

            if (p.isEmpty()) {
                phone.setError("ফোন নম্বর দিন");
                return;
            }

            if (pw.isEmpty()) {
                pass.setError("পাসওয়ার্ড দিন");
                return;
            }

            String sp = pref.getString("phone","");
            String sw = pref.getString("password","");

            if (!sp.isEmpty() &&
                    (!sp.equals(p) || !sw.equals(pw))) {

                Toast.makeText(
                        this,
                        "ফোন নম্বর বা পাসওয়ার্ড সঠিক নয়",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            pref.edit()
                    .putString("phone",p)
                    .putString("password",pw)
                    .putBoolean("logged_in",true)
                    .apply();

            if (pref.getString("pin","").length() == 8) {
                showHome();
            } else {
                showPinSetup();
            }
        });

        TextView forgot =
                tv(
                        "পাসওয়ার্ড ভুলে গেছেন?",
                        16,
                        Color.WHITE
                );

        forgot.setGravity(Gravity.CENTER);

        root.addView(
                forgot,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        forgot.setOnClickListener(v -> showForgotPassword());

        TextView reg =
                tv(
                        "অ্যাকাউন্ট নেই?  রেজিস্টার করুন",
                        17,
                        Color.WHITE
                );

        reg.setGravity(Gravity.CENTER);
        reg.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        root.addView(
                reg,
                new LinearLayout.LayoutParams(-1,dp(52))
        );

        reg.setOnClickListener(v -> showRegister());
    }

    /* =========================================================
       PIN SETUP
       ========================================================= */

    private void showPinSetup() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(BLUE);

        LinearLayout screen = new LinearLayout(this);

        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);

        screen.setPadding(
                dp(20),dp(15),dp(20),dp(15)
        );

        screen.setBackgroundColor(BLUE);

        setContentView(screen);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        card.setPadding(
                dp(25),dp(28),dp(25),dp(22)
        );

        card.setBackground(bg(Color.rgb(250,252,250),28));

        screen.addView(
                card,
                new LinearLayout.LayoutParams(-1,dp(500))
        );

        TextView lock = tv("🔒",50,BLUE);
        lock.setGravity(Gravity.CENTER);
        lock.setBackground(bg(Color.rgb(232,240,250),70));

        card.addView(
                lock,
                new LinearLayout.LayoutParams(dp(105),dp(105))
        );

        space(card,18);

        TextView title = tv("পিন সেট করুন",26,BLUE);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                title,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        TextView sub =
                tv(
                        "অ্যাপে ঢোকার জন্য ৮ ডিজিটের পিন দিন",
                        17,
                        Color.DKGRAY
                );

        sub.setGravity(Gravity.CENTER);

        card.addView(
                sub,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        space(card,8);

        EditText p = pinInput("৮ ডিজিট PIN");
        EditText c = pinInput("PIN আবার দিন");

        card.addView(
                p,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        space(card,10);

        card.addView(
                c,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        space(card,16);

        TextView save =
                button(
                        "পিন সেট করুন  ✓",
                        Color.TRANSPARENT,
                        BLUE
                );

        save.setBackground(
                outline(
                        Color.TRANSPARENT,
                        Color.rgb(80,145,205),
                        16
                )
        );

        card.addView(
                save,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        save.setOnClickListener(v -> {

            String a = p.getText().toString().trim();
            String b = c.getText().toString().trim();

            if (a.length() != 8) {
                p.setError("৮ ডিজিটের PIN দিন");
                return;
            }

            if (!a.equals(b)) {
                c.setError("দুইটি PIN একই নয়");
                return;
            }

            pref.edit()
                    .putString("pin",a)
                    .putBoolean("logged_in",true)
                    .apply();

            showHome();
        });
    }

    /* =========================================================
       PIN UNLOCK
       ========================================================= */

    private void showPinUnlock() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(BLUE);

        LinearLayout screen = new LinearLayout(this);

        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);

        screen.setPadding(
                dp(20),dp(15),dp(20),dp(15)
        );

        screen.setBackgroundColor(BLUE);

        setContentView(screen);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);

        card.setPadding(
                dp(25),dp(28),dp(25),dp(20)
        );

        card.setBackground(bg(Color.rgb(250,252,250),28));

        screen.addView(
                card,
                new LinearLayout.LayoutParams(-1,dp(480))
        );

        TextView lock = tv("🔒",50,BLUE);
        lock.setGravity(Gravity.CENTER);
        lock.setBackground(bg(Color.rgb(232,240,250),70));

        card.addView(
                lock,
                new LinearLayout.LayoutParams(dp(105),dp(105))
        );

        space(card,18);

        TextView title = tv("পিন যাচাই করুন",26,BLUE);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                title,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        TextView sub =
                tv(
                        "আপনার ৮ ডিজিটের পিন দিন",
                        17,
                        Color.DKGRAY
                );

        sub.setGravity(Gravity.CENTER);

        card.addView(
                sub,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        space(card,8);

        EditText p = pinInput("PIN");

        card.addView(
                p,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        space(card,18);

        TextView verify =
                button(
                        "যাচাই করুন  ✓",
                        Color.TRANSPARENT,
                        BLUE
                );

        verify.setBackground(
                outline(
                        Color.TRANSPARENT,
                        Color.rgb(80,145,205),
                        16
                )
        );

        card.addView(
                verify,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        verify.setOnClickListener(v -> {

            String saved = pref.getString("pin","");

            if (p.getText().toString().trim().equals(saved)) {
                showHome();
            } else {
                p.setError("ভুল PIN");
            }
        });

        TextView forgot =
                tv(
                        "PIN ভুলে গেছেন?  লগইন করুন",
                        16,
                        BLUE
                );

        forgot.setGravity(Gravity.CENTER);

        card.addView(
                forgot,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        forgot.setOnClickListener(v -> {

            pref.edit()
                    .putBoolean("logged_in",false)
                    .apply();

            showLogin();
        });
    }

    /* =========================================================
       HOME SERVICE
       ========================================================= */

    private LinearLayout serviceRow(LinearLayout parent) {

        LinearLayout r = new LinearLayout(this);

        r.setGravity(Gravity.CENTER);

        parent.addView(
                r,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        return r;
    }

    private void service(
            LinearLayout row,
            String icon,
            String title) {

        LinearLayout box = new LinearLayout(this);

        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        TextView i = tv(icon,27,DARK);
        i.setGravity(Gravity.CENTER);

        box.addView(
                i,
                new LinearLayout.LayoutParams(-1,dp(38))
        );

        TextView t = tv(title,11,DARK);
        t.setGravity(Gravity.CENTER);

        box.addView(
                t,
                new LinearLayout.LayoutParams(-1,dp(38))
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,-1,1);

        p.setMargins(dp(1),dp(1),dp(1),dp(1));

        row.addView(box,p);

        if (title.contains("অ্যাড\nব্যালেন্স")) {

            box.setOnClickListener(v -> showAddBalance());

        } else if (title.contains("মোবাইল\nব্যাংকিং")) {

            box.setOnClickListener(v -> showMobileBanking());

        } else if (title.contains("ব্যাংক\nট্রান্সফার")) {

            box.setOnClickListener(v -> showBankTransfer());

        } else if (title.contains("মোবাইল\nরিচার্জ")) {

            box.setOnClickListener(v -> showMobileRecharge());

        } else if (title.contains("গ্রুপ\nচ্যাট")) {

            box.setOnClickListener(v -> showGroupChat());

        } else if (title.contains("ইনভাইট\nবোনাস")) {

            box.setOnClickListener(v -> showInviteBonus());

        } else if (title.contains("বিশেষ\nঅফার")) {

            box.setOnClickListener(v -> showSpecialOffers());

        } else if (title.contains("কাস্টমার\nকেয়ার")) {

            box.setOnClickListener(v -> showCustomerCare());

        } else if (title.contains("কাস্টমার\nরিভিউ")) {

            box.setOnClickListener(v -> showCustomerReviews());

        } else if (title.contains("ভিডিও\nটিউটোরিয়াল")) {

            box.setOnClickListener(v -> showVideoTutorials());

        } else if (title.contains("বিল\nপে")) {

            box.setOnClickListener(v -> showBillPay());
        }
    }

    private void bonusBox(
            LinearLayout parent,
            String amount,
            String bonus) {

        LinearLayout b = new LinearLayout(this);

        b.setOrientation(LinearLayout.VERTICAL);
        b.setGravity(Gravity.CENTER);
        b.setBackground(bg(Color.WHITE,10));

        TextView a = tv(amount,13,DARK);
        a.setGravity(Gravity.CENTER);

        TextView x = tv(bonus,10,Color.rgb(170,40,40));
        x.setGravity(Gravity.CENTER);

        b.addView(
                a,
                new LinearLayout.LayoutParams(-1,dp(22))
        );

        b.addView(
                x,
                new LinearLayout.LayoutParams(-1,dp(20))
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,dp(44),1);

        p.setMargins(dp(3),0,dp(3),0);

        parent.addView(b,p);
    }

    private void nav(LinearLayout parent,String s) {

        TextView n = tv(s,14,DARK);

        n.setGravity(Gravity.CENTER);

        parent.addView(
                n,
                new LinearLayout.LayoutParams(0,dp(58),1)
        );
    }

    /* =========================================================
       HOME
       ========================================================= */

    private void showHome() {

        getWindow().setStatusBarColor(GREEN);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.WHITE);

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(10),dp(6),dp(10),dp(6));
        header.setBackgroundColor(GREEN);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(178))
        );

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);

        header.addView(
                top,
                new LinearLayout.LayoutParams(-1,dp(50))
        );

        TextView brand = tv("Quick Pay",21,DARK);

        brand.setGravity(Gravity.CENTER);
        brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        brand.setBackground(bg(Color.WHITE,15));

        top.addView(
                brand,
                new LinearLayout.LayoutParams(dp(145),dp(45))
        );

        top.addView(
                new Space(this),
                new LinearLayout.LayoutParams(0,1,1)
        );

        TextView en = tv("EN",15,Color.WHITE);
        en.setGravity(Gravity.CENTER);

        top.addView(
                en,
                new LinearLayout.LayoutParams(dp(38),dp(45))
        );

        TextView bell = tv("🔔",19,Color.WHITE);
        bell.setGravity(Gravity.CENTER);

        top.addView(
                bell,
                new LinearLayout.LayoutParams(dp(42),dp(45))
        );

        TextView out = tv("⇥",25,Color.WHITE);
        out.setGravity(Gravity.CENTER);

        top.addView(
                out,
                new LinearLayout.LayoutParams(dp(42),dp(45))
        );

        out.setOnClickListener(v -> {

            pref.edit()
                    .putBoolean("logged_in",false)
                    .apply();

            showLogin();
        });

        LinearLayout user = new LinearLayout(this);
        user.setGravity(Gravity.CENTER_VERTICAL);

        header.addView(
                user,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView name =
                tv(
                        pref.getString("name","Rosy"),
                        25,
                        Color.WHITE
                );

        name.setGravity(Gravity.CENTER_VERTICAL);
        name.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        user.addView(
                name,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView bal =
                tv(
                        "Main Balance  ৳ ১২,৫০০\nDrive Balance  ৳ ১৮০",
                        12,
                        DARK
                );

        bal.setGravity(Gravity.CENTER);
        bal.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        bal.setBackground(bg(YELLOW,30));

        user.addView(
                bal,
                new LinearLayout.LayoutParams(dp(190),dp(56))
        );

        TextView notice =
                tv(
                        "●  সর্বশেষ আপডেট: Quick Pay-এ স্বাগতম",
                        12,
                        Color.DKGRAY
                );

        notice.setGravity(Gravity.CENTER_VERTICAL);
        notice.setPadding(dp(10),0,dp(5),0);
        notice.setSingleLine(true);
        notice.setBackground(bg(Color.WHITE,12));

        header.addView(
                notice,
                new LinearLayout.LayoutParams(-1,dp(38))
        );

        LinearLayout services = new LinearLayout(this);

        services.setOrientation(LinearLayout.VERTICAL);
        services.setPadding(dp(12),dp(4),dp(12),dp(2));

        main.addView(
                services,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        LinearLayout r = serviceRow(services);

        service(r,"👛","অ্যাড\nব্যালেন্স");
        service(r,"💵","মোবাইল\nব্যাংকিং");
        service(r,"🏦","ব্যাংক\nট্রান্সফার");
        service(r,"📱","মোবাইল\nরিচার্জ");

        r = serviceRow(services);

        service(r,"💬","গ্রুপ\nচ্যাট");
        service(r,"🎁","ইনভাইট\nবোনাস");
        service(r,"🧾","বিল\nপে");
        service(r,"🏷","বিশেষ\nঅফার");

        r = serviceRow(services);

        service(r,"🎧","কাস্টমার\nকেয়ার");
        service(r,"⭐","কাস্টমার\nরিভিউ");
        service(r,"▶","ভিডিও\nটিউটোরিয়াল");
        service(r,"👥","কন্টাক্ট\nআস");

        LinearLayout bonus = new LinearLayout(this);

        bonus.setOrientation(LinearLayout.VERTICAL);
        bonus.setPadding(dp(10),dp(2),dp(10),dp(3));
        bonus.setBackground(bg(GREEN,16));

        main.addView(
                bonus,
                new LinearLayout.LayoutParams(-1,dp(98))
        );

        TextView bt =
                tv(
                        "🎁  ডিপোজিট বোনাস অফার",
                        18,
                        Color.WHITE
                );

        bt.setGravity(Gravity.CENTER_VERTICAL);
        bt.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        bonus.addView(
                bt,
                new LinearLayout.LayoutParams(-1,dp(32))
        );

        TextView bs =
                tv(
                        "এখনই করুন, বোনাস নিয়ে নিন!",
                        11,
                        Color.WHITE
                );

        bonus.addView(
                bs,
                new LinearLayout.LayoutParams(-1,dp(20))
        );

        LinearLayout bb = new LinearLayout(this);
        bb.setGravity(Gravity.CENTER);

        bonus.addView(
                bb,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        bonusBox(bb,"৳ ৫০০","বোনাস ৳ ৫০");
        bonusBox(bb,"৳ ১০০০","বোনাস ৳ ১০০");
        bonusBox(bb,"৳ ২০০০","বোনাস ৳ ২০০");

        LinearLayout bottom = new LinearLayout(this);

        bottom.setGravity(Gravity.CENTER);
        bottom.setBackgroundColor(Color.WHITE);

        main.addView(
                bottom,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        nav(bottom,"⌂\nহোম");
        nav(bottom,"◷\nলেনদেন");
        nav(bottom,"♙\nপ্রোফাইল");
    }

    /* =========================================================
       BILL PAY
       ========================================================= */

    private void showBillPay() {

        selectedBillType = "";
        selectedBillCode = "";
        selectedBillColor = BLUE;

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title = tv("বিল পেমেন্ট",21,Color.WHITE);

        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        header.addView(
                new Space(this),
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(10),dp(12),dp(25));

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        LinearLayout heading = new LinearLayout(this);

        heading.setGravity(Gravity.CENTER_VERTICAL);

        content.addView(
                heading,
                new LinearLayout.LayoutParams(-1,dp(45))
        );

        TextView doc = tv("▤",23,Color.rgb(70,80,95));
        doc.setGravity(Gravity.CENTER);

        heading.addView(
                doc,
                new LinearLayout.LayoutParams(dp(38),dp(42))
        );

        TextView choose =
                tv(
                        "বিলের ধরন নির্বাচন করুন",
                        16,
                        DARK
                );

        choose.setGravity(Gravity.CENTER_VERTICAL);
        choose.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        heading.addView(
                choose,
                new LinearLayout.LayoutParams(0,dp(42),1)
        );

        LinearLayout row = null;

        String[][] types = {
                {"ডেসকো","⚡","DES","0"},
                {"ডিপিডিসি","⚡","DPDC","1"},
                {"নেসকো","ϟ","NESCO","2"},
                {"ওয়াসা","💧","WASA","3"},
                {"গ্যাস","♨","GAS","4"},
                {"টেলিফোন","☎","TEL","5"},
                {"ইন্টারনেট","⌁","INT","6"},
                {"মোবাইল","▯","MOB","7"},
                {"কেবল","▭","CAB","8"},
                {"ডিশ","◉","DISH","9"},
                {"সিটি কর্প","▥","CITY","10"}
        };

        for (int i = 0; i < types.length; i++) {

            if (i % 2 == 0) {

                row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER);

                content.addView(
                        row,
                        new LinearLayout.LayoutParams(-1,dp(128))
                );
            }

            addBillType(
                    row,
                    types[i][0],
                    types[i][1],
                    types[i][2],
                    Integer.parseInt(types[i][3])
            );
        }
    }

    /* =========================================================
       BILL TYPE CARD
       ========================================================= */

    private void addBillType(
            LinearLayout parent,
            String name,
            String icon,
            String code,
            int index) {

        int[] colors = {
                Color.rgb(255,170,20),
                Color.rgb(255,82,55),
                Color.rgb(72,155,75),
                Color.rgb(35,150,240),
                Color.rgb(255,80,75),
                Color.rgb(75,180,90),
                Color.rgb(125,75,205),
                Color.rgb(30,175,170),
                Color.rgb(135,110,100),
                Color.rgb(85,105,205),
                Color.rgb(85,125,145)
        };

        int color = colors[
                Math.min(index,colors.length - 1)
        ];

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);

        card.setPadding(
                dp(5),
                dp(7),
                dp(5),
                dp(7)
        );

        card.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(225,225,225),
                        13
                )
        );

        LinearLayout.LayoutParams cp =
                new LinearLayout.LayoutParams(
                        0,
                        dp(116),
                        1
                );

        cp.setMargins(
                dp(3),
                dp(5),
                dp(3),
                dp(5)
        );

        parent.addView(card,cp);

        TextView iconView =
                tv(
                        icon,
                        28,
                        color
                );

        iconView.setGravity(Gravity.CENTER);
        iconView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        GradientDrawable circle =
                new GradientDrawable();

        circle.setShape(
                GradientDrawable.OVAL
        );

        circle.setColor(
                Color.argb(
                        35,
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color)
                )
        );

        iconView.setBackground(circle);

        LinearLayout.LayoutParams ip =
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                );

        card.addView(iconView,ip);

        TextView nameView =
                tv(
                        name,
                        14,
                        DARK
                );

        nameView.setGravity(Gravity.CENTER);
        nameView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        card.addView(
                nameView,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(35)
                )
        );

        card.setOnClickListener(v -> {

            selectedBillType = name;
            selectedBillCode = code;
            selectedBillColor = color;

            showBillPayForm();
        });
    }

    /* =========================================================
       BILL PAY FORM
       ========================================================= */

    private void showBillPayForm() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title = tv("বিল পেমেন্ট",21,Color.WHITE);

        title.setGravity(Gravity.CENTER);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        header.addView(
                new Space(this),
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(25)
        );

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        LinearLayout heading = new LinearLayout(this);

        heading.setGravity(Gravity.CENTER_VERTICAL);

        content.addView(
                heading,
                new LinearLayout.LayoutParams(-1,dp(45))
        );

        TextView doc = tv("▤",23,Color.rgb(70,80,95));
        doc.setGravity(Gravity.CENTER);

        heading.addView(
                doc,
                new LinearLayout.LayoutParams(dp(38),dp(42))
        );

        TextView headingText =
                tv(
                        "বিলের ধরন নির্বাচন করুন",
                        16,
                        DARK
                );

        headingText.setGravity(
                Gravity.CENTER_VERTICAL
        );

        headingText.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        heading.addView(
                headingText,
                new LinearLayout.LayoutParams(0,dp(42),1)
        );

        TextView change =
                tv(
                        "পরিবর্তন",
                        14,
                        BLUE
                );

        change.setGravity(Gravity.CENTER);

        heading.addView(
                change,
                new LinearLayout.LayoutParams(dp(75),dp(42))
        );

        change.setOnClickListener(
                v -> showBillPay()
        );

        /* SELECTED BILL CARD */

        LinearLayout selectedCard =
                new LinearLayout(this);

        selectedCard.setOrientation(
                LinearLayout.VERTICAL
        );

        selectedCard.setGravity(
                Gravity.CENTER
        );

        selectedCard.setBackground(
                outline(
                        Color.WHITE,
                        selectedBillColor,
                        13
                )
        );

        content.addView(
                selectedCard,
                new LinearLayout.LayoutParams(-1,dp(126))
        );

        TextView selectedIcon =
                tv(
                        getBillIcon(selectedBillType),
                        28,
                        selectedBillColor
                );

        selectedIcon.setGravity(Gravity.CENTER);

        GradientDrawable selectedCircle =
                new GradientDrawable();

        selectedCircle.setShape(
                GradientDrawable.OVAL
        );

        selectedCircle.setColor(
                Color.argb(
                        35,
                        Color.red(selectedBillColor),
                        Color.green(selectedBillColor),
                        Color.blue(selectedBillColor)
                )
        );

        selectedIcon.setBackground(
                selectedCircle
        );

        selectedCard.addView(
                selectedIcon,
                new LinearLayout.LayoutParams(
                        dp(58),
                        dp(58)
                )
        );

        TextView selectedName =
                tv(
                        selectedBillType,
                        14,
                        DARK
                );

        selectedName.setGravity(Gravity.CENTER);
        selectedName.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        selectedCard.addView(
                selectedName,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(35)
                )
        );

        space(content,10);

        LinearLayout infoTitle =
                new LinearLayout(this);

        infoTitle.setGravity(
                Gravity.CENTER_VERTICAL
        );

        content.addView(
                infoTitle,
                new LinearLayout.LayoutParams(-1,dp(40))
        );

        TextView infoIcon =
                tv("ⓘ",21,Color.rgb(70,80,95));

        infoIcon.setGravity(Gravity.CENTER);

        infoTitle.addView(
                infoIcon,
                new LinearLayout.LayoutParams(dp(35),dp(38))
        );

        TextView info =
                tv(
                        "বিলের তথ্য দিন",
                        17,
                        DARK
                );

        info.setGravity(
                Gravity.CENTER_VERTICAL
        );

        info.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        infoTitle.addView(
                info,
                new LinearLayout.LayoutParams(0,dp(38),1)
        );

        space(content,3);

        TextView numberTitle =
                tv(
                        "বিল নম্বর",
                        14,
                        Color.DKGRAY
                );

        numberTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                numberTitle,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        EditText billNumber =
                new EditText(this);

        billNumber.setHint(
                "বিল নম্বর (" +
                        selectedBillCode +
                        "...)"
        );

        billNumber.setTextSize(16);
        billNumber.setSingleLine(true);
        billNumber.setTextColor(DARK);
        billNumber.setHintTextColor(Color.GRAY);
        billNumber.setPadding(
                dp(15),
                0,
                dp(15),
                0
        );

        billNumber.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        );

        billNumber.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        11
                )
        );

        content.addView(
                billNumber,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(content,8);

        TextView amountTitle =
                tv(
                        "বিলের পরিমাণ",
                        14,
                        Color.DKGRAY
                );

        amountTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                amountTitle,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        EditText billAmount =
                new EditText(this);

        billAmount.setHint(
                "বিলের পরিমাণ (সর্বনিম্ন ৳500)"
        );

        billAmount.setTextSize(16);
        billAmount.setSingleLine(true);
        billAmount.setTextColor(DARK);
        billAmount.setHintTextColor(Color.GRAY);

        billAmount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        billAmount.setPadding(
                dp(15),
                0,
                dp(15),
                0
        );

        billAmount.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        11
                )
        );

        content.addView(
                billAmount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(content,18);

        TextView pay =
                button(
                        "বিল পেমেন্ট করুন",
                        Color.rgb(165,165,165),
                        Color.WHITE
                );

        pay.setEnabled(false);

        content.addView(
                pay,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(content,18);

        double balance =
                getMainBalance();

        TextView balanceView =
                tv(
                        "▣  বর্তমান ব্যালেন্স:  ৳ "
                                + formatMoney(balance),
                        17,
                        Color.rgb(55,75,100)
                );

        balanceView.setGravity(
                Gravity.CENTER
        );

        balanceView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        balanceView.setBackground(
                bg(Color.WHITE,12)
        );

        content.addView(
                balanceView,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        TextWatcher watcher =
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        updateBillPayButton(
                                billNumber,
                                billAmount,
                                pay
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                };

        billNumber.addTextChangedListener(watcher);
        billAmount.addTextChangedListener(watcher);

        pay.setOnClickListener(v -> {

            String number =
                    billNumber.getText()
                            .toString()
                            .trim();

            String money =
                    billAmount.getText()
                            .toString()
                            .trim();

            if (number.isEmpty()) {
                billNumber.setError(
                        "বিল নম্বর দিন"
                );
                return;
            }

            if (money.isEmpty()) {
                billAmount.setError(
                        "বিলের পরিমাণ দিন"
                );
                return;
            }

            double value;

            try {

                value =
                        Double.parseDouble(
                                money
                        );

            } catch (Exception e) {

                billAmount.setError(
                        "সঠিক টাকার পরিমাণ দিন"
                );

                return;
            }

            if (value < 500) {

                billAmount.setError(
                        "সর্বনিম্ন বিল পেমেন্ট ৳500"
                );

                return;
            }

            double currentBalance =
                    getMainBalance();

            if (currentBalance > 0 &&
                    value > currentBalance) {

                new AlertDialog.Builder(this)
                        .setTitle(
                                "পর্যাপ্ত ব্যালেন্স নেই"
                        )
                        .setMessage(
                                "আপনার বর্তমান ব্যালেন্স: ৳ "
                                        + formatMoney(
                                        currentBalance
                                )
                                        + "\n"
                                        + "বিলের পরিমাণ: ৳ "
                                        + formatMoney(
                                        value
                                )
                        )
                        .setPositiveButton(
                                "ঠিক আছে",
                                null
                        )
                        .show();

                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle(
                            "বিল পেমেন্ট নিশ্চিত করুন"
                    )
                    .setMessage(
                            "বিলের ধরন: "
                                    + selectedBillType
                                    + "\n\n"
                                    + "বিল নম্বর: "
                                    + number
                                    + "\n\n"
                                    + "বিলের পরিমাণ: ৳ "
                                    + money
                                    + "\n\n"
                                    + "এই ভার্সনে এটি Demo Bill Payment Request। "
                                    + "আসল বিল পরিশোধের জন্য সংশ্লিষ্ট Provider API/Backend সংযুক্ত করতে হবে।"
                    )
                    .setNegativeButton(
                            "বাতিল",
                            null
                    )
                    .setPositiveButton(
                            "নিশ্চিত করুন",
                            (dialog,which) -> {

                                Toast.makeText(
                                        this,
                                        selectedBillType
                                                + " বিল পেমেন্ট রিকোয়েস্ট গ্রহণ করা হয়েছে।",
                                        Toast.LENGTH_LONG
                                ).show();

                                billNumber.setText("");
                                billAmount.setText("");
                            }
                    )
                    .show();
        });
    }

    /* =========================================================
       BILL PAY BUTTON
       ========================================================= */

    private void updateBillPayButton(
            EditText number,
            EditText amount,
            TextView pay) {

        String n =
                number.getText()
                        .toString()
                        .trim();

        String a =
                amount.getText()
                        .toString()
                        .trim();

        boolean valid = false;

        if (!n.isEmpty() && !a.isEmpty()) {

            try {

                double value =
                        Double.parseDouble(a);

                valid = value >= 500;

            } catch (Exception ignored) {
                valid = false;
            }
        }

        if (valid) {

            pay.setEnabled(true);
            pay.setTextColor(Color.WHITE);
            pay.setBackground(
                    bg(BLUE,12)
            );

        } else {

            pay.setEnabled(false);
            pay.setTextColor(Color.WHITE);
            pay.setBackground(
                    bg(Color.rgb(165,165,165),12)
            );
        }
    }

    /* =========================================================
       BILL ICON
       ========================================================= */

    private String getBillIcon(String type) {

        if (type.equals("ডেসকো")) return "⚡";
        if (type.equals("ডিপিডিসি")) return "⚡";
        if (type.equals("নেসকো")) return "ϟ";
        if (type.equals("ওয়াসা")) return "💧";
        if (type.equals("গ্যাস")) return "♨";
        if (type.equals("টেলিফোন")) return "☎";
        if (type.equals("ইন্টারনেট")) return "⌁";
        if (type.equals("মোবাইল")) return "▯";
        if (type.equals("কেবল")) return "▭";
        if (type.equals("ডিশ")) return "◉";
        if (type.equals("সিটি কর্প")) return "▥";

        return "▤";
    }

    /* =========================================================
       BILL BALANCE
       ========================================================= */

    private double getMainBalance() {

        String saved =
                pref.getString(
                        "main_balance",
                        "0"
                );

        try {

            return Double.parseDouble(saved);

        } catch (Exception e) {

            return 0;
        }
    }

    private String formatMoney(double value) {

        if (value == (long)value) {

            return String.format(
                    Locale.getDefault(),
                    "%d.00",
                    (long)value
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f",
                value
        );
    }

    /* =========================================================
       INVITE BONUS
       ========================================================= */

    private String getInviteCode() {

        String code = pref.getString("invite_code", "");

        if (code.isEmpty()) {

            String phone =
                    pref.getString("phone", "")
                            .replaceAll("[^0-9]", "");

            String tail =
                    phone.length() >= 4
                            ? phone.substring(phone.length() - 4)
                            : "0000";

            code = "QP" + tail;

            pref.edit()
                    .putString("invite_code", code)
                    .apply();
        }

        return code;
    }

    private int getInviteCount() {
        return pref.getInt("invite_count", 0);
    }

    // Demo panel থেকে Invite Count পরিবর্তন করার জন্য
    private void setInviteCount(int count) {
        if (count < 0) count = 0;
        pref.edit()
                .putInt("invite_count", count)
                .apply();
    }

    private double getInviteEarned() {
        return Double.longBitsToDouble(
                pref.getLong(
                        "invite_earned_bits",
                        Double.doubleToLongBits(0.0)
                )
        );
    }

    private void saveInviteEarned(double value) {
        pref.edit()
                .putLong(
                        "invite_earned_bits",
                        Double.doubleToLongBits(value)
                )
                .apply();
    }

    private void copyInviteCode() {

        String code = getInviteCode();

        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

        android.content.ClipData clip =
                android.content.ClipData.newPlainText(
                        "Quick Pay Invite Code",
                        code
                );

        clipboard.setPrimaryClip(clip);

        Toast.makeText(
                this,
                "ইনভাইট কোড কপি হয়েছে: " + code,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void shareInvite() {

        String code = getInviteCode();

        String link =
                "quickpay://invite/" + code;

        String message =
                "🎁 Quick Pay-এ আমাকে ইনভাইট করা হয়েছে!\n\n"
                        + "Invite Code: " + code + "\n"
                        + "Invite Link: " + link + "\n\n"
                        + "অ্যাপে রেজিস্ট্রেশনের সময় এই কোড ব্যবহার করুন।";

        android.content.Intent share =
                new android.content.Intent(
                        android.content.Intent.ACTION_SEND
                );

        share.setType("text/plain");
        share.putExtra(
                android.content.Intent.EXTRA_TEXT,
                message
        );

        startActivity(
                android.content.Intent.createChooser(
                        share,
                        "ইনভাইট শেয়ার করুন"
                )
        );
    }

    private void showInviteBonus() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title =
                tv("ইনভাইট বোনাস",21,Color.WHITE);

        title.setGravity(Gravity.CENTER);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        header.addView(
                new Space(this),
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dp(12),dp(12),dp(12),dp(28)
        );

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        LinearLayout hero = new LinearLayout(this);

        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.setPadding(
                dp(18),dp(20),dp(18),dp(20)
        );
        hero.setBackground(bg(GREEN,18));

        content.addView(
                hero,
                new LinearLayout.LayoutParams(-1,dp(190))
        );

        TextView gift = tv("🎁",44,Color.WHITE);
        gift.setGravity(Gravity.CENTER);

        hero.addView(
                gift,
                new LinearLayout.LayoutParams(-1,dp(52))
        );

        TextView ht =
                tv(
                        "বন্ধুকে ইনভাইট করুন",
                        22,
                        Color.WHITE
                );

        ht.setGravity(Gravity.CENTER);
        ht.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        hero.addView(
                ht,
                new LinearLayout.LayoutParams(-1,dp(40))
        );

        TextView hs =
                tv(
                        "আপনার ইনভাইট কোড শেয়ার করে নতুন ইউজার আনুন",
                        13,
                        Color.WHITE
                );

        hs.setGravity(Gravity.CENTER);

        hero.addView(
                hs,
                new LinearLayout.LayoutParams(-1,dp(35))
        );

        space(content,10);

        LinearLayout codeCard = new LinearLayout(this);

        codeCard.setOrientation(LinearLayout.VERTICAL);
        codeCard.setPadding(
                dp(16),dp(14),dp(16),dp(14)
        );
        codeCard.setBackground(bg(Color.WHITE,16));

        content.addView(
                codeCard,
                new LinearLayout.LayoutParams(-1,dp(142))
        );

        TextView cl =
                tv(
                        "আপনার Invite Code",
                        14,
                        Color.DKGRAY
                );

        cl.setGravity(Gravity.CENTER);

        codeCard.addView(
                cl,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        TextView code =
                tv(
                        getInviteCode(),
                        28,
                        BLUE
                );

        code.setGravity(Gravity.CENTER);
        code.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        codeCard.addView(
                code,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        TextView copy =
                button(
                        "কোড কপি করুন",
                        BLUE,
                        Color.WHITE
                );

        codeCard.addView(
                copy,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        copy.setOnClickListener(v -> copyInviteCode());

        space(content,10);

        LinearLayout shareCard = new LinearLayout(this);

        shareCard.setOrientation(LinearLayout.VERTICAL);
        shareCard.setPadding(
                dp(16),dp(14),dp(16),dp(14)
        );
        shareCard.setBackground(bg(Color.WHITE,16));

        content.addView(
                shareCard,
                new LinearLayout.LayoutParams(-1,dp(128))
        );

        TextView st =
                tv(
                        "Invite Link",
                        15,
                        DARK
                );

        st.setGravity(Gravity.CENTER);

        shareCard.addView(
                st,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        TextView link =
                tv(
                        "quickpay://invite/"
                                + getInviteCode(),
                        13,
                        Color.DKGRAY
                );

        link.setGravity(Gravity.CENTER);

        shareCard.addView(
                link,
                new LinearLayout.LayoutParams(-1,dp(32))
        );

        TextView share =
                button(
                        "শেয়ার ইনভাইট",
                        GREEN,
                        Color.WHITE
                );

        shareCard.addView(
                share,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        share.setOnClickListener(v -> shareInvite());

        space(content,10);

        LinearLayout stats = new LinearLayout(this);

        stats.setGravity(Gravity.CENTER);

        content.addView(
                stats,
                new LinearLayout.LayoutParams(-1,dp(92))
        );

        TextView invited =
                tv(
                        "👤\n"
                                + getInviteCount()
                                + "\nইনভাইটেড",
                        15,
                        DARK
                );

        invited.setGravity(Gravity.CENTER);
        invited.setBackground(bg(Color.WHITE,14));

        stats.addView(
                invited,
                new LinearLayout.LayoutParams(0,dp(82),1)
        );

        TextView earned =
                tv(
                        "💰\n৳ "
                                + formatMoney(getInviteEarned())
                                + "\nবোনাস",
                        15,
                        DARK
                );

        earned.setGravity(Gravity.CENTER);
        earned.setBackground(bg(Color.WHITE,14));

        LinearLayout.LayoutParams ep =
                new LinearLayout.LayoutParams(0,dp(82),1);

        ep.setMargins(dp(8),0,0,0);

        stats.addView(earned,ep);

        space(content,10);

        TextView rulesTitle =
                tv(
                        "📋 ইনভাইট বোনাস নিয়ম",
                        18,
                        DARK
                );

        rulesTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                rulesTitle,
                new LinearLayout.LayoutParams(-1,dp(38))
        );

        TextView rules =
                tv(
                        "• আপনার Invite Code কপি বা শেয়ার করুন।\n"
                                + "• নতুন ইউজার রেজিস্ট্রেশনের সময় কোড ব্যবহার করতে পারবে।\n"
                                + "• Invite Count ও Bonus Amount ডেমো প্যানেল থেকে ইচ্ছামতো পরিবর্তন করা যাবে।\n"
                                + "• নতুন Invite হলে সংখ্যা ১, ২, ৩, ৪, ৫, ৬... এভাবে দেখাবে।",
                        14,
                        Color.DKGRAY
                );

        rules.setPadding(
                dp(15),dp(12),dp(15),dp(12)
        );
        rules.setBackground(bg(Color.WHITE,14));

        content.addView(
                rules,
                new LinearLayout.LayoutParams(-1,dp(150))
        );

        space(content,10);

        TextView historyButton =
                button(
                        "🎁 ইনভাইট ট্রান্সেকশন / হিস্টোরি",
                        Color.WHITE,
                        BLUE
                );

        content.addView(
                historyButton,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        historyButton.setOnClickListener(
                v -> showInviteHistory()
        );
    }

    private void showInviteHistory() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showInviteBonus());

        TextView title =
                tv("ইনভাইট ট্রান্সেকশন",20,Color.WHITE);

        title.setGravity(Gravity.CENTER);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        header.addView(
                new Space(this),
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout list = new LinearLayout(this);

        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(
                dp(12),dp(12),dp(12),dp(25)
        );

        scroll.addView(list);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        int count = getInviteCount();

        if (count == 0) {

            LinearLayout empty = new LinearLayout(this);

            empty.setOrientation(LinearLayout.VERTICAL);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(
                    dp(15),dp(25),dp(15),dp(25)
            );
            empty.setBackground(bg(Color.WHITE,16));

            list.addView(
                    empty,
                    new LinearLayout.LayoutParams(-1,dp(190))
            );

            TextView icon = tv("🎁",42,DARK);
            icon.setGravity(Gravity.CENTER);

            empty.addView(
                    icon,
                    new LinearLayout.LayoutParams(-1,dp(55))
            );

            TextView msg =
                    tv(
                            "এখনও কোনো ইনভাইট ট্রান্সেকশন নেই",
                            16,
                            DARK
                    );

            msg.setGravity(Gravity.CENTER);

            empty.addView(
                    msg,
                    new LinearLayout.LayoutParams(-1,dp(45))
            );

            TextView sub =
                    tv(
                            "Invite Code শেয়ার করলে এখানে হিসাব দেখানো যাবে।",
                            13,
                            Color.GRAY
                    );

            sub.setGravity(Gravity.CENTER);

            empty.addView(
                    sub,
                    new LinearLayout.LayoutParams(-1,dp(45))
            );

            return;
        }

        for (int i = count; i >= 1; i--) {

            LinearLayout row = new LinearLayout(this);

            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(
                    dp(12),dp(8),dp(12),dp(8)
            );
            row.setBackground(bg(Color.WHITE,14));

            LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(
                            -1,
                            dp(82)
                    );

            rp.setMargins(0,0,0,dp(8));

            list.addView(row,rp);

            TextView icon =
                    tv("👤",30,BLUE);

            icon.setGravity(Gravity.CENTER);

            row.addView(
                    icon,
                    new LinearLayout.LayoutParams(
                            dp(48),dp(62)
                    )
            );

            LinearLayout info = new LinearLayout(this);

            info.setOrientation(LinearLayout.VERTICAL);
            info.setGravity(Gravity.CENTER_VERTICAL);

            row.addView(
                    info,
                    new LinearLayout.LayoutParams(0,dp(62),1)
            );

            TextView name =
                    tv(
                            "Invited User #" + i,
                            15,
                            DARK
                    );

            name.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );

            info.addView(
                    name,
                    new LinearLayout.LayoutParams(-1,dp(30))
            );

            TextView status =
                    tv(
                            "Referral code: " + getInviteCode(),
                            12,
                            Color.GRAY
                    );

            info.addView(
                    status,
                    new LinearLayout.LayoutParams(-1,dp(25))
            );

            TextView amount =
                    tv(
                            "Pending",
                            13,
                            Color.rgb(190,120,0)
                    );

            amount.setGravity(Gravity.CENTER);

            row.addView(
                    amount,
                    new LinearLayout.LayoutParams(dp(78),dp(55))
            );
        }
    }


    /* =========================================================
       SPECIAL OFFERS
       ========================================================= */

    private JSONArray getSpecialOfferData() {
        String saved = pref.getString("special_offers", "");
        try {
            if (!saved.trim().isEmpty()) return new JSONArray(saved);
        } catch (Exception ignored) {}

        JSONArray defaults = new JSONArray();
        defaults.put(makeOffer("গ্রামীণফোন","","10 GB","100 মিনিট","","299","50","30 দিন","বিশেষ ইন্টারনেট ও মিনিট অফার"));
        defaults.put(makeOffer("গ্রামীণফোন","","5 GB","200 মিনিট","50 SMS","199","30","15 দিন","দৈনন্দিন ব্যবহারের অফার"));
        defaults.put(makeOffer("বাংলালিংক","","12 GB","100 মিনিট","","299","50","30 দিন","ডাটা + মিনিট প্যাক"));
        defaults.put(makeOffer("Airtel","","10 GB","150 মিনিট","","279","50","30 দিন","ডাটা ও মিনিট অফার"));
        defaults.put(makeOffer("Robi","","8 GB","200 মিনিট","","299","100","30 দিন","বোনাসসহ অফার"));
        defaults.put(makeOffer("Teletalk","","5 GB","100 মিনিট","","199","50","15 দিন","কম দামের প্যাক"));

        pref.edit().putString("special_offers", defaults.toString()).apply();
        return defaults;
    }

    private JSONObject makeOffer(String operator, String logo, String data, String minutes,
                                 String sms, String price, String bonus, String validity,
                                 String description) {
        JSONObject o = new JSONObject();
        try {
            o.put("operator", operator);
            o.put("logo", logo);
            o.put("data", data);
            o.put("minutes", minutes);
            o.put("sms", sms);
            o.put("price", price);
            o.put("bonus", bonus);
            o.put("validity", validity);
            o.put("description", description);
            o.put("visible", true);
        } catch (Exception ignored) {}
        return o;
    }

    private String offerText(JSONObject o, String key) {
        try { return o.optString(key, "").trim(); }
        catch (Exception e) { return ""; }
    }

    private boolean offerVisible(JSONObject o) {
        return o.optBoolean("visible", true);
    }

    private void showSpecialOffers() {
        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));
        setContentView(main);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);
        main.addView(header,new LinearLayout.LayoutParams(-1,dp(62)));

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);
        header.addView(back,new LinearLayout.LayoutParams(dp(52),dp(62)));
        back.setOnClickListener(v -> showHome());

        TextView title = tv("বিশেষ অফার",21,Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        header.addView(title,new LinearLayout.LayoutParams(0,dp(62),1));
        header.addView(new Space(this),new LinearLayout.LayoutParams(dp(52),dp(62)));

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(25));
        scroll.addView(content);
        main.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        TextView intro = tv("📱  সিমের বিশেষ অফার",18,DARK);
        intro.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        intro.setPadding(dp(4),dp(2),dp(4),dp(10));
        content.addView(intro,new LinearLayout.LayoutParams(-1,dp(45)));

        JSONArray offers = getSpecialOfferData();
        String[] operators = {"গ্রামীণফোন","বাংলালিংক","Airtel","Robi","Teletalk"};

        for (String operator : operators) {
            boolean hasOffer = false;
            for (int i=0;i<offers.length();i++) {
                JSONObject o=offers.optJSONObject(i);
                if (o!=null && offerVisible(o) && operator.equalsIgnoreCase(offerText(o,"operator"))) { hasOffer=true; break; }
            }
            if (!hasOffer) continue;

            TextView opTitle = tv(operator,18,BLUE);
            opTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
            opTitle.setPadding(dp(4),dp(8),dp(4),dp(8));
            content.addView(opTitle,new LinearLayout.LayoutParams(-1,dp(42)));

            for (int i=0;i<offers.length();i++) {
                JSONObject o=offers.optJSONObject(i);
                if (o==null || !offerVisible(o) || !operator.equalsIgnoreCase(offerText(o,"operator"))) continue;

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(15),dp(13),dp(15),dp(13));
                card.setBackground(bg(Color.WHITE,16));
                LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);
                cp.setMargins(0,0,0,dp(10));
                content.addView(card,cp);

                String logo=offerText(o,"logo");
                TextView name=tv((logo.isEmpty()?"📱  ":logo+"  ")+operator,16,DARK);
                name.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
                card.addView(name);

                String data=offerText(o,"data"), minutes=offerText(o,"minutes"), sms=offerText(o,"sms");
                if(!data.isEmpty()) { TextView x=tv("📶  "+data,15,DARK); x.setPadding(0,dp(9),0,0); card.addView(x); }
                if(!minutes.isEmpty()) { TextView x=tv("📞  "+minutes,15,DARK); x.setPadding(0,dp(5),0,0); card.addView(x); }
                if(!sms.isEmpty()) { TextView x=tv("✉  "+sms,15,DARK); x.setPadding(0,dp(5),0,0); card.addView(x); }

                String price=offerText(o,"price"), bonus=offerText(o,"bonus"), validity=offerText(o,"validity"), description=offerText(o,"description");
                if(!price.isEmpty()) { TextView x=tv("মূল্য: ৳ "+price,16,BLUE); x.setTypeface(Typeface.DEFAULT,Typeface.BOLD); x.setPadding(0,dp(9),0,0); card.addView(x); }
                if(!bonus.isEmpty() && !bonus.equals("0")) { TextView x=tv("🎁 বোনাস: ৳ "+bonus,15,GREEN); x.setTypeface(Typeface.DEFAULT,Typeface.BOLD); x.setPadding(0,dp(5),0,0); card.addView(x); }
                if(!validity.isEmpty()) { TextView x=tv("মেয়াদ: "+validity,14,Color.DKGRAY); x.setPadding(0,dp(5),0,0); card.addView(x); }
                if(!description.isEmpty()) { TextView x=tv(description,13,Color.GRAY); x.setPadding(0,dp(7),0,dp(7)); card.addView(x); }

                TextView take=button("অফার নিন",GREEN,Color.WHITE);
                card.addView(take,new LinearLayout.LayoutParams(-1,dp(50)));
                take.setOnClickListener(v -> {
                    String msg="অপারেটর: "+operator
                            +(data.isEmpty()?"":"\n"+data)
                            +(minutes.isEmpty()?"":"\n"+minutes)
                            +(sms.isEmpty()?"":"\n"+sms)
                            +(price.isEmpty()?"":"\nমূল্য: ৳ "+price)
                            +(bonus.isEmpty()||bonus.equals("0")?"":"\nবোনাস: ৳ "+bonus)
                            +(validity.isEmpty()?"":"\nমেয়াদ: "+validity);
                    new AlertDialog.Builder(this).setTitle("অফার").setMessage(msg)
                            .setNegativeButton("বন্ধ",null).setPositiveButton("ঠিক আছে",null).show();
                });
            }
        }
    }

    /* =========================================================
       CUSTOMER CARE
       ========================================================= */

    private String getCustomerCareWhatsApp() {
        return pref.getString("customer_care_whatsapp", "").trim();
    }

    private void showCustomerCare() {
        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);
        LinearLayout main=new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(247,248,250));
        setContentView(main);

        LinearLayout header=new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(dp(8),0,dp(8),0); header.setBackgroundColor(BLUE);
        main.addView(header,new LinearLayout.LayoutParams(-1,dp(62)));
        TextView back=tv("‹",38,Color.WHITE); back.setGravity(Gravity.CENTER);
        header.addView(back,new LinearLayout.LayoutParams(dp(52),dp(62))); back.setOnClickListener(v->showHome());
        TextView title=tv("কাস্টমার কেয়ার",21,Color.WHITE); title.setGravity(Gravity.CENTER); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        header.addView(title,new LinearLayout.LayoutParams(0,dp(62),1)); header.addView(new Space(this),new LinearLayout.LayoutParams(dp(52),dp(62)));

        LinearLayout content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setGravity(Gravity.TOP); content.setPadding(dp(16),dp(20),dp(16),dp(25));
        main.addView(content,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setGravity(Gravity.CENTER_HORIZONTAL); card.setPadding(dp(18),dp(25),dp(18),dp(25)); card.setBackground(bg(Color.WHITE,18));
        content.addView(card,new LinearLayout.LayoutParams(-1,-2));
        TextView icon=tv("💬",48,GREEN); icon.setGravity(Gravity.CENTER); card.addView(icon,new LinearLayout.LayoutParams(-1,dp(65)));
        TextView heading=tv("কাস্টমার কেয়ার",22,DARK); heading.setTypeface(Typeface.DEFAULT,Typeface.BOLD); heading.setGravity(Gravity.CENTER); card.addView(heading,new LinearLayout.LayoutParams(-1,dp(40)));

        String number=getCustomerCareWhatsApp();
        TextView numberText=tv(number.isEmpty()?"WhatsApp নম্বর এখনো সেট করা হয়নি":"WhatsApp: "+number,15,Color.DKGRAY);
        numberText.setGravity(Gravity.CENTER); numberText.setPadding(0,dp(8),0,dp(15)); card.addView(numberText,new LinearLayout.LayoutParams(-1,dp(55)));
        TextView contact=button("WhatsApp-এ যোগাযোগ করুন",GREEN,Color.WHITE);
        card.addView(contact,new LinearLayout.LayoutParams(-1,dp(55)));
        contact.setEnabled(!number.isEmpty());
        if(number.isEmpty()) contact.setBackground(bg(Color.rgb(170,170,170),12));
        contact.setOnClickListener(v->openCustomerCareWhatsApp(number));
    }

    private void openCustomerCareWhatsApp(String rawNumber) {
        String number=rawNumber.replaceAll("[^0-9]","");
        if(number.startsWith("00")) number=number.substring(2);
        if(number.startsWith("0")) number="88"+number;
        else if(!number.startsWith("88")) number="88"+number;
        try {
            android.content.Intent app=new android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse("whatsapp://send?phone="+number));
            startActivity(app);
        } catch(Exception e) {
            try {
                android.content.Intent web=new android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse("https://wa.me/"+number));
                startActivity(web);
            } catch(Exception ignored) {
                Toast.makeText(this,"WhatsApp খোলা যাচ্ছে না",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* =========================================================
       CUSTOMER REVIEWS
       ========================================================= */

    private JSONArray getCustomerReviewData() {
        String saved=pref.getString("customer_reviews","");
        try { if(!saved.trim().isEmpty()) return new JSONArray(saved); }
        catch(Exception ignored) {}
        return new JSONArray();
    }

    private void showCustomerReviews() {
        getWindow().setStatusBarColor(BLUE); getWindow().setNavigationBarColor(Color.WHITE);
        LinearLayout main=new LinearLayout(this); main.setOrientation(LinearLayout.VERTICAL); main.setBackgroundColor(Color.rgb(247,248,250)); setContentView(main);
        LinearLayout header=new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(dp(8),0,dp(8),0); header.setBackgroundColor(BLUE); main.addView(header,new LinearLayout.LayoutParams(-1,dp(62)));
        TextView back=tv("‹",38,Color.WHITE); back.setGravity(Gravity.CENTER); header.addView(back,new LinearLayout.LayoutParams(dp(52),dp(62))); back.setOnClickListener(v->showHome());
        TextView title=tv("কাস্টমার রিভিউ",21,Color.WHITE); title.setGravity(Gravity.CENTER); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); header.addView(title,new LinearLayout.LayoutParams(0,dp(62),1)); header.addView(new Space(this),new LinearLayout.LayoutParams(dp(52),dp(62)));
        ScrollView scroll=new ScrollView(this); LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(dp(12),dp(12),dp(12),dp(25)); scroll.addView(list); main.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        JSONArray reviews=getCustomerReviewData();
        if(reviews.length()==0) { addVideoEmptyState(list,"⭐","কাস্টমার রিভিউ","কাস্টমারদের ভিডিও রিভিউ এখানে দেখা যাবে।"); return; }
        for(int i=0;i<reviews.length();i++) { JSONObject item=reviews.optJSONObject(i); if(item==null||!item.optBoolean("visible",true)) continue; addVideoCard(list,item.optString("title","কাস্টমার রিভিউ"),item.optString("description",""),item.optString("url","")); }
    }

    /* =========================================================
       VIDEO TUTORIALS
       ========================================================= */

    private JSONArray getTutorialData() {
        String saved=pref.getString("video_tutorials","");
        try { if(!saved.trim().isEmpty()) return new JSONArray(saved); }
        catch(Exception ignored) {}
        return new JSONArray();
    }

    private void showVideoTutorials() {
        getWindow().setStatusBarColor(BLUE); getWindow().setNavigationBarColor(Color.WHITE);
        LinearLayout main=new LinearLayout(this); main.setOrientation(LinearLayout.VERTICAL); main.setBackgroundColor(Color.rgb(247,248,250)); setContentView(main);
        LinearLayout header=new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(dp(8),0,dp(8),0); header.setBackgroundColor(BLUE); main.addView(header,new LinearLayout.LayoutParams(-1,dp(62)));
        TextView back=tv("‹",38,Color.WHITE); back.setGravity(Gravity.CENTER); header.addView(back,new LinearLayout.LayoutParams(dp(52),dp(62))); back.setOnClickListener(v->showHome());
        TextView title=tv("ভিডিও টিউটোরিয়াল",21,Color.WHITE); title.setGravity(Gravity.CENTER); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); header.addView(title,new LinearLayout.LayoutParams(0,dp(62),1)); header.addView(new Space(this),new LinearLayout.LayoutParams(dp(52),dp(62)));
        ScrollView scroll=new ScrollView(this); LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(dp(12),dp(12),dp(12),dp(25)); scroll.addView(list); main.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        JSONArray tutorials=getTutorialData();
        if(tutorials.length()==0) { addVideoEmptyState(list,"▶","ভিডিও টিউটোরিয়াল","অ্যাপ ব্যবহার শেখানোর ভিডিও এখানে দেখা যাবে।"); return; }
        for(int i=0;i<tutorials.length();i++) { JSONObject item=tutorials.optJSONObject(i); if(item==null||!item.optBoolean("visible",true)) continue; addVideoCard(list,item.optString("title","ভিডিও টিউটোরিয়াল"),item.optString("description",""),item.optString("url","")); }
    }

    private void addVideoEmptyState(LinearLayout parent,String iconText,String titleText,String messageText) {
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setGravity(Gravity.CENTER); card.setPadding(dp(20),dp(25),dp(20),dp(25)); card.setBackground(bg(Color.WHITE,16)); parent.addView(card,new LinearLayout.LayoutParams(-1,dp(210)));
        TextView icon=tv(iconText,44,DARK); icon.setGravity(Gravity.CENTER); card.addView(icon,new LinearLayout.LayoutParams(-1,dp(58)));
        TextView title=tv(titleText,18,DARK); title.setGravity(Gravity.CENTER); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); card.addView(title,new LinearLayout.LayoutParams(-1,dp(38)));
        TextView message=tv(messageText,14,Color.GRAY); message.setGravity(Gravity.CENTER); card.addView(message,new LinearLayout.LayoutParams(-1,dp(55)));
    }

    private void addVideoCard(LinearLayout parent,String titleText,String descriptionText,String url) {
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(15),dp(14),dp(15),dp(14)); card.setBackground(bg(Color.WHITE,16));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.setMargins(0,0,0,dp(10)); parent.addView(card,cp);
        TextView play=tv("▶",34,BLUE); play.setGravity(Gravity.CENTER); card.addView(play,new LinearLayout.LayoutParams(-1,dp(62)));
        TextView title=tv(titleText,17,DARK); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); card.addView(title,new LinearLayout.LayoutParams(-1,dp(32)));
        if(!descriptionText.trim().isEmpty()) { TextView description=tv(descriptionText,13,Color.GRAY); description.setPadding(0,dp(4),0,dp(9)); card.addView(description,new LinearLayout.LayoutParams(-1,-2)); }
        TextView watch=button("ভিডিও দেখুন",BLUE,Color.WHITE); card.addView(watch,new LinearLayout.LayoutParams(-1,dp(50))); watch.setEnabled(!url.trim().isEmpty()); if(url.trim().isEmpty()) watch.setBackground(bg(Color.rgb(170,170,170),12)); watch.setOnClickListener(v->openVideoUrl(url));
    }

    private void openVideoUrl(String url) {
        if(url==null||url.trim().isEmpty()) { Toast.makeText(this,"ভিডিও লিংক এখনো সেট করা হয়নি",Toast.LENGTH_SHORT).show(); return; }
        try { startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW,android.net.Uri.parse(url.trim()))); }
        catch(Exception e) { Toast.makeText(this,"ভিডিও খোলা যাচ্ছে না",Toast.LENGTH_SHORT).show(); }
    }

    /* =========================================================
       GROUP CHAT
       ========================================================= */

    private void showGroupChat() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(5),0,dp(5),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        LinearLayout titleBox = new LinearLayout(this);

        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setGravity(Gravity.CENTER_VERTICAL);

        header.addView(
                titleBox,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView title = tv("গ্রুপ চ্যাট",20,Color.WHITE);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        titleBox.addView(
                title,
                new LinearLayout.LayoutParams(-1,dp(34))
        );

        TextView online = tv("Quick Pay Community",11,Color.WHITE);

        titleBox.addView(
                online,
                new LinearLayout.LayoutParams(-1,dp(22))
        );

        TextView clear = tv("⋮",30,Color.WHITE);
        clear.setGravity(Gravity.CENTER);

        header.addView(
                clear,
                new LinearLayout.LayoutParams(dp(48),dp(62))
        );

        clear.setOnClickListener(v -> showChatMenu());

        ScrollView scroll = new ScrollView(this);

        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(248,249,251));

        LinearLayout chatBox = new LinearLayout(this);

        chatBox.setOrientation(LinearLayout.VERTICAL);
        chatBox.setPadding(
                dp(10),dp(12),dp(10),dp(12)
        );

        scroll.addView(chatBox);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        loadChatMessages(chatBox,scroll);

        LinearLayout inputArea = new LinearLayout(this);

        inputArea.setGravity(Gravity.CENTER_VERTICAL);
        inputArea.setPadding(dp(8),dp(7),dp(8),dp(7));
        inputArea.setBackgroundColor(Color.WHITE);

        main.addView(
                inputArea,
                new LinearLayout.LayoutParams(-1,dp(70))
        );

        EditText message = new EditText(this);

        message.setHint("মেসেজ লিখুন...");
        message.setTextSize(16);
        message.setSingleLine(false);
        message.setMaxLines(3);
        message.setTextColor(DARK);
        message.setHintTextColor(Color.GRAY);
        message.setPadding(dp(15),0,dp(15),0);

        message.setBackground(
                outline(
                        Color.rgb(247,249,252),
                        Color.rgb(215,222,232),
                        25
                )
        );

        inputArea.addView(
                message,
                new LinearLayout.LayoutParams(0,dp(54),1)
        );

        TextView send = tv("➤",24,Color.WHITE);

        send.setGravity(Gravity.CENTER);
        send.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        send.setBackground(bg(BLUE,50));

        LinearLayout.LayoutParams sendParams =
                new LinearLayout.LayoutParams(dp(54),dp(54));

        sendParams.leftMargin = dp(7);

        inputArea.addView(send,sendParams);

        send.setOnClickListener(v -> {

            String text =
                    message.getText()
                            .toString()
                            .trim();

            if (text.isEmpty()) {
                return;
            }

            saveChatMessage(text);

            message.setText("");

            loadChatMessages(chatBox,scroll);
        });

        message.setOnEditorActionListener((v,actionId,event) -> {

            String text =
                    message.getText()
                            .toString()
                            .trim();

            if (!text.isEmpty()) {

                saveChatMessage(text);
                message.setText("");
                loadChatMessages(chatBox,scroll);

                return true;
            }

            return false;
        });
    }

    /* =========================================================
       CHAT SAVE
       ========================================================= */

    private void saveChatMessage(String text) {

        try {

            String old =
                    pref.getString(
                            "group_chat_messages",
                            "[]"
                    );

            JSONArray array = new JSONArray(old);

            JSONObject object = new JSONObject();

            object.put(
                    "name",
                    pref.getString("name","Rosy")
            );

            object.put("message",text);

            object.put(
                    "time",
                    new SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                    ).format(new Date())
            );

            array.put(object);

            pref.edit()
                    .putString(
                            "group_chat_messages",
                            array.toString()
                    )
                    .apply();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "মেসেজ সেভ করা যায়নি",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /* =========================================================
       LOAD CHAT
       ========================================================= */

    private void loadChatMessages(
            LinearLayout chatBox,
            ScrollView scroll) {

        chatBox.removeAllViews();

        try {

            String data =
                    pref.getString(
                            "group_chat_messages",
                            "[]"
                    );

            JSONArray array = new JSONArray(data);

            if (array.length() == 0) {

                LinearLayout empty =
                        new LinearLayout(this);

                empty.setOrientation(
                        LinearLayout.VERTICAL
                );

                empty.setGravity(Gravity.CENTER);
                empty.setPadding(dp(20),dp(60),dp(20),dp(60));

                TextView icon = tv("💬",48,BLUE);
                icon.setGravity(Gravity.CENTER);

                empty.addView(
                        icon,
                        new LinearLayout.LayoutParams(
                                -1,
                                dp(70)
                        )
                );

                TextView title =
                        tv(
                                "গ্রুপ চ্যাটে স্বাগতম",
                                21,
                                BLUE
                        );

                title.setGravity(Gravity.CENTER);
                title.setTypeface(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                );

                empty.addView(
                        title,
                        new LinearLayout.LayoutParams(
                                -1,
                                dp(40)
                        )
                );

                TextView sub =
                        tv(
                                "প্রথম মেসেজটি আপনিই পাঠান।",
                                15,
                                Color.DKGRAY
                        );

                sub.setGravity(Gravity.CENTER);

                empty.addView(
                        sub,
                        new LinearLayout.LayoutParams(
                                -1,
                                dp(35)
                        )
                );

                chatBox.addView(
                        empty,
                        new LinearLayout.LayoutParams(
                                -1,
                                -2
                        )
                );

            } else {

                for (int i = 0; i < array.length(); i++) {

                    JSONObject object =
                            array.getJSONObject(i);

                    String name =
                            object.optString(
                                    "name",
                                    "User"
                            );

                    String message =
                            object.optString(
                                    "message",
                                    ""
                            );

                    String time =
                            object.optString(
                                    "time",
                                    ""
                            );

                    addChatMessage(
                            chatBox,
                            name,
                            message,
                            time
                    );
                }
            }

        } catch (Exception e) {

            TextView error =
                    tv(
                            "চ্যাট লোড করা যায়নি",
                            16,
                            Color.RED
                    );

            error.setGravity(Gravity.CENTER);

            chatBox.addView(
                    error,
                    new LinearLayout.LayoutParams(-1,dp(60))
            );
        }

        chatBox.postDelayed(
                () -> scroll.fullScroll(ScrollView.FOCUS_DOWN),
                100
        );
    }

    /* =========================================================
       CHAT MESSAGE DESIGN
       ========================================================= */

    private void addChatMessage(
            LinearLayout parent,
            String name,
            String message,
            String time) {

        String currentName =
                pref.getString("name","Rosy");

        boolean mine =
                name.equals(currentName);

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                mine
                        ? Gravity.RIGHT
                        : Gravity.LEFT
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        rowParams.setMargins(
                dp(2),dp(4),dp(2),dp(4)
        );

        parent.addView(row,rowParams);

        LinearLayout bubble =
                new LinearLayout(this);

        bubble.setOrientation(
                LinearLayout.VERTICAL
        );

        bubble.setPadding(
                dp(13),
                dp(8),
                dp(13),
                dp(8)
        );

        if (mine) {

            bubble.setBackground(
                    bg(
                            Color.rgb(225,240,255),
                            16
                    )
            );

        } else {

            bubble.setBackground(
                    bg(
                            Color.WHITE,
                            16
                    )
            );
        }

        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        dp(285),
                        -2
                );

        row.addView(bubble,bubbleParams);

        TextView user =
                tv(
                        mine
                                ? "আপনি"
                                : name,
                        13,
                        mine ? BLUE : GREEN
                );

        user.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        bubble.addView(
                user,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(24)
                )
        );

        TextView text =
                tv(
                        message,
                        16,
                        DARK
                );

        text.setPadding(0,dp(2),0,dp(2));
        text.setGravity(Gravity.LEFT);

        bubble.addView(
                text,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView clock =
                tv(
                        time,
                        10,
                        Color.GRAY
                );

        clock.setGravity(
                mine
                        ? Gravity.RIGHT
                        : Gravity.LEFT
        );

        bubble.addView(
                clock,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(20)
                )
        );
    }

    /* =========================================================
       CHAT MENU
       ========================================================= */

    private void showChatMenu() {

        String[] options = {
                "চ্যাট পরিষ্কার করুন",
                "চ্যাট সম্পর্কে"
        };

        new AlertDialog.Builder(this)
                .setTitle("গ্রুপ চ্যাট")
                .setItems(
                        options,
                        (dialog,which) -> {

                            if (which == 0) {

                                new AlertDialog.Builder(this)
                                        .setTitle(
                                                "চ্যাট পরিষ্কার করবেন?"
                                        )
                                        .setMessage(
                                                "এই ফোনে সংরক্ষিত সব গ্রুপ চ্যাট মুছে যাবে।"
                                        )
                                        .setNegativeButton(
                                                "বাতিল",
                                                null
                                        )
                                        .setPositiveButton(
                                                "মুছে ফেলুন",
                                                (d,w) -> {

                                                    pref.edit()
                                                            .remove(
                                                                    "group_chat_messages"
                                                            )
                                                            .apply();

                                                    showGroupChat();

                                                    Toast.makeText(
                                                            this,
                                                            "চ্যাট পরিষ্কার করা হয়েছে",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                        )
                                        .show();

                            } else {

                                new AlertDialog.Builder(this)
                                        .setTitle(
                                                "গ্রুপ চ্যাট"
                                        )
                                        .setMessage(
                                                "এখানে Quick Pay ব্যবহারকারীরা গ্রুপে কথা বলতে পারবে।\n\n"
                                                        + "বর্তমান ভার্সনে মেসেজ এই ডিভাইসে লোকালি সংরক্ষণ করা হচ্ছে।\n\n"
                                                        + "পরবর্তীতে Backend / API / Socket যুক্ত করলে একাধিক ব্যবহারকারীর মধ্যে লাইভ চ্যাট করা যাবে।"
                                        )
                                        .setPositiveButton(
                                                "ঠিক আছে",
                                                null
                                        )
                                        .show();
                            }
                        }
                )
                .show();
    }

    /* =========================================================
       ADD BALANCE
       ========================================================= */

    private void showAddBalance() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.WHITE);

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title = tv("অ্যাড ব্যালেন্স",21,Color.WHITE);

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView info =
                tv(
                        "আপনার Quick Pay ওয়ালেটে টাকা যোগ করুন।\n\n"
                                + "টাকা যোগ করার জন্য নিচের অটো ডিপোজিট অপশন ব্যবহার করুন।",
                        17,
                        DARK
                );

        info.setPadding(dp(28),dp(35),dp(28),dp(20));

        main.addView(
                info,
                new LinearLayout.LayoutParams(-1,dp(150))
        );

        TextView auto =
                button(
                        "অটো ডিপোজিট",
                        BLUE,
                        Color.WHITE
                );

        LinearLayout.LayoutParams ap =
                new LinearLayout.LayoutParams(-1,dp(60));

        ap.setMargins(dp(20),dp(10),dp(20),0);

        main.addView(auto,ap);

        auto.setOnClickListener(v -> showAutoDeposit());
    }

    /* =========================================================
       AUTO DEPOSIT
       ========================================================= */

    private void showAutoDeposit() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(root);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        root.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showAddBalance());

        TextView title = tv("অটো ডিপোজিট",21,Color.WHITE);

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14),dp(15),dp(14),dp(20));

        scroll.addView(content);

        root.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        TextView notice =
                tv(
                        "নিচের যেকোনো নম্বরে টাকা পাঠান।\n"
                                + "টাকা পাঠানোর পর সঠিক টাকার পরিমাণ ও Transaction ID দিয়ে সাবমিট করুন।",
                        16,
                        DARK
                );

        notice.setPadding(dp(18),dp(18),dp(18),dp(18));
        notice.setBackground(bg(Color.WHITE,18));

        content.addView(
                notice,
                new LinearLayout.LayoutParams(-1,dp(105))
        );

        space(content,12);

        addDepositProvider(
                content,
                "bKash",
                "বিকাশ পার্সোনাল",
                ""
        );

        addDepositProvider(
                content,
                "Nagad",
                "নগদ পার্সোনাল",
                ""
        );

        addDepositProvider(
                content,
                "Rocket",
                "রকেট পার্সোনাল",
                ""
        );

        addDepositProvider(
                content,
                "Upay",
                "উপায় পার্সোনাল",
                ""
        );

        space(content,12);

        TextView amountTitle = tv("টাকার পরিমাণ",16,BLUE);

        amountTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                amountTitle,
                new LinearLayout.LayoutParams(-1,dp(32))
        );

        EditText amount = input("টাকার পরিমাণ",false);

        amount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        content.addView(
                amount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(content,12);

        TextView trxTitle = tv("Transaction ID",16,BLUE);

        trxTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                trxTitle,
                new LinearLayout.LayoutParams(-1,dp(32))
        );

        EditText trx = input("Transaction ID লিখুন",false);

        trx.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        );

        content.addView(
                trx,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(content,18);

        TextView submit =
                button(
                        "ডিপোজিট সাবমিট করুন",
                        BLUE,
                        Color.WHITE
                );

        content.addView(
                submit,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        submit.setOnClickListener(v -> {

            String money =
                    amount.getText().toString().trim();

            String transaction =
                    trx.getText().toString().trim();

            if (money.isEmpty()) {
                amount.setError("টাকার পরিমাণ দিন");
                return;
            }

            if (transaction.isEmpty()) {
                trx.setError("Transaction ID দিন");
                return;
            }

            try {

                double value =
                        Double.parseDouble(money);

                if (value < 50) {
                    amount.setError("সর্বনিম্ন ৫০ টাকা");
                    return;
                }

            } catch (Exception e) {

                amount.setError("সঠিক টাকার পরিমাণ দিন");
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("ডিপোজিট সাবমিট")
                    .setMessage(
                            "টাকার পরিমাণ: ৳ "
                                    + money
                                    + "\nTransaction ID: "
                                    + transaction
                                    + "\n\nআপনার ডিপোজিট রিকোয়েস্ট সাবমিট করা হবে।"
                    )
                    .setNegativeButton("বাতিল",null)
                    .setPositiveButton(
                            "সাবমিট",
                            (dialog,which) -> {

                                Toast.makeText(
                                        this,
                                        "ডিপোজিট রিকোয়েস্ট সাবমিট হয়েছে। অ্যাডমিন ভেরিফাই করার পর ব্যালেন্স যোগ হবে।",
                                        Toast.LENGTH_LONG
                                ).show();

                                amount.setText("");
                                trx.setText("");
                            }
                    )
                    .show();
        });
    }

    /* =========================================================
       DEPOSIT PROVIDER
       ========================================================= */

    private void addDepositProvider(
            LinearLayout parent,
            String name,
            String subtitle,
            String number) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12),dp(7),dp(12),dp(7));
        card.setBackground(bg(Color.WHITE,14));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1,dp(86));

        cardParams.setMargins(0,0,0,dp(7));

        parent.addView(card,cardParams);

        TextView title =
                tv(
                        name + "  •  " + subtitle,
                        17,
                        BLUE
                );

        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                title,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        LinearLayout row = new LinearLayout(this);

        row.setGravity(Gravity.CENTER_VERTICAL);

        card.addView(
                row,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        TextView phone =
                tv(
                        number.isEmpty()
                                ? "নম্বর এখনো সেট করা হয়নি"
                                : number,
                        15,
                        number.isEmpty()
                                ? Color.GRAY
                                : DARK
                );

        phone.setGravity(Gravity.CENTER_VERTICAL);

        row.addView(
                phone,
                new LinearLayout.LayoutParams(0,dp(40),1)
        );

        TextView copy =
                button(
                        "কপি",
                        BLUE,
                        Color.WHITE
                );

        row.addView(
                copy,
                new LinearLayout.LayoutParams(dp(68),dp(38))
        );

        copy.setOnClickListener(v -> {

            if (number.isEmpty()) {

                Toast.makeText(
                        this,
                        "নম্বর এখনো সেট করা হয়নি",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager)
                            getSystemService(
                                    Context.CLIPBOARD_SERVICE
                            );

            clipboard.setPrimaryClip(
                    android.content.ClipData.newPlainText(
                            "Company Number",
                            number
                    )
            );

            Toast.makeText(
                    this,
                    name + " নম্বর কপি হয়েছে",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    /* =========================================================
       BANK TRANSFER
       ========================================================= */

    private void showBankTransfer() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title = tv("ব্যাংক ট্রান্সফার",21,Color.WHITE);

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView bankIcon = tv("🏦",21,Color.WHITE);
        bankIcon.setGravity(Gravity.CENTER);

        header.addView(
                bankIcon,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(25));

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        TextView info =
                tv(
                        "🏦 ব্যাংক অ্যাকাউন্টে টাকা পাঠান\n\n"
                                + "নিচের তথ্যগুলো সঠিকভাবে পূরণ করে ট্রান্সফার রিকোয়েস্ট পাঠান।",
                        16,
                        DARK
                );

        info.setPadding(dp(18),dp(18),dp(18),dp(18));
        info.setBackground(bg(Color.WHITE,18));

        content.addView(
                info,
                new LinearLayout.LayoutParams(-1,dp(125))
        );

        space(content,12);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16),dp(16),dp(16),dp(20));
        card.setBackground(bg(Color.WHITE,18));

        content.addView(
                card,
                new LinearLayout.LayoutParams(-1,-2)
        );

        TextView bankTitle = tv("ব্যাংকের নাম",15,DARK);
        bankTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                bankTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText bank = input("ব্যাংকের নাম লিখুন",false);

        card.addView(
                bank,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView holderTitle =
                tv("অ্যাকাউন্ট হোল্ডারের নাম",15,DARK);

        holderTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                holderTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText holder =
                input("অ্যাকাউন্ট হোল্ডারের নাম",false);

        card.addView(
                holder,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView accountTitle =
                tv("অ্যাকাউন্ট নম্বর",15,DARK);

        accountTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                accountTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText account =
                input("ব্যাংক অ্যাকাউন্ট নম্বর",false);

        account.setInputType(InputType.TYPE_CLASS_NUMBER);

        card.addView(
                account,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView branchTitle =
                tv("শাখার নাম",15,DARK);

        branchTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                branchTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText branch =
                input("শাখার নাম (ঐচ্ছিক)",false);

        card.addView(
                branch,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView amountTitle =
                tv("ট্রান্সফারের পরিমাণ",15,DARK);

        amountTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                amountTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText amount =
                input("টাকার পরিমাণ",false);

        amount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        card.addView(
                amount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView referenceTitle =
                tv("রেফারেন্স",15,DARK);

        referenceTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                referenceTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText reference =
                input("রেফারেন্স (ঐচ্ছিক)",false);

        card.addView(
                reference,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,18);

        TextView transfer =
                button(
                        "ব্যাংক ট্রান্সফার করুন  →",
                        BLUE,
                        Color.WHITE
                );

        card.addView(
                transfer,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        transfer.setOnClickListener(v -> {

            String bankName =
                    bank.getText().toString().trim();

            String holderName =
                    holder.getText().toString().trim();

            String accountNumber =
                    account.getText().toString().trim();

            String amountValue =
                    amount.getText().toString().trim();

            String branchName =
                    branch.getText().toString().trim();

            String referenceValue =
                    reference.getText().toString().trim();

            if (bankName.isEmpty()) {
                bank.setError("ব্যাংকের নাম দিন");
                return;
            }

            if (holderName.isEmpty()) {
                holder.setError("অ্যাকাউন্ট হোল্ডারের নাম দিন");
                return;
            }

            if (accountNumber.isEmpty()) {
                account.setError("অ্যাকাউন্ট নম্বর দিন");
                return;
            }

            if (accountNumber.length() < 8) {
                account.setError("সঠিক অ্যাকাউন্ট নম্বর দিন");
                return;
            }

            if (amountValue.isEmpty()) {
                amount.setError("টাকার পরিমাণ দিন");
                return;
            }

            try {

                double value =
                        Double.parseDouble(amountValue);

                if (value < 500) {
                    amount.setError("সর্বনিম্ন ৫০০ টাকা");
                    return;
                }

                String message =
                        "ব্যাংক: "
                                + bankName
                                + "\n\nঅ্যাকাউন্ট হোল্ডার: "
                                + holderName
                                + "\n\nঅ্যাকাউন্ট নম্বর: "
                                + accountNumber
                                + "\n\nশাখা: "
                                + (
                                branchName.isEmpty()
                                        ? "দেওয়া হয়নি"
                                        : branchName
                        )
                                + "\n\nপরিমাণ: ৳ "
                                + amountValue
                                + "\n\n"
                                + (
                                referenceValue.isEmpty()
                                        ? ""
                                        : "রেফারেন্স: "
                                        + referenceValue
                                        + "\n\n"
                        )
                                + "ট্রান্সফার তথ্যগুলো সঠিক কিনা যাচাই করুন।";

                new AlertDialog.Builder(this)
                        .setTitle("ব্যাংক ট্রান্সফার নিশ্চিত করুন")
                        .setMessage(message)
                        .setNegativeButton("বাতিল",null)
                        .setPositiveButton(
                                "নিশ্চিত",
                                (dialog,which) -> {

                                    Toast.makeText(
                                            this,
                                            "ব্যাংক ট্রান্সফার রিকোয়েস্ট গ্রহণ করা হয়েছে।",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    bank.setText("");
                                    holder.setText("");
                                    account.setText("");
                                    branch.setText("");
                                    amount.setText("");
                                    reference.setText("");
                                }
                        )
                        .show();

            } catch (Exception e) {

                amount.setError("সঠিক টাকার পরিমাণ দিন");
            }
        });
    }

    /* =========================================================
       MOBILE BANKING
       ========================================================= */

    private void showMobileBanking() {
        showMobileBanking(selectedMobileProvider);
    }

    private void showMobileBanking(String provider) {

        selectedMobileProvider = provider;

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title = tv("মোবাইল ব্যাংকিং",21,Color.WHITE);

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView bell = tv("🔔",19,Color.WHITE);
        bell.setGravity(Gravity.CENTER);

        header.addView(
                bell,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(20));

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        TextView providerTitle =
                tv(
                        "মোবাইল ব্যাংকিং নির্বাচন করুন",
                        16,
                        DARK
                );

        providerTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        providerTitle.setPadding(dp(4),dp(2),dp(4),dp(7));

        content.addView(
                providerTitle,
                new LinearLayout.LayoutParams(-1,dp(34))
        );

        LinearLayout providers = new LinearLayout(this);
        providers.setGravity(Gravity.CENTER);

        content.addView(
                providers,
                new LinearLayout.LayoutParams(-1,dp(78))
        );

        addMobileProviderTab(providers,"বিকাশ","💗","বিকাশ");
        addMobileProviderTab(providers,"নগদ","🟠","নগদ");
        addMobileProviderTab(providers,"রকেট","🟣","রকেট");
        addMobileProviderTab(providers,"উপায়","🟡","উপায়");

        space(content,10);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16),dp(15),dp(16),dp(18));
        card.setBackground(bg(Color.WHITE,18));

        content.addView(
                card,
                new LinearLayout.LayoutParams(-1,-2)
        );

        TextView selected =
                tv(
                        "✓ " + selectedMobileProvider,
                        20,
                        BLUE
                );

        selected.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        selected.setGravity(Gravity.CENTER_VERTICAL);

        card.addView(
                selected,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        TextView accountTitle =
                tv("একাউন্ট ধরন",15,DARK);

        accountTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                accountTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        LinearLayout accountRow = new LinearLayout(this);
        accountRow.setGravity(Gravity.CENTER);

        card.addView(
                accountRow,
                new LinearLayout.LayoutParams(-1,dp(52))
        );

        TextView personal =
                mobileChoiceButton(
                        "পার্সোনাল",
                        selectedAccountType.equals("পার্সোনাল")
                );

        TextView agent =
                mobileChoiceButton(
                        "এজেন্ট",
                        selectedAccountType.equals("এজেন্ট")
                );

        accountRow.addView(
                personal,
                new LinearLayout.LayoutParams(0,dp(48),1)
        );

        LinearLayout.LayoutParams agentParams =
                new LinearLayout.LayoutParams(0,dp(48),1);

        agentParams.leftMargin = dp(8);

        accountRow.addView(agent,agentParams);

        personal.setOnClickListener(v -> {

            selectedAccountType = "পার্সোনাল";
            showMobileBanking(selectedMobileProvider);
        });

        agent.setOnClickListener(v -> {

            selectedAccountType = "এজেন্ট";
            showMobileBanking(selectedMobileProvider);
        });

        space(card,12);

        TextView numberTitle =
                tv("মোবাইল নম্বর",15,DARK);

        numberTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                numberTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        LinearLayout phoneBox = new LinearLayout(this);

        phoneBox.setGravity(Gravity.CENTER_VERTICAL);

        phoneBox.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        12
                )
        );

        card.addView(
                phoneBox,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        TextView country = tv("+88",17,BLUE);

        country.setGravity(Gravity.CENTER);
        country.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        phoneBox.addView(
                country,
                new LinearLayout.LayoutParams(dp(55),dp(58))
        );

        EditText number = new EditText(this);

        number.setHint("মোবাইল নম্বর লিখুন");
        number.setTextSize(17);
        number.setSingleLine(true);
        number.setTextColor(DARK);
        number.setHintTextColor(Color.GRAY);
        number.setInputType(InputType.TYPE_CLASS_PHONE);
        number.setPadding(dp(8),0,dp(12),0);
        number.setBackgroundColor(Color.TRANSPARENT);

        phoneBox.addView(
                number,
                new LinearLayout.LayoutParams(0,dp(58),1)
        );

        space(card,12);

        TextView amountTitle =
                tv("পরিমাণ লিখুন",15,DARK);

        amountTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                amountTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText amount = new EditText(this);

        amount.setHint("টাকার পরিমাণ লিখুন");
        amount.setTextSize(17);
        amount.setSingleLine(true);
        amount.setTextColor(DARK);
        amount.setHintTextColor(Color.GRAY);

        amount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        amount.setPadding(dp(18),0,dp(18),0);

        amount.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        12
                )
        );

        card.addView(
                amount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,10);

        TextView quickTitle =
                tv(
                        "দ্রুত পরিমাণ নির্বাচন করুন",
                        14,
                        Color.DKGRAY
                );

        card.addView(
                quickTitle,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        LinearLayout quickRow = new LinearLayout(this);
        quickRow.setGravity(Gravity.CENTER);

        card.addView(
                quickRow,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        addQuickAmount(quickRow,amount,"৳ 1,000");
        addQuickAmount(quickRow,amount,"৳ 10,000");
        addQuickAmount(quickRow,amount,"৳ 20,000");
        addQuickAmount(quickRow,amount,"৳ 50,000");

        space(card,16);

        TextView send =
                button(
                        "টাকা পাঠান  →",
                        BLUE,
                        Color.WHITE
                );

        card.addView(
                send,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        send.setOnClickListener(v -> {

            String num =
                    number.getText()
                            .toString()
                            .trim()
                            .replace(" ","")
                            .replace("-","");

            String money =
                    amount.getText()
                            .toString()
                            .trim();

            if (num.isEmpty()) {
                number.setError("মোবাইল নম্বর দিন");
                return;
            }

            if (num.startsWith("+88")) {
                num = num.substring(3);
            }

            if (!num.matches("01[0-9]{9}")) {
                number.setError(
                        "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                );
                return;
            }

            if (money.isEmpty()) {
                amount.setError("টাকার পরিমাণ দিন");
                return;
            }

            try {

                double value =
                        Double.parseDouble(money);

                if (value < 500) {
                    amount.setError("সর্বনিম্ন ৫০০ টাকা");
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("টাকা পাঠানো নিশ্চিত করুন")
                        .setMessage(
                                "মাধ্যম: "
                                        + selectedMobileProvider
                                        + "\nএকাউন্ট: "
                                        + selectedAccountType
                                        + "\nনম্বর: +88 "
                                        + num
                                        + "\nপরিমাণ: ৳ "
                                        + money
                                        + "\n\n"
                                        + "এটি একটি ডেমো রিকোয়েস্ট। আসল টাকা পাঠানোর জন্য Provider API সংযুক্ত করতে হবে।"
                        )
                        .setNegativeButton("বাতিল",null)
                        .setPositiveButton(
                                "নিশ্চিত",
                                (dialog,which) -> {

                                    Toast.makeText(
                                            this,
                                            selectedMobileProvider
                                                    + " টাকা পাঠানোর রিকোয়েস্ট গ্রহণ করা হয়েছে।",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    number.setText("");
                                    amount.setText("");
                                }
                        )
                        .show();

            } catch(Exception e) {

                amount.setError("সঠিক টাকার পরিমাণ দিন");
            }
        });
    }

    /* =========================================================
       MOBILE PROVIDER TAB
       ========================================================= */

    private void addMobileProviderTab(
            LinearLayout parent,
            String name,
            String icon,
            String provider) {

        boolean selected =
                selectedMobileProvider.equals(provider);

        LinearLayout box = new LinearLayout(this);

        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        if (selected) {

            box.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            13
                    )
            );

        } else {

            box.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(220,225,232),
                            13
                    )
            );
        }

        TextView i = tv(icon,22,DARK);
        i.setGravity(Gravity.CENTER);

        box.addView(
                i,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        TextView n =
                tv(
                        name,
                        13,
                        selected ? BLUE : DARK
                );

        n.setGravity(Gravity.CENTER);
        n.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        box.addView(
                n,
                new LinearLayout.LayoutParams(-1,dp(27))
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,dp(72),1);

        p.setMargins(dp(3),0,dp(3),0);

        parent.addView(box,p);

        box.setOnClickListener(v -> {

            selectedMobileProvider = provider;
            showMobileBanking(provider);
        });
    }

    /* =========================================================
       ACCOUNT TYPE
       ========================================================= */

    private TextView mobileChoiceButton(
            String text,
            boolean selected) {

        TextView b =
                tv(
                        text,
                        15,
                        selected ? BLUE : Color.DKGRAY
                );

        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        if (selected) {

            b.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            12
                    )
            );

        } else {

            b.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(215,220,228),
                            12
                    )
            );
        }

        return b;
    }

    /* =========================================================
       QUICK AMOUNT
       ========================================================= */

    private void addQuickAmount(
            LinearLayout parent,
            EditText amount,
            String value) {

        TextView b = tv(value,12,BLUE);

        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        b.setBackground(
                outline(
                        Color.WHITE,
                        BLUE,
                        9
                )
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,dp(42),1);

        p.setMargins(dp(2),0,dp(2),0);

        parent.addView(b,p);

        b.setOnClickListener(v -> {

            String clean =
                    value
                            .replace("৳","")
                            .replace(",","")
                            .trim();

            amount.setText(clean);
            amount.setSelection(amount.length());
        });
    }

    /* =========================================================
       MOBILE RECHARGE
       ========================================================= */

    private void showMobileRecharge() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout main = new LinearLayout(this);

        main.setOrientation(LinearLayout.VERTICAL);
        main.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(main);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        main.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showHome());

        TextView title =
                tv(
                        "মোবাইল রিচার্জ",
                        21,
                        Color.WHITE
                );

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        TextView bell = tv("🔔",19,Color.WHITE);
        bell.setGravity(Gravity.CENTER);

        header.addView(
                bell,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);

        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12),dp(12),dp(12),dp(20));

        scroll.addView(content);

        main.addView(
                scroll,
                new LinearLayout.LayoutParams(-1,0,1)
        );

        TextView operatorTitle =
                tv(
                        "অপারেটর নির্বাচন করুন",
                        16,
                        DARK
                );

        operatorTitle.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        content.addView(
                operatorTitle,
                new LinearLayout.LayoutParams(-1,dp(36))
        );

        LinearLayout opRow1 = new LinearLayout(this);
        opRow1.setGravity(Gravity.CENTER);

        content.addView(
                opRow1,
                new LinearLayout.LayoutParams(-1,dp(72))
        );

        addRechargeOperator(opRow1,"GP","🟢","GP");
        addRechargeOperator(opRow1,"Robi","🔴","Robi");
        addRechargeOperator(opRow1,"Airtel","🔵","Airtel");

        LinearLayout opRow2 = new LinearLayout(this);
        opRow2.setGravity(Gravity.CENTER);

        content.addView(
                opRow2,
                new LinearLayout.LayoutParams(-1,dp(72))
        );

        addRechargeOperator(
                opRow2,
                "Banglalink",
                "🟠",
                "Banglalink"
        );

        addRechargeOperator(
                opRow2,
                "Teletalk",
                "🟢",
                "Teletalk"
        );

        space(content,10);

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16),dp(16),dp(16),dp(18));
        card.setBackground(bg(Color.WHITE,18));

        content.addView(
                card,
                new LinearLayout.LayoutParams(-1,-2)
        );

        TextView selected =
                tv(
                        "✓ " + selectedRechargeOperator,
                        20,
                        BLUE
                );

        selected.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        selected.setGravity(Gravity.CENTER_VERTICAL);

        card.addView(
                selected,
                new LinearLayout.LayoutParams(-1,dp(42))
        );

        TextView numberTitle =
                tv("মোবাইল নম্বর",15,DARK);

        numberTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                numberTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText number = new EditText(this);

        number.setHint("যে নম্বরে রিচার্জ করবেন");
        number.setTextSize(17);
        number.setSingleLine(true);
        number.setTextColor(DARK);
        number.setHintTextColor(Color.GRAY);
        number.setInputType(InputType.TYPE_CLASS_PHONE);
        number.setPadding(dp(18),0,dp(18),0);

        number.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        12
                )
        );

        card.addView(
                number,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        TextView typeTitle =
                tv("রিচার্জের ধরন",15,DARK);

        typeTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                typeTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        LinearLayout typeRow = new LinearLayout(this);
        typeRow.setGravity(Gravity.CENTER);

        card.addView(
                typeRow,
                new LinearLayout.LayoutParams(-1,dp(52))
        );

        TextView prepaid =
                rechargeTypeButton("প্রিপেইড",true);

        TextView postpaid =
                rechargeTypeButton("পোস্টপেইড",false);

        typeRow.addView(
                prepaid,
                new LinearLayout.LayoutParams(0,dp(48),1)
        );

        LinearLayout.LayoutParams postParams =
                new LinearLayout.LayoutParams(0,dp(48),1);

        postParams.leftMargin = dp(8);

        typeRow.addView(postpaid,postParams);

        final boolean[] isPrepaid =
                new boolean[]{true};

        prepaid.setOnClickListener(v -> {

            isPrepaid[0] = true;

            prepaid.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            12
                    )
            );

            prepaid.setTextColor(BLUE);

            postpaid.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(215,220,228),
                            12
                    )
            );

            postpaid.setTextColor(Color.DKGRAY);
        });

        postpaid.setOnClickListener(v -> {

            isPrepaid[0] = false;

            postpaid.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            12
                    )
            );

            postpaid.setTextColor(BLUE);

            prepaid.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(215,220,228),
                            12
                    )
            );

            prepaid.setTextColor(Color.DKGRAY);
        });

        space(card,12);

        TextView amountTitle =
                tv("রিচার্জের পরিমাণ",15,DARK);

        amountTitle.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                amountTitle,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        EditText amount = new EditText(this);

        amount.setHint("টাকার পরিমাণ লিখুন");
        amount.setTextSize(17);
        amount.setSingleLine(true);
        amount.setTextColor(DARK);
        amount.setHintTextColor(Color.GRAY);

        amount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        amount.setPadding(dp(18),0,dp(18),0);

        amount.setBackground(
                outline(
                        Color.WHITE,
                        Color.rgb(220,225,232),
                        12
                )
        );

        card.addView(
                amount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,10);

        TextView quick =
                tv(
                        "দ্রুত পরিমাণ নির্বাচন করুন",
                        14,
                        Color.DKGRAY
                );

        card.addView(
                quick,
                new LinearLayout.LayoutParams(-1,dp(28))
        );

        LinearLayout quickRow = new LinearLayout(this);
        quickRow.setGravity(Gravity.CENTER);

        card.addView(
                quickRow,
                new LinearLayout.LayoutParams(-1,dp(48))
        );

        addQuickRechargeAmount(quickRow,amount,"৳ ৫০");
        addQuickRechargeAmount(quickRow,amount,"৳ ১০০");
        addQuickRechargeAmount(quickRow,amount,"৳ ২০০");
        addQuickRechargeAmount(quickRow,amount,"৳ ৫০০");

        space(card,18);

        TextView recharge =
                button(
                        "রিচার্জ করুন  →",
                        BLUE,
                        Color.WHITE
                );

        card.addView(
                recharge,
                new LinearLayout.LayoutParams(-1,dp(60))
        );

        recharge.setOnClickListener(v -> {

            String num =
                    number.getText()
                            .toString()
                            .trim()
                            .replace(" ","")
                            .replace("-","");

            String money =
                    amount.getText()
                            .toString()
                            .trim();

            if (num.isEmpty()) {
                number.setError("মোবাইল নম্বর দিন");
                return;
            }

            if (num.startsWith("+88")) {
                num = num.substring(3);
            }

            if (!num.matches("01[0-9]{9}")) {

                number.setError(
                        "সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন"
                );

                return;
            }

            if (money.isEmpty()) {

                amount.setError(
                        "রিচার্জের পরিমাণ দিন"
                );

                return;
            }

            try {

                double value =
                        Double.parseDouble(money);

                if (value < 20) {

                    amount.setError(
                            "সর্বনিম্ন ২০ টাকা"
                    );

                    return;
                }

                String rechargeType =
                        isPrepaid[0]
                                ? "প্রিপেইড"
                                : "পোস্টপেইড";

                new AlertDialog.Builder(this)
                        .setTitle("রিচার্জ নিশ্চিত করুন")
                        .setMessage(
                                "অপারেটর: "
                                        + selectedRechargeOperator
                                        + "\nধরন: "
                                        + rechargeType
                                        + "\nনম্বর: "
                                        + num
                                        + "\nপরিমাণ: ৳ "
                                        + money
                                        + "\n\n"
                                        + "এটি একটি ডেমো রিচার্জ রিকোয়েস্ট। আসল রিচার্জের জন্য Recharge API সংযুক্ত করতে হবে।"
                        )
                        .setNegativeButton("বাতিল",null)
                        .setPositiveButton(
                                "নিশ্চিত",
                                (dialog,which) -> {

                                    Toast.makeText(
                                            this,
                                            selectedRechargeOperator
                                                    + " রিচার্জ রিকোয়েস্ট গ্রহণ করা হয়েছে।",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    number.setText("");
                                    amount.setText("");
                                }
                        )
                        .show();

            } catch(Exception e) {

                amount.setError(
                        "সঠিক টাকার পরিমাণ দিন"
                );
            }
        });
    }

    /* =========================================================
       RECHARGE OPERATOR
       ========================================================= */

    private void addRechargeOperator(
            LinearLayout parent,
            String name,
            String icon,
            String operator) {

        boolean selected =
                selectedRechargeOperator.equals(operator);

        LinearLayout box = new LinearLayout(this);

        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        if (selected) {

            box.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            13
                    )
            );

        } else {

            box.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(220,225,232),
                            13
                    )
            );
        }

        TextView i = tv(icon,22,DARK);
        i.setGravity(Gravity.CENTER);

        box.addView(
                i,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        TextView n =
                tv(
                        name,
                        13,
                        selected ? BLUE : DARK
                );

        n.setGravity(Gravity.CENTER);
        n.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        box.addView(
                n,
                new LinearLayout.LayoutParams(-1,dp(27))
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,dp(68),1);

        p.setMargins(dp(3),0,dp(3),0);

        parent.addView(box,p);

        box.setOnClickListener(v -> {

            selectedRechargeOperator = operator;
            showMobileRecharge();
        });
    }

    /* =========================================================
       RECHARGE TYPE
       ========================================================= */

    private TextView rechargeTypeButton(
            String text,
            boolean selected) {

        TextView b =
                tv(
                        text,
                        15,
                        selected ? BLUE : Color.DKGRAY
                );

        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        if (selected) {

            b.setBackground(
                    outline(
                            Color.rgb(238,246,255),
                            BLUE,
                            12
                    )
            );

        } else {

            b.setBackground(
                    outline(
                            Color.WHITE,
                            Color.rgb(215,220,228),
                            12
                    )
            );
        }

        return b;
    }

    /* =========================================================
       QUICK RECHARGE AMOUNT
       ========================================================= */

    private void addQuickRechargeAmount(
            LinearLayout parent,
            EditText amount,
            String value) {

        TextView b = tv(value,12,BLUE);

        b.setGravity(Gravity.CENTER);
        b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        b.setBackground(
                outline(
                        Color.WHITE,
                        BLUE,
                        9
                )
        );

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0,dp(42),1);

        p.setMargins(dp(2),0,dp(2),0);

        parent.addView(b,p);

        b.setOnClickListener(v -> {

            String clean =
                    value
                            .replace("৳","")
                            .replace(",","")
                            .replace(" ","")
                            .replace("০","0")
                            .replace("১","1")
                            .replace("২","2")
                            .replace("৩","3")
                            .replace("৪","4")
                            .replace("৫","5")
                            .replace("৬","6")
                            .replace("৭","7")
                            .replace("৮","8")
                            .replace("৯","9");

            amount.setText(clean);
            amount.setSelection(amount.length());
        });
    }

    /* =========================================================
       OLD MONEY FORM
       ========================================================= */

    private void showMoneyForm(String type) {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(Color.WHITE);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(248,249,251));

        setContentView(root);

        LinearLayout header = new LinearLayout(this);

        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(8),0,dp(8),0);
        header.setBackgroundColor(BLUE);

        root.addView(
                header,
                new LinearLayout.LayoutParams(-1,dp(62))
        );

        TextView back = tv("‹",38,Color.WHITE);
        back.setGravity(Gravity.CENTER);

        header.addView(
                back,
                new LinearLayout.LayoutParams(dp(52),dp(62))
        );

        back.setOnClickListener(v -> showMobileBanking());

        TextView title = tv(type,21,Color.WHITE);

        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0,dp(62),1)
        );

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18),dp(20),dp(18),dp(20));
        card.setBackground(bg(Color.WHITE,18));

        LinearLayout.LayoutParams cardp =
                new LinearLayout.LayoutParams(-1,-2);

        cardp.setMargins(dp(12),dp(18),dp(12),0);

        root.addView(card,cardp);

        TextView info =
                tv(
                        "বিকাশ / নগদ / রকেট / উপায়",
                        16,
                        BLUE
                );

        info.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        card.addView(
                info,
                new LinearLayout.LayoutParams(-1,dp(34))
        );

        TextView min =
                tv(
                        "সর্বনিম্ন লেনদেন: ৳ ৫০০",
                        15,
                        Color.DKGRAY
                );

        card.addView(
                min,
                new LinearLayout.LayoutParams(-1,dp(30))
        );

        space(card,10);

        EditText number =
                input(
                        type.equals("ক্যাশ আউট")
                                ? "আপনার মোবাইল নম্বর"
                                : "যে নম্বরে পাঠাবেন",
                        false
                );

        card.addView(
                number,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        EditText amount =
                input(
                        "টাকার পরিমাণ (সর্বনিম্ন ৳৫০০)",
                        false
                );

        amount.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        card.addView(
                amount,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,12);

        EditText reference =
                input(
                        "রেফারেন্স (ঐচ্ছিক)",
                        false
                );

        card.addView(
                reference,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        space(card,16);

        TextView confirm =
                button(
                        type + "  →",
                        BLUE,
                        Color.WHITE
                );

        card.addView(
                confirm,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        confirm.setOnClickListener(v -> {

            String num =
                    number.getText().toString().trim();

            String raw =
                    amount.getText().toString().trim();

            if (num.isEmpty()) {
                number.setError("মোবাইল নম্বর দিন");
                return;
            }

            if (raw.isEmpty()) {
                amount.setError("টাকার পরিমাণ দিন");
                return;
            }

            try {

                double value =
                        Double.parseDouble(raw);

                if (value < 500) {
                    amount.setError("সর্বনিম্ন ৫০০ টাকা");
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle(type + " নিশ্চিত করুন")
                        .setMessage(
                                "নম্বর: "
                                        + num
                                        + "\nপরিমাণ: ৳ "
                                        + raw
                                        + "\n\n"
                                        + "এটি একটি ডেমো রিকোয়েস্ট। Provider API সংযুক্ত হলে আসল লেনদেন সম্পন্ন হবে।"
                        )
                        .setNegativeButton("বাতিল",null)
                        .setPositiveButton(
                                "OK",
                                (d,w) -> {

                                    Toast.makeText(
                                            this,
                                            "রিকোয়েস্ট গ্রহণ করা হয়েছে।",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                        )
                        .show();

            } catch(Exception e) {

                amount.setError(
                        "সঠিক টাকার পরিমাণ দিন"
                );
            }
        });
    }

    /* =========================================================
       REGISTER
       ========================================================= */

    private void showRegister() {

        getWindow().setStatusBarColor(BLUE);
        getWindow().setNavigationBarColor(BLUE);

        LinearLayout root = new LinearLayout(this);

        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22),dp(18),dp(22),dp(20));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(BLUE);

        ScrollView sc = new ScrollView(this);

        sc.setFillViewport(true);
        sc.addView(root);

        setContentView(sc);

        TextView logo = tv("Quick Pay",29,BLUE);

        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        logo.setBackground(bg(Color.WHITE,18));

        root.addView(
                logo,
                new LinearLayout.LayoutParams(dp(260),dp(75))
        );

        space(root,12);

        TextView country =
                tv(
                        "বাংলাদেশ  🇧🇩",
                        18,
                        Color.GRAY
                );

        country.setGravity(Gravity.CENTER_VERTICAL);
        country.setPadding(dp(18),0,dp(18),0);
        country.setBackground(bg(Color.WHITE,12));

        root.addView(
                country,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,9);

        EditText agent =
                input("রিসেলার এজেন্ট কোড",false);

        root.addView(
                agent,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,9);

        EditText name = input("পূর্ণ নাম",false);

        root.addView(
                name,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,9);

        EditText phone =
                input("+880 ফোন নম্বর",false);

        root.addView(
                phone,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,9);

        EditText pw =
                input("৬ ডিজিট পাসওয়ার্ড",true);

        root.addView(
                pw,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,9);

        EditText cpw =
                input(
                        "৬ ডিজিট পাসওয়ার্ড নিশ্চিত করুন",
                        true
                );

        root.addView(
                cpw,
                new LinearLayout.LayoutParams(-1,dp(55))
        );

        space(root,16);

        TextView next =
                button(
                        "পরবর্তী",
                        Color.WHITE,
                        BLUE
                );

        root.addView(
                next,
                new LinearLayout.LayoutParams(-1,dp(58))
        );

        next.setOnClickListener(v -> {

            String p =
                    phone.getText().toString().trim();

            String a =
                    pw.getText().toString().trim();

            String c =
                    cpw.getText().toString().trim();

            if (name.getText().toString().trim().isEmpty()) {
                name.setError("পূর্ণ নাম দিন");
                return;
            }

            if (p.isEmpty()) {
                phone.setError("ফোন নম্বর দিন");
                return;
            }

            if (a.length() != 6) {
                pw.setError("৬ ডিজিটের পাসওয়ার্ড দিন");
                return;
            }

            if (!a.equals(c)) {
                cpw.setError("পাসওয়ার্ড একই নয়");
                return;
            }

            pref.edit()
                    .putString(
                            "name",
                            name.getText().toString().trim()
                    )
                    .putString("phone",p)
                    .putString("password",a)
                    .putBoolean("logged_in",true)
                    .apply();

            showPinSetup();
        });

        TextView back =
                tv(
                        "অ্যাকাউন্ট আছে?  লগইন",
                        16,
                        Color.WHITE
                );

        back.setGravity(Gravity.CENTER);

        root.addView(
                back,
                new LinearLayout.LayoutParams(-1,dp(52))
        );

        back.setOnClickListener(v -> showLogin());
    }

    /* =========================================================
       FORGOT PASSWORD
       ========================================================= */

    private void showForgotPassword() {

        final EditText p = new EditText(this);

        p.setHint("ফোন নম্বর");

        p.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        new AlertDialog.Builder(this)
                .setTitle("পাসওয়ার্ড পুনরুদ্ধার")
                .setMessage(
                        "আপনার রেজিস্টার করা ফোন নম্বর দিন।"
                )
                .setView(p)
                .setPositiveButton(
                        "পরবর্তী",
                        (d,w) ->
                                Toast.makeText(
                                        this,
                                        "পাসওয়ার্ড রিসেট ফিচার পরে যুক্ত হবে",
                                        Toast.LENGTH_SHORT
                                ).show()
                )
                .setNegativeButton(
                        "বাতিল",
                        null
                )
                .show();
    }

    /* =========================================================
       BACK
       ========================================================= */

    @Override
    public void onBackPressed() {

        if (pref.getBoolean("logged_in",false)) {
            showHome();
        } else {
            showLogin();
        }
    }
}
