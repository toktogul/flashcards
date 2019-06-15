package kg.flashcards.android

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_photo_dialog.view.*
import java.io.File

class FlashcardsAdapter(private val listener: AdapterListener) : RecyclerView.Adapter<FlashcardViewHolder>() {
    private val list = mutableListOf<FlashcardItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val vh = FlashcardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_flashcard, parent, false)
        )
        vh.itemView.setOnLongClickListener {
            AlertDialog.Builder(it.context)
                .setTitle(it.resources.getString(R.string.delete_item))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    val item = list[vh.adapterPosition]
                    listener.onDeleteClick(item)
                    deleteFile(vh.adapterPosition)
                    dialog.dismiss()
                }
                .show()
            return@setOnLongClickListener true
        }

        vh.itemView.setOnClickListener {
            vh.value.visibility = if (vh.value.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        }
        return vh
    }

    private fun deleteFile(adapterPosition: Int) {
        list.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val item = list[position]
        holder.image.setImageURI(item.uri)
        holder.value.text = item.value
    }

    fun addFiles(list: List<FlashcardItem>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addFile(item: FlashcardItem) {
        this.list.add(item)
        notifyItemInserted(list.size)

    }
}


class FlashcardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image = view.findViewById<ImageView>(R.id.card)!!
    val value = view.findViewById<TextView>(R.id.card_value)!!
}

class FlashcardItem(val uri: Uri, val value: String, val file: File)

interface AdapterListener {
    fun onDeleteClick(item: FlashcardItem)
}