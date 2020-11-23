package net.azarquiel.carro2020profe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.note2020.model.nota
import kotlinx.android.synthetic.main.custom_card_nota.view.*


/**
 * Created by pacopulido on 9/10/18.
 */
class CustomAdapter(
    val context: Context,
    val layout: Int,
    val listener: OnLongClickNota
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var dataList: List<nota> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setNotas(notas: List<nota>) {
        this.dataList = notas
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {
        fun bind(dataItem: nota, listener: OnLongClickNota){
            // itemview es el item de diseño
            // al que hay que poner los datos del objeto dataItem

            itemView.card_tvNombre.text = dataItem.nombre
            itemView.card_tvFecha.text = dataItem.fecha

            if (dataItem.contenido!=null) {
                val data = dataItem.contenido
                if (dataItem.contenido.toString().length>200) {
                    if (data != null) {
                        itemView.card_tvAsunto.text = data.substring(0, 200) + "..."
                    }
                }else {
                    itemView.card_tvAsunto.text = data
                }
            }else{
                itemView.card_tvAsunto.text = " "
            }

            itemView.tag = dataItem
            itemView.setOnLongClickListener{listener.onLongClickNota(dataItem, itemView)}
        }
    }

    interface OnLongClickNota {
        fun onLongClickNota(nota: nota, itemView: View):Boolean {
            return true
        }
    }
    
}