package com.sudokuanke.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.backend.SudokuUtil
import com.sudokuanke.R

class LoadActivity : ComponentActivity(), AdapterView.OnItemSelectedListener {
    private var selectedFileName = ""
    private lateinit var listOfFiles : List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.load_page)

        val loadButton = findViewById<Button>(R.id.loadButton)
        loadButton.setOnClickListener {
            startSudokuActivity()
        }

        val exitButton = findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener {
            finish()
        }

        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            deleteSave()
        }

        val warningTextView = findViewById<TextView>(R.id.warningTextView)
        val combobox = findViewById<Spinner>(R.id.fileSelectionBox)
        combobox.onItemSelectedListener = this

        refreshCombobox()
    }

    private fun deleteSave() {
        SudokuUtil.deleteSave(applicationContext, selectedFileName)
        refreshCombobox()
    }

    private fun refreshCombobox() {
        val warningTextView = findViewById<TextView>(R.id.warningTextView)
        val combobox = findViewById<Spinner>(R.id.fileSelectionBox)

        listOfFiles = SudokuUtil.getSavedNames(applicationContext)
        if (listOfFiles.isEmpty()) {
            combobox.visibility = INVISIBLE
            warningTextView.visibility = VISIBLE
        } else {
            warningTextView.visibility = INVISIBLE
            combobox.visibility = VISIBLE
        }

        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, listOfFiles )
        combobox.adapter = adapter
    }

    private fun startSudokuActivity() {
        val boardAsString = SudokuUtil.getFromDiskAsString(applicationContext, selectedFileName)

        val intent = Intent(this, SudokuActivity::class.java)
        intent.putExtra("board", boardAsString)
        finish()
        startActivity(intent)
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        selectedFileName = listOfFiles[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}