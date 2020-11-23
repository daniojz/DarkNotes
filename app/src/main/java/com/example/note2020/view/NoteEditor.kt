package com.example.note2020.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.note2020.R
import com.example.note2020.model.nota
import kotlinx.android.synthetic.main.activity_note_editor.*
import kotlinx.android.synthetic.main.toolbar_note.*
import kotlinx.android.synthetic.main.toolbar_note.view.*

class NoteEditor : AppCompatActivity() {

    private lateinit var preferencias: SharedPreferences
    private lateinit var notaSeleccionada: nota

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_editor)

        init()

        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener{
            backAndSave()
        }
        toolbar.ib_cancel.setOnClickListener{
            cancel()
        }
        et_nombreNota.setText(notaSeleccionada.nombre)

    }

    private fun init() {
        notaSeleccionada = nota(
            intent.getStringExtra("nombreNota").toString(),
            intent.getStringExtra("contenidoNota"),
            intent.getIntExtra("idNota", 0),
            intent.getStringExtra("fechaNota").toString()
        )
        preferencias = getSharedPreferences("data", Context.MODE_PRIVATE) //creamos o abrimos el archigo data.xml

        getText(notaSeleccionada)
    }


    override fun finish() {
        var data = Intent()
        data.putExtra("contenidoNota", tv_editNoteContent.text.toString())
        data.putExtra("nombreNota", et_nombreNota.text.toString())
        setResult(RESULT_OK, data)
        super.finish()
    }

    private fun getText(notaSeleccionada: nota){
        if (notaSeleccionada.contenido!=null) {
            tv_editNoteContent.setText(notaSeleccionada.contenido)
        }else{
            tv_editNoteContent.setText("")
        }
    }

    private fun backAndSave(): View.OnClickListener? {
        finish() //close intent
        return null
    }

    private fun cancel() {
        super.finish()
    }
}