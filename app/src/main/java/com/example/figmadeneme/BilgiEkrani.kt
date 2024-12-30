package com.example.figmadeneme

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.appcompat.app.AppCompatActivity
import com.example.figmadeneme.databinding.ActivityBilgiEkraniBinding
import android.os.BatteryManager
import android.widget.TextView
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.widget.Toast

class BilgiEkrani : AppCompatActivity() {
    private lateinit var binding: ActivityBilgiEkraniBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBilgiEkraniBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // RAM Bilgileri
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // RAM'i GB olarak hesapla
        val totalRam = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0) // GB cinsinden
        val availableMemory = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0) // GB cinsinden
        val usedRam = totalRam - availableMemory
        val usedRamPercentage = (usedRam / totalRam) * 100

        // Yüzde işaretiyle beraber RAM kullanımını göster
        binding.kullanilanRamTextYuzde.text = String.format("%.2f%%", usedRamPercentage)

        // TextView üzerinde RAM bilgisini GB olarak ve virgülden sonra iki basamakla göster
        binding.textViewRam.text = String.format("Total: %.2f GB", totalRam)
        binding.textViewRam2.text = String.format("Free: %.2f GB", availableMemory)

        // ProgressBar'a RAM kullanım yüzdesini aktar
        binding.ramUsageBar.progress = usedRamPercentage.toInt()

        // Depolama Bilgileri
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalStorage = (totalBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0)
        val availableStorage = (availableBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0)

        // TextView'leri güncelle
        binding.textViewStorage1.text = String.format("Total: %.2f GB", totalStorage)
        binding.textViewStorage2.text = String.format("Free: %.2f GB", availableStorage)

        // Kullanılan depolama ve yüzdesi
        val usedStorage = totalStorage - availableStorage
        val usedStoragePercentage = (usedStorage / totalStorage) * 100
        binding.kullanilanDepolamaTextYuzde.text = String.format("%.2f%%", usedStoragePercentage)

        // ProgressBar'a depolama kullanım yüzdesini aktar
        binding.storageUsageBar.progress = usedStoragePercentage.toInt()
        // CPU Modeli
        val cpuModel = Build.HARDWARE

        // CPU Çekirdek Sayısı
        val cpuCores = Runtime.getRuntime().availableProcessors()

        // Bilgileri TextView'de Göster
        binding.textViewCpu1.text = "CPU Model: ${cpuModel.toUpperCase()}"
        binding.textViewCpu2.text = "Cores Number: $cpuCores"


        // Uygulama sayısını hesaplamak için PackageManager'ı kullanın
        val packageManager = packageManager
        val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // Google Play Store'dan yüklenen uygulamaları filtreleyin
        val playStoreApps = applications.filter { appInfo ->
            val installerPackageName = packageManager.getInstallerPackageName(appInfo.packageName)
            installerPackageName == "com.android.vending" // Google Play Store'dan yüklenen uygulamalar
        }
        val appCount = playStoreApps.size


        // Android versiyon bilgilerini al
        val versionName = Build.VERSION.RELEASE
        val versionCode = Build.VERSION.SDK_INT

        // Versiyon bilgilerini bir TextView içinde göster
        binding.textViewAndroidVersion.text = "Android Version: $versionName"
        binding.textViewAndroidVersionName.text = "API Level : $versionCode"
        // SensorManager'ı al
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Tüm sensörleri al
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Sensörlerin sayısını ve isimlerini göster
        val sensorInfo = StringBuilder()
        sensorInfo.append("Sensör Sayısı: ${sensorList.size}\n\n")
        /*for (sensor in sensorList) {
            sensorInfo.append("Sensör: ${sensor.name} - Tür: ${sensor.type}\n")
        }*/

        //Uygulama sayısını bir TextView içinde gösterin

        fun isPlayStoreInstalled(context: Context): Boolean {
            return try {
                // Play Store package name
                val playStorePackageName = "com.android.vending"

                // Uygulamanın package manager'ını al
                val packageManager = context.packageManager

                // Play Store'un package bilgilerini almaya çalış
                packageManager.getPackageInfo(playStorePackageName, 0)
                true // Eğer başarılı olursa, Play Store yüklüdür
            } catch (e: PackageManager.NameNotFoundException) {
                false // Eğer package bulunamazsa, Play Store yüklü değildir
            }
        }
        val isInstalled = isPlayStoreInstalled(this)

        if (isInstalled) {
            // Play Store yüklü
            Toast.makeText(this, "Play Store yüklü.", Toast.LENGTH_SHORT).show()
        } else {
            // Play Store yüklü değil
            Toast.makeText(this, "Play Store yüklü değil.", Toast.LENGTH_SHORT).show()
        }


                    binding.imageView7.setOnClickListener({
                    val intent = Intent(this@BilgiEkrani, Uygulamalar::class.java)
                    startActivity(intent)
                    })
                    binding.nextButton.setOnClickListener({
                        val intent = Intent(this, UcuncuSayfa::class.java)
                        startActivity(intent)
                    })

                    binding.textViewNext.setOnClickListener({
                        val intent = Intent(this, UcuncuSayfa::class.java)
                        startActivity(intent)
                    })
                    binding.imageView4.setOnClickListener({
                        val intent = Intent(this, UcuncuSayfa::class.java)
                        startActivity(intent)
                    })
                    binding.imageView6.setOnClickListener({
                        val intent = Intent(this, UcuncuSayfa::class.java)
                        startActivity(intent)
                    })
                    binding.textViewSensors.setOnClickListener({
                        val intent = Intent(this, UcuncuSayfa::class.java)
                        startActivity(intent)
                    })

                    binding.textViewExit.setOnClickListener({
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    })

                }
}



