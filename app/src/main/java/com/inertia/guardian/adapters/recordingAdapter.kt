package com.inertia.guardian.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inertia.guardian.R
import com.inertia.guardian.models.uris
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class recordingAdapter(private val mctx:Context,private val reclist:ArrayList<uris>):RecyclerView.Adapter<recViewHolder>() {
    private val mCtx:Context = mctx
    private val recList:List<uris> = reclist
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(
            R.layout.rec_item,parent,false)
        return recViewHolder(view,recList)
    }

    override fun onBindViewHolder(holder: recViewHolder, position: Int) {
        val r = recList.get(position)
        holder.rec_name?.setText(r.name)
        holder.rec_time?.setText(r.times)
    }

    override fun getItemCount(): Int {
        return recList.size
    }
}

class recViewHolder(v:View,private val reclist: List<uris>):RecyclerView.ViewHolder(v) {
    var rec_name:TextView ?= null
    var rec_time:TextView ?= null
    var play:FloatingActionButton ?= null
    var download:FloatingActionButton ?= null
    var share:FloatingActionButton ?= null


    init {

        rec_name = v.findViewById(R.id.rec_name)
        play = v.findViewById(R.id.play)
        download = v.findViewById(R.id.download)
        share = v.findViewById(R.id.share)
        rec_time = v.findViewById(R.id.rec_time)

        play?.setOnClickListener{
            val ii = adapterPosition
            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            val type = "audio/*"
            val f:File = File(reclist.get(ii).uri)
            val u:Uri = FileProvider.getUriForFile(
                v.context,
                v.context.applicationContext
                    .packageName + ".provider", f
            )
//            i.setDataAndType(Uri.parse(reclist.get(ii).uri), "audio/*");
            i.setDataAndType(u,type)
            v.context.startActivity(i)
        }

        download?.setOnClickListener {
            val ii = adapterPosition
            addpic(reclist.get(ii).uri,v.context)
            val name = reclist.get(ii).name + ".mp3"
            val storage:File = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),"Guardian")

            if (!storage.exists()){
                storage.mkdir()
            }
            if (storage.exists()){
                val audioFile = File(storage,name)
                val source = File(reclist.get(ii).uri)

                copy(source,audioFile)
                Toast.makeText(v.context, "File Saved", Toast.LENGTH_SHORT).show()
            }

        }

        share?.setOnClickListener{
            val ii = adapterPosition
            val i = Intent(Intent.ACTION_SEND)
            i.type = "audio/*"
            i.putExtra(Intent.EXTRA_STREAM,Uri.parse(reclist.get(ii).uri))
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            v.context.startActivity(Intent.createChooser(i,"choose"))
        }
    }

    fun addpic(imagepath:String,c:Context){
        val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagepath)
        val content = Uri.fromFile(f)
        media.setData(content)
        c.sendBroadcast(media)
    }

    @Throws(IOException::class)
    fun copy(src: File?, dst: File?) {
        FileInputStream(src).use { `in` ->
            FileOutputStream(dst).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }

}
