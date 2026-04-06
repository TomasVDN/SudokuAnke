package com.sudokuanke.activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.frontend.ColorPrefs
import com.frontend.ShinyButton
import com.sudokuanke.R

class SettingsActivity : ComponentActivity() {

    private data class ColorEntry(val label: String, val key: String)

    private val buttonColors = listOf(
        ColorEntry("Button color", ColorPrefs.BUTTON_BG),
    )
    private val shineColors = listOf(
        ColorEntry("Shine color 1", ColorPrefs.BUTTON_SHINE_1),
        ColorEntry("Shine color 2", ColorPrefs.BUTTON_SHINE_2),
        ColorEntry("Shine color 3", ColorPrefs.BUTTON_SHINE_3),
    )
    private val gameColors = listOf(
        ColorEntry("Selected cell",       ColorPrefs.CELL_HIGHLIGHT),
        ColorEntry("Your digits",         ColorPrefs.DIGIT_USER),
        ColorEntry("Fixed digits",        ColorPrefs.DIGIT_FIXED),
        ColorEntry("Conflicts",           ColorPrefs.DIGIT_CONFLICT),
        ColorEntry("Number highlight",    ColorPrefs.NUMBER_SELECTED_BACKGROUND),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shineSwitch          = findViewById<Switch>(R.id.shineSwitch)
        val buttonColorsSection  = findViewById<LinearLayout>(R.id.buttonColorsSection)
        val shineColorsSection   = findViewById<LinearLayout>(R.id.shineColorsSection)
        val gameColorsContainer  = findViewById<LinearLayout>(R.id.gameColorsContainer)
        val resetButton          = findViewById<ShinyButton>(R.id.resetButton)

        fun applyShineVisibility(shineOn: Boolean) {
            buttonColorsSection.visibility = if (shineOn) android.view.View.GONE else android.view.View.VISIBLE
            shineColorsSection.visibility  = if (shineOn) android.view.View.VISIBLE else android.view.View.GONE
        }

        shineSwitch.isChecked = ColorPrefs.getShineEnabled(this)
        applyShineVisibility(shineSwitch.isChecked)

        shineSwitch.setOnCheckedChangeListener { _, checked ->
            ColorPrefs.setShineEnabled(this, checked)
            applyShineVisibility(checked)
        }

        fun populateAll() {
            buttonColorsSection.removeAllViews()
            shineColorsSection.removeAllViews()
            gameColorsContainer.removeAllViews()
            buttonColors.forEach { addColorRow(buttonColorsSection, it) }
            shineColors.forEach  { addColorRow(shineColorsSection,  it) }
            gameColors.forEach   { addColorRow(gameColorsContainer,  it) }
        }

        populateAll()

        resetButton.setOnClickListener {
            ColorPrefs.resetToDefaults(this)
            shineSwitch.isChecked = ColorPrefs.getShineEnabled(this)
            applyShineVisibility(shineSwitch.isChecked)
            populateAll()
        }
    }

    private fun addColorRow(container: LinearLayout, entry: ColorEntry) {
        val row = layoutInflater.inflate(R.layout.item_color_setting, container, false)
        val label     = row.findViewById<TextView>(R.id.colorLabel)
        val preview   = row.findViewById<android.view.View>(R.id.colorPreview)
        val changeButton = row.findViewById<ShinyButton>(R.id.changeColorButton)

        label.text = entry.label
        preview.setBackgroundColor(ColorPrefs.getColor(this, entry.key))

        changeButton.setOnClickListener {
            showColorPicker(ColorPrefs.getColor(this, entry.key)) { newColor ->
                ColorPrefs.setColor(this, entry.key, newColor)
                preview.setBackgroundColor(newColor)
            }
        }

        container.addView(row)
    }

    private fun showColorPicker(currentColor: Int, onPicked: (Int) -> Unit) {
        var r = Color.red(currentColor)
        var g = Color.green(currentColor)
        var b = Color.blue(currentColor)

        val dp = resources.displayMetrics.density
        val pad = (16 * dp).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad / 2, pad, pad / 2)
        }

        val preview = android.view.View(this).apply {
            setBackgroundColor(Color.rgb(r, g, b))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (64 * dp).toInt()
            ).also { it.bottomMargin = (16 * dp).toInt() }
        }
        layout.addView(preview)

        fun addSlider(label: String, initial: Int, onChange: (Int) -> Unit) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = (8 * dp).toInt() }
            }
            val tv = TextView(this).apply {
                text = label
                layoutParams = LinearLayout.LayoutParams((32 * dp).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val valueText = TextView(this).apply {
                text = initial.toString()
                textAlignment = android.view.View.TEXT_ALIGNMENT_TEXT_END
                layoutParams = LinearLayout.LayoutParams((48 * dp).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val seekBar = SeekBar(this).apply {
                max = 255
                progress = initial
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                        onChange(progress)
                        valueText.text = progress.toString()
                        preview.setBackgroundColor(Color.rgb(r, g, b))
                    }
                    override fun onStartTrackingTouch(sb: SeekBar) {}
                    override fun onStopTrackingTouch(sb: SeekBar) {}
                })
            }
            row.addView(tv)
            row.addView(seekBar)
            row.addView(valueText)
            layout.addView(row)
        }

        addSlider("R", r) { r = it }
        addSlider("G", g) { g = it }
        addSlider("B", b) { b = it }

        AlertDialog.Builder(this)
            .setTitle("Pick color")
            .setView(layout)
            .setPositiveButton("OK") { _, _ -> onPicked(Color.rgb(r, g, b)) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
