package com.example.tempestapp

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView

class InputValidation(private val context: Context) {

    fun isEditTextFilled(
            editText: AppCompatEditText,
            textView: AppCompatTextView,
            message: String
        ): Boolean {
            val value = editText.text.toString().trim()
            if (value.isEmpty()) {
                textView.error = message
                hideKeyboardFrom(editText)
                return false
            }
            return true
        }

        fun isEditTextMatches(
            editText1: AppCompatEditText,
            editText2: AppCompatEditText,
            textView: AppCompatTextView,
            message: String
        ): Boolean {
            val value1 = editText1.text.toString().trim()
            val value2 = editText2.text.toString().trim()

            if (!value1.contentEquals(value2)) {
                textView.error = message
                hideKeyboardFrom(editText2)
                return false
            }
            return true
        }

        private fun hideKeyboardFrom(view: View) {
           val inputMethodManager =
               context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
           inputMethodManager.hideSoftInputFromWindow(
               view.windowToken,
               WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
           )
       }
}