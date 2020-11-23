package com.example.note2020.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.note2020.R
import com.example.note2020.model.nota
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.trash_button.*
import net.azarquiel.carro2020profe.adapter.CustomAdapter
import org.jetbrains.anko.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), CustomAdapter.OnLongClickNota {

    private lateinit var notas:ArrayList<nota>
    private lateinit var selectedNotas:ArrayList<nota>
    private lateinit var selectedViewNotas:ArrayList<View>

    private lateinit var adapter: CustomAdapter
    private lateinit var preferencias: SharedPreferences
    private lateinit var contadorIdentificador: SharedPreferences
    private lateinit var selectedNote: nota

    private var identificador = 0
    private var deleteMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        init()
    }

    private fun init() {
        selectedViewNotas = ArrayList()
        selectedNotas = ArrayList()
        notas = ArrayList()

        preferencias = getSharedPreferences("data", Context.MODE_PRIVATE) //creamos o abrimos el archigo data.xml

        contadorIdentificador = getSharedPreferences("identifierCounter", Context.MODE_PRIVATE)
        identificador = contadorIdentificador.getInt("identifierCounter", 0) //porque numero de identificador van las notas

        setAdapter()
        getNotas()

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener { dialogoAdd() }
        findViewById<FloatingActionButton>(R.id.fab_delete).setOnClickListener{ removeNotas()}
    }

    private fun setAdapter() {
        adapter = CustomAdapter(this, R.layout.custom_card_nota, this)
        adapter.setNotas(notas)
        rv_notas.adapter = adapter
        rv_notas.layoutManager = LinearLayoutManager(this)
    }

    private fun getNotas() {
        val mapaProductos = preferencias.all
        for (entry in mapaProductos.entries){
            val jsonProducto = entry.value.toString()
            val nota = Gson().fromJson(jsonProducto, nota::class.java)
            notas.add(nota)
        }
        notas.sortBy { it.fecha.toLowerCase() } //ordenamos el array antes de actualizarlo
        adapter.setNotas(notas)
    }


    private fun dialogoAdd() {
        alert {
            customView {
                verticalLayout {
                    title = "Añadir nota:"
                    val textNombre = editText {
                        hint = "Nombre de la nota:"
                    }
                    positiveButton("Aceptar") {
                        addNota(textNombre.text.toString(), null)
                    }
                    negativeButton("Cancelar") {
                    }
                }
            }
        }.show()
    }

    /*private fun dialogoRemove(nota: nota){
        alert {
            title = "Eliminar nota"
            message = "Estas seguro de que quieres eliminar \"${nota.nombre}\""
            positiveButton("Si"){
                removeNotas(nota)
            }
            negativeButton("No"){
            }
        }.show()
    }*/

    private fun addNota(nombre: String, asunto: String?) {
        val editorId = contadorIdentificador.edit()
        identificador++
        editorId.putInt("contadorIdentificador", identificador)
        editorId.commit()

        val nota = nota(nombre, asunto, identificador, obtenerFechaConFormato())
        val jsonProducto:String = Gson().toJson(nota)

        val editor = preferencias.edit()
        editor.putString(nota.id.toString(), jsonProducto)
        editor.commit()

        notas.add(nota)
        adapter.setNotas(notas)

    }

    private fun removeNotas() {
        if (selectedNotas!=null) {
            selectedNotas.forEach() {
                val editor = preferencias.edit()
                editor.remove(it.id.toString())
                editor.commit()

                notas.remove(it)
                adapter.setNotas(notas)
            }
        }

        deleteMode(false)
    }

    private fun editNota(nota: nota, contenido:String?, nombre: String) {
        notas.remove(nota)
        val jsonNota = preferencias.getString(nota.id.toString(), "no se ha encontrado la nota")
        val nota: nota = Gson().fromJson(jsonNota, nota::class.java)

        if (contenido!=null) nota.contenido = contenido
        if (nombre!=null) nota.nombre = nombre

        val notaModificada: String = Gson().toJson(nota)
        val editor = preferencias.edit()
        editor.putString(nota.id.toString(), notaModificada)
        editor.commit()

        notas.add(nota)
        notas.sortBy { it.fecha.toLowerCase() } //ordenamos el array antes de actualizarlo
        adapter.setNotas(notas) //actualizamos el recycleview
    }

    private fun deleteMode(bandera:Boolean){
        deleteMode=bandera
        if (bandera==true){
            fab_delete.visibility=View.VISIBLE
            fab_add.visibility=View.GONE
        }else{
            fab_delete.visibility=View.GONE
            fab_add.visibility=View.VISIBLE
            selectedNotas = ArrayList()
            selectedViewNotas = ArrayList()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun obtenerFechaConFormato(): String {
        val calendar: Calendar = Calendar.getInstance()
        val date: Date = calendar.getTime()
        val sdf: SimpleDateFormat
        sdf = SimpleDateFormat("dd-MM-yyyy")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"))
        return sdf.format(date)
    }

    override fun onLongClickNota(nota: nota, itemView: View): Boolean {
        //dialogoRemove(nota)
        itemView.backgroundColor = Color.RED
        itemView.translationZ = 0F
        selectedViewNotas.add(itemView)
        selectedNotas.add(nota)

        deleteMode(true)
        return super.onLongClickNota(nota, itemView)
    }

    fun onClickNota(v: View){
        if (deleteMode){
            selectedViewNotas.forEach() {
                if (it.tag == v.tag) {
                    v.backgroundColor = Color.BLACK
                    v.translationZ = 12F
                    selectedViewNotas.remove(v)
                    Log.d("app", "${v.tag}")
                } else {
                    v.backgroundColor = Color.RED
                    v.translationZ = 0F
                    Log.d("app", "${v.tag}")
                    selectedViewNotas.add(v)
                }

            }

        }else {
            selectedNote = v.tag as nota
            val intent = Intent(applicationContext, NoteEditor::class.java)
            intent.putExtra("nombreNota", selectedNote.nombre)
            intent.putExtra("contenidoNota", selectedNote.contenido)
            intent.putExtra("idNota", selectedNote.id)
            intent.putExtra("fechaNota", selectedNote.fecha)

            startActivityForResult(intent, CONTENT_NOTE)
            //overridePendingTransition(R.drawable.left_in, R.drawable.left_out); <--- animacion en desarrollo
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==RESULT_OK && requestCode==CONTENT_NOTE){
            if (data != null) {
                if (data.hasExtra("contenidoNota")){
                    editNota(selectedNote, data.extras?.getString("contenidoNota").toString(), data.extras?.getString("nombreNota").toString())
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val CONTENT_NOTE:Int = 10
    }


}