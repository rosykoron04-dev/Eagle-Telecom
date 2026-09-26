# ফোন দিয়ে APK বানানোর সবচেয়ে সহজ উপায়

1. GitHub-এ একটি নতুন repository তৈরি করুন (ফোনের Chrome দিয়েও করা যায়)।
2. এই ZIP-এর সব ফাইল repository-তে upload করুন।
3. `.github/workflows/build-apk.yml` ফাইলটিও upload হয়েছে কিনা দেখুন।
4. GitHub-এর **Actions** ট্যাবে যান।
5. **Build Eagle Telecom APK** workflow নির্বাচন করে **Run workflow** চাপুন।
6. Build শেষ হলে workflow-এর **Artifacts** অংশ থেকে `Eagle-Telecom-APK` ZIP download করুন।
7. ZIP খুলে `app-debug.apk` ফোনে install করুন।

নোট: এটি debug APK। Real payment/recharge চালুর আগে নিজের secure backend এবং বৈধ provider APIs যুক্ত করতে হবে। bKash/Nagad/bank secret কখনো APK-তে রাখবেন না।
