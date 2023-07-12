package com.example.gymkhanaadmin

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class QRgeneratorActivity : AppCompatActivity() {
    private lateinit var textInput: EditText
    private lateinit var generateButton: Button
    private lateinit var qrCodeImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrgenerator)

        textInput = findViewById(R.id.textInput)
        generateButton = findViewById(R.id.generateButton)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)

        generateButton.setOnClickListener {
            generateQRCode()
        }
    }

    private fun generateQRCode() {
        val inputText = textInput.text.toString().trim()
        if (inputText.isNotEmpty()) {
            val qrCodeSize = 500
            val qrCodeBitmap = generateQRCodeBitmap(inputText, qrCodeSize)
            qrCodeImageView.setImageBitmap(qrCodeBitmap)
        } else {
            qrCodeImageView.setImageBitmap(null)
        }
    }

    private fun generateQRCodeBitmap(text: String, size: Int): Bitmap? {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap

        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return null
    }
}
