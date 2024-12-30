package com.example.figmadeneme

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.figmadeneme.databinding.ActivityUygulamalarBinding

class Uygulamalar : AppCompatActivity() {

    private lateinit var binding: ActivityUygulamalarBinding
    private val SHARED_PREFS_KEY = "AppLogs"
    private val APPS_KEY = "installedApps"
    private val REMOVED_APPS_KEY = "removedApps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUygulamalarBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Başlangıçta yüklü uygulamaları göster ve kaldırılan uygulamaları gizle
        binding.linearLayoutSilinen.visibility = LinearLayout.GONE
        binding.linearLayoutIcons.visibility = LinearLayout.VISIBLE

        // Başlıkları ekleyin
        addTitleToLayout(binding.linearLayoutIcons, "Yüklü Uygulamalar")
        addTitleToLayout(binding.linearLayoutSilinen, "Silinen Uygulamalar")

        // Uygulamaları yükleme işlemini arka planda gerçekleştiriyoruz
        LoadAppsTask().execute()

        // Silinen uygulamaları göster
        showRemovedApps()

        // Add click listener for the "Yüklü" button
        binding.solButton.setOnClickListener {
            binding.linearLayoutSilinen.visibility = LinearLayout.GONE // Hide removed apps
            binding.linearLayoutIcons.visibility = LinearLayout.VISIBLE // Show installed apps
        }

        // Add click listener for the "Silinen" button
        binding.sagButton.setOnClickListener {
            binding.linearLayoutIcons.visibility = LinearLayout.GONE // Hide installed apps
            binding.linearLayoutSilinen.visibility = LinearLayout.VISIBLE // Show removed apps
        }
    }

    private fun addTitleToLayout(layout: LinearLayout, title: String) {
        // Başlık TextView'ı oluşturun
        val titleTextView = TextView(this)
        titleTextView.text = title
        titleTextView.textSize = 18f // Başlık boyutunu ayarlayın
        titleTextView.setPadding(16, 16, 16, 16) // Başlığa padding ekleyin
        titleTextView.setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Başlık rengi
        layout.addView(titleTextView, 0) // Başlığı LinearLayout'un başına ekleyin
    }

    private fun createAppLayout(appInfo: ApplicationInfo, pm: PackageManager): LinearLayout {
        val appLayout = LinearLayout(this)
        appLayout.orientation = LinearLayout.HORIZONTAL

        val imageView = ImageView(this)
        val icon = appInfo.loadIcon(pm)
        imageView.setImageDrawable(icon)

        val iconLayoutParams = LinearLayout.LayoutParams(128, 128)
        imageView.layoutParams = iconLayoutParams

        val textView = TextView(this)
        val appName = appInfo.loadLabel(pm).toString()
        textView.text = appName

        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textLayoutParams.setMargins(16, 0, 0, 0)
        textView.layoutParams = textLayoutParams

        appLayout.addView(imageView)
        appLayout.addView(textView)
        return appLayout
    }

    private fun createRemovedAppLayout(packageName: String, pm: PackageManager): LinearLayout {
        val appLayout = LinearLayout(this)
        appLayout.orientation = LinearLayout.HORIZONTAL

        val imageView = ImageView(this)
        val icon = pm.getDefaultActivityIcon() // Silinmiş uygulamalar için varsayılan bir ikon kullanabilirsiniz
        imageView.setImageDrawable(icon)

        val iconLayoutParams = LinearLayout.LayoutParams(128, 128)
        imageView.layoutParams = iconLayoutParams

        val textView = TextView(this)
        textView.text = packageName // Paket adını gösterir

        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textLayoutParams.setMargins(16, 0, 0, 0)
        textView.layoutParams = textLayoutParams

        appLayout.addView(imageView)
        appLayout.addView(textView)
        return appLayout
    }

    private inner class LoadAppsTask : AsyncTask<Void, Void, List<ApplicationInfo>>() {

        override fun doInBackground(vararg params: Void?): List<ApplicationInfo> {
            val pm = packageManager
            return pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }

        override fun onPostExecute(applications: List<ApplicationInfo>) {
            val pm = packageManager
            val installedApps = mutableSetOf<String>()

            for (appInfo in applications) {
                val packageName = appInfo.packageName
                try {
                    val installerPackageName = pm.getInstallerPackageName(packageName)
                    if (installerPackageName == "com.android.vending") {
                        // Uygulama Google Play Store'dan yüklenmişse layout'u oluştur ve ekle
                        val appLayout = createAppLayout(appInfo, pm)
                        binding.linearLayoutIcons.addView(appLayout)

                        // Yüklü uygulama adını kaydet
                        installedApps.add(packageName)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Önceki yüklü uygulamaları SharedPreferences'tan al
            val sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
            val savedApps = sharedPreferences.getStringSet(APPS_KEY, emptySet())

            // Silinen uygulamaları bul (önceki listede olup şu an olmayanlar)
            val removedApps = savedApps?.minus(installedApps) ?: emptySet()

            if (removedApps.isNotEmpty()) {
                // Yeni silinen uygulamaları eski silinenlerle birleştir
                val currentRemovedApps = sharedPreferences.getStringSet(REMOVED_APPS_KEY, emptySet())?.toMutableSet()
                currentRemovedApps?.addAll(removedApps)

                // Silinen uygulamaları kalıcı olarak kaydet
                val editor = sharedPreferences.edit()
                editor.putStringSet(REMOVED_APPS_KEY, currentRemovedApps)
                editor.apply()

                // Silinen uygulamaları göster
                showRemovedApps()
            }

            // Mevcut uygulamaları SharedPreferences'a kaydet
            val editor = sharedPreferences.edit()
            editor.putStringSet(APPS_KEY, installedApps)
            editor.apply()
        }
    }
    private fun showRemovedApps() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val removedApps = sharedPreferences.getStringSet(REMOVED_APPS_KEY, emptySet())
        val pm = packageManager
        binding.linearLayoutSilinen.removeAllViews() // Önceki verileri temizle
        // Başlık TextView'ı oluşturun
        val titleTextView = TextView(this)
        titleTextView.text = "Silinen Uygulamalar"
        titleTextView.textSize = 18f // Başlık boyutunu ayarlayın
        titleTextView.setPadding(16, 16, 16, 16) // Başlığa padding ekleyin
        titleTextView.setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Başlık rengi
        binding.linearLayoutSilinen.addView(titleTextView) // Başlığı LinearLayout'a ekleyin

        if (removedApps.isNullOrEmpty()) {
            // Silinen uygulama yoksa uygun mesajı göster
            val noAppsTextView = TextView(this)
            noAppsTextView.text = "Silinen uygulama yok."
            binding.linearLayoutSilinen.addView(noAppsTextView)
        } else {
            for (packageName in removedApps) {
                val appLayout = createRemovedAppLayout(packageName, pm)
                binding.linearLayoutSilinen.addView(appLayout)
            }
        }
    }
}
