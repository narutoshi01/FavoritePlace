package com.narutoshi.favoriteplace

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_edit_place.*
import java.text.SimpleDateFormat
import java.util.*

class EditPlaceActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_place)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setDefaultDate()

        et_date.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.apply {
            findItem(R.id.action_delete).isVisible = false
            findItem(R.id.action_edit).isVisible = false
            findItem(R.id.action_register).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_register) {
            // TODO DBに登録。新規 OR アップデート
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setDefaultDate() {
        val today = SimpleDateFormat("yyyy/MM/dd").format(Date())
        et_date.setText(today)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_date -> onDateSet()
        }
    }

    private fun onDateSet() {
        val calender = Calendar.getInstance()
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calender.set(Calendar.YEAR, year)
                calender.set(Calendar.MONTH, month)
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val selectedDate = SimpleDateFormat("yyyy/MM/dd").format(calender.time)
                et_date.setText(selectedDate)
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
