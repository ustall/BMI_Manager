package com.example.itmmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calculateBMI()
    }

    private fun calculateBMI() {
        val buttonCalculate = findViewById<Button>(R.id.b_calc)
        val heightEditText = findViewById<EditText>(R.id.height_tf)
        val weightEditText = findViewById<EditText>(R.id.weight_tf)
        val bmiResultText = findViewById<TextView>(R.id.bmi_result_text)
        val bmiResultDesc = findViewById<TextView>(R.id.bmi_result_desc)
        // Add an OnEditorActionListener to heightEditText
        heightEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculateBMIResult(heightEditText, weightEditText, bmiResultText,bmiResultDesc)
                true
            } else {
                false
            }
        }
        buttonCalculate.setOnClickListener {
            // Trigger the BMI calculation when the button is clicked
            calculateBMIResult(heightEditText, weightEditText, bmiResultText,bmiResultDesc)
        }
    }

    private fun calculateBMIResult(
        heightEditText: EditText,
        weightEditText: EditText,
        bmiResultText: TextView,
        bmiResultDesc: TextView
    ) {
        val heightStr = heightEditText.text.toString()
        val weightStr = weightEditText.text.toString()

        if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
            val height = heightStr.toFloat()/100
            val weight = weightStr.toFloat()

            if (height > 0 && weight > 0) {
                val bmi = weight / (height * height)
                val formattedBmi = String.format("%.2f", bmi)
                bmiResultText.text = "$formattedBmi"
                when {
                    bmi < 16 -> bmiResultDesc.text = "Severe underweight"
                    bmi < 16.9 -> bmiResultDesc.text = "Underweight"
                    bmi < 18.4 -> bmiResultDesc.text = "Mildly underweight"
                    bmi < 24.9 -> bmiResultDesc.text = "Normal weight"
                    bmi < 29.9 -> bmiResultDesc.text = "Overweight"
                    bmi < 34.9 -> bmiResultDesc.text = "Obesity Class 1 (Moderate)"
                    bmi < 39.9 -> bmiResultDesc.text = "Obesity Class 2 (Severe)"
                    else -> bmiResultDesc.text = "Obesity Class 3 (Very Severe)"
                }
                // Hide the keyboard
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            } else {
                // Handle invalid input (e.g., non-positive height or weight)
                bmiResultText.text = "Invalid input"
            }
        } else {
            // Handle empty input fields
            bmiResultText.text = "Please enter height and weight"
        }
    }
}
