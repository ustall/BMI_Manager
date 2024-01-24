package com.example.itmmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var bmiResultText: TextView
    private lateinit var bmiResultDesc: TextView
    private lateinit var bmiGoalEditText: EditText
    private lateinit var neededWeightTextView: TextView
    private lateinit var bmiGoalCV: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        calculateBMI()
        morePopUpButton()
        calculateBMIGoal()
    }

    private fun initializeViews() {
        heightEditText = findViewById(R.id.height_tf)
        weightEditText = findViewById(R.id.weight_tf)
        bmiResultText = findViewById(R.id.bmi_result_text)
        bmiResultDesc = findViewById(R.id.bmi_result_desc)
        bmiGoalEditText = findViewById(R.id.bmi_goal_field)
        neededWeightTextView = findViewById(R.id.needed_weight_tv)
        bmiGoalCV = findViewById(R.id.bmi_goal)
    }

    private fun calculateBMI() {
        val buttonCalculate = findViewById<Button>(R.id.b_calc)

        heightEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculateBMIResult()
                true
            } else {
                false
            }
        }
        buttonCalculate.setOnClickListener {
            calculateBMIResult()
        }
    }

    private fun calculateBMIResult() {
        val heightStr = heightEditText.text.toString()
        val weightStr = weightEditText.text.toString()

        if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
            val height = heightStr.toFloat() / 100
            val weight = weightStr.toFloat()

            if (height > 0 && weight > 0) {
                val bmi = weight / (height * height)
                val formattedBmi = String.format("%.2f", bmi)
                bmiResultText.text = formattedBmi
                handleBMIResult(bmi)
                hideKeyboard()
            } else {
                bmiResultText.text = "Invalid input"
            }
        } else {
            bmiResultText.text = ""
        }
    }

    private fun handleBMIResult(bmi: Float) {
        when {
            bmi < 16 -> bmiResultDesc.text = "Severe underweight"
            bmi < 16.9 -> bmiResultDesc.text = "Underweight"
            bmi < 18.4 -> bmiResultDesc.text = "Mildly underweight"
            bmi < 24.9 -> bmiResultDesc.text = "Normal weight"
            bmi < 29.9 -> bmiResultDesc.text = "Overweight"
            bmi < 34.9 -> bmiResultDesc.text = "Obesity Class 1"
            bmi < 39.9 -> bmiResultDesc.text = "Obesity Class 2"
            else -> bmiResultDesc.text = "Obesity Class 3"
        }
    }

    private fun morePopUpButton() {
        val moreButton = findViewById<ImageView>(R.id.more_button)

        heightEditText = findViewById(R.id.height_tf)
        weightEditText = findViewById(R.id.weight_tf)
        bmiResultText = findViewById(R.id.bmi_result_text)
        bmiResultDesc = findViewById(R.id.bmi_result_desc)
        bmiGoalEditText = findViewById(R.id.bmi_goal_field)
        neededWeightTextView = findViewById(R.id.needed_weight_tv)
        bmiGoalCV = findViewById(R.id.bmi_goal)

        moreButton.setOnClickListener {
            val popupView = LayoutInflater.from(this).inflate(R.layout.more_popup, null)
            val popupWindow = createPopupWindow(popupView)
            val backgroundView = LayoutInflater.from(this).inflate(R.layout.background_tint, null)
            val backgroundPopupWindow = createBackgroundPopupWindow(backgroundView, popupWindow)

            backgroundPopupWindow.showAtLocation(backgroundView, 0, 0, 0)
            popupWindow.setOnDismissListener {
                backgroundPopupWindow.dismiss()
            }

            popupWindow.showAsDropDown(moreButton)

            val moreButtonsIds = arrayOf(R.id.add_bmi_goal_button, R.id.save_button)
            val moreButtonsIdsMap =
                moreButtonsIds.map { popupView.findViewById<LinearLayout>(it) }

            val moreButtonsIdsListener = View.OnClickListener { view ->
                when (view.id) {
                    R.id.add_bmi_goal_button -> {
                        bmiGoalCV.visibility = View.VISIBLE
                        popupWindow.dismiss()
                    }
                    R.id.save_button -> {
                        popupWindow.dismiss()
                    }
                }
            }
            moreButtonsIdsMap.forEach { it.setOnClickListener(moreButtonsIdsListener) }
        }
    }

    private fun createPopupWindow(contentView: View): PopupWindow {
        val popupWindow = PopupWindow(
            contentView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.isTouchable = true
        popupWindow.isFocusable = true
        return popupWindow
    }

    private fun createBackgroundPopupWindow(
        backgroundView: View,
        popupWindow: PopupWindow
    ): PopupWindow {
        val backgroundPopupWindow = PopupWindow(
            backgroundView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        backgroundPopupWindow.showAtLocation(backgroundView, 0, 0, 0)
        backgroundPopupWindow.setOnDismissListener {
            popupWindow.dismiss()
        }
        return backgroundPopupWindow
    }


    private fun calculateBMIGoal() {
        val buttonCalculate = findViewById<Button>(R.id.b_calc)

        bmiGoalEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculateNeededWeightAndDisplay()
                true
            } else {
                false
            }
        }
        buttonCalculate.setOnClickListener {
            calculateNeededWeightAndDisplay()
        }
        hideBmiGoalWindow()
    }

    private fun calculateNeededWeightAndDisplay() {
        val heightStr = heightEditText.text.toString()
        val targetBMI = bmiGoalEditText.text.toString().toFloatOrNull()
        val height = heightStr.toFloat() / 100

        if (targetBMI != null && height > 0) {
            val neededWeight = calculateNeededWeight(targetBMI, height)
            val formattedNeededWeight = String.format("%.2f", neededWeight)
            neededWeightTextView.text = "Needed Weight:\n$formattedNeededWeight kg"
        } else {
            neededWeightTextView.text = ""
        }
    }

    private fun hideBmiGoalWindow(){
        val bmiGoalCV = findViewById<CardView>(R.id.bmi_goal)
        bmiGoalCV.setOnLongClickListener {
            Toast.makeText(bmiGoalCV.context, "BMI Goal window hidden", Toast.LENGTH_SHORT).show()
            bmiGoalCV.visibility = View.GONE
            true
        }
    }

    private fun calculateNeededWeight(targetBMI: Float, height: Float): Float {
        return targetBMI * height * height
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

