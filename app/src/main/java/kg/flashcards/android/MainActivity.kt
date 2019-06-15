package kg.flashcards.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_photo_dialog.view.*


const val GALLERY_CODE = 124

class MainActivity : Activity(), View.OnClickListener, AdapterListener {
    private lateinit var fileFinder: FileFinder

    private lateinit var adapter: FlashcardsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileFinder = FileFinder(applicationContext)
        add.setOnClickListener(this)
        adapter = FlashcardsAdapter(this)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fetchAllFiles()
    }

    private fun fetchAllFiles() {
        val files = applicationContext.filesDir.listFiles()!!
        val flashcards = mutableListOf<FlashcardItem>()
        files.forEach {
            flashcards.add(FlashcardItem(Uri.parse(it.path), it.name.split("_")[0], it))
        }
        adapter.addFiles(flashcards)
    }

    override fun onDeleteClick(item: FlashcardItem) {
        fileFinder.deleteFile(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) showDialog(intent!!)
    }

    private fun showDialog(intent: Intent) {
        val result = fileFinder.getTmpFileUri(intent.data!!)
        result?.let { tmp ->
            val view = LayoutInflater.from(this).inflate(R.layout.add_photo_dialog, null)
            view.image.setImageURI(Uri.parse(tmp.path))
            AlertDialog.Builder(this)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    fileFinder.saveFile(result, view.enter_value.text.toString())?.let { file ->
                        val item = FlashcardItem(Uri.parse(file.path), file.name.split("_")[0], file)
                        adapter.addFile(item)

                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun onClick(v: View?) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.app_name)), GALLERY_CODE)
    }
}