package com.example.gymkhana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.khalti.checkout.helper.Config
import com.khalti.checkout.helper.KhaltiCheckOut
import com.khalti.checkout.helper.OnCheckOutListener
import com.khalti.checkout.helper.PaymentPreference
import com.khalti.widget.KhaltiButton


class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val khaltiButton = findViewById<KhaltiButton>(R.id.khalti_button)

        val map = HashMap<String, Any>()
        map["merchant_extra"] = "This is extra data"

        val builder = Config.Builder("test_public_key_285b8913b21f4795a986f075bd9ac01f", "Product ID", "Main", 1100L, object : OnCheckOutListener {
            override fun onError(action: String, errorMap: Map<String, String>) {
                // Handle error
            }

            override fun onSuccess(data: Map<String, Any>) {
                // Handle success
            }
        })
            .paymentPreferences(arrayListOf(
                PaymentPreference.KHALTI,
                PaymentPreference.EBANKING,
                PaymentPreference.MOBILE_BANKING,
                PaymentPreference.CONNECT_IPS,
                PaymentPreference.SCT
            ))
            .additionalData(map)
            .productUrl("http://example.com/product")
            .mobile("9800000000")

        val config = builder.build()
        khaltiButton.setCheckOutConfig(config)

        khaltiButton.setOnClickListener {
            val khaltiCheckOut = KhaltiCheckOut(this, config)
            khaltiCheckOut.show()
        }
    }
}
