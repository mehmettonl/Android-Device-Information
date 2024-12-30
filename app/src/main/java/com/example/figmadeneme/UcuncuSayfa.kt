package com.example.figmadeneme

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.figmadeneme.databinding.ActivityUcuncuSayfaBinding

class UcuncuSayfa : AppCompatActivity() {
    private lateinit var binding: ActivityUcuncuSayfaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUcuncuSayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SensorManager'ı al
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Tüm sensörleri al
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Sensörlerin özelliklerini toplama
        val sensorInfo = StringBuilder()
        sensorInfo.append("Sensör Sayısı: ${sensorList.size}\n\n")

        for (sensor in sensorList) {
            sensorInfo.append("Adı: ${sensor.name}\n")
            sensorInfo.append("Türü: ${sensor.type}\n")
            sensorInfo.append("Üretici: ${sensor.vendor}\n")
            sensorInfo.append("Versiyon: ${sensor.version}\n")
            sensorInfo.append("Güç Tüketimi: ${sensor.power} mA\n")
            sensorInfo.append("Maksimum Aralık: ${sensor.maximumRange}\n")
            sensorInfo.append("Çözünürlük: ${sensor.resolution}\n")

            // Sensörün ne işe yaradığını ekle
            val description = getSensorDescription(sensor.type)
            sensorInfo.append("Özelliği: $description\n\n")
        }

        binding.textViewSensorNumber.text = sensorInfo.toString()


        binding.textViewSystem.setOnClickListener {
            val intent = Intent(this, BilgiEkrani::class.java)
            startActivity(intent)
        }


    }

    private fun getSensorDescription(sensorType: Int): String {
        return when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> "Cihazın hızlanma kuvvetini m/s² cinsinden ölçer."
            Sensor.TYPE_GYROSCOPE -> "Cihazın dönüş hızını ölçer."
            Sensor.TYPE_MAGNETIC_FIELD -> "Ortamın manyetik alanını ölçer."
            Sensor.TYPE_LIGHT -> "Ortam ışık seviyesini lüks cinsinden ölçer."
            Sensor.TYPE_PRESSURE -> "Ortamın hava basıncını ölçer."
            Sensor.TYPE_PROXIMITY -> "Cihazın yakınındaki bir nesnenin mesafesini ölçer."
            Sensor.TYPE_GRAVITY -> "Yerçekimi kuvvetini m/s² cinsinden ölçer."
            Sensor.TYPE_LINEAR_ACCELERATION -> "Yerçekimi etkisi olmadan hızlanmayı ölçer."
            Sensor.TYPE_ROTATION_VECTOR -> "Cihazın yönelimini ölçer."
            Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ortam sıcaklığını ölçer."
            Sensor.TYPE_RELATIVE_HUMIDITY -> "Ortamın bağıl nemini yüzde olarak ölçer."
            Sensor.TYPE_HEART_RATE -> "Kullanıcının kalp atış hızını ölçer."
            Sensor.TYPE_STEP_COUNTER -> "Atılan adımları sayar."
            Sensor.TYPE_TEMPERATURE -> "Cihazın dahili sıcaklığını ölçer."
            else -> "Bu sensör için açıklama mevcut değil."
        }
    }
}
