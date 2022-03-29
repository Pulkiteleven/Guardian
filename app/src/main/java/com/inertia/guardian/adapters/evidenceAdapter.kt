package com.inertia.guardian.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.inertia.guardian.R
import org.intellij.lang.annotations.RegExp
import java.io.File

class evidenceAdapter(private val mctx:Context,private val evidencelist:ArrayList<String>, private val name:String):RecyclerView.Adapter<evidenceViewHolder>() {
    private val mCtx:Context = mctx
    private val evidenceList:List<String> = evidencelist
    private val Name:String = name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): evidenceViewHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.item_evidence,parent,false)
        return evidenceViewHolder(view,evidenceList,Name)
    }

    override fun onBindViewHolder(holder: evidenceViewHolder, position: Int) {
        val c = evidenceList.get(position)
        holder.name?.setText(c)
    }

    override fun getItemCount(): Int {
        return evidenceList.size
    }

}

class evidenceViewHolder(v:View, private val evidencelist: List<String>, namee:String):RecyclerView.ViewHolder(v) {
    var name:TextView ?= null
    var open:MaterialButton ?= null
    var downlaod:MaterialButton ?= null
    var share:MaterialButton ?= null


    val storage: File = File(
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) , "/Guardian/Evidences/" + namee)


    init {
        name = v.findViewById(R.id.evi_name)
        open = v.findViewById(R.id.open)
        downlaod = v.findViewById(R.id.download)
        share = v.findViewById(R.id.share)
        if (!storage.exists()){
            storage.mkdir()
        }

        downlaod?.setOnClickListener {
            val i = adapterPosition
            val url = evidencelist.get(i)
            downloader(url,namee,v)
        }
        open?.setOnClickListener {
            val i = adapterPosition
            val url = evidencelist.get(i)
            opener(url,namee,v)
        }
        share?.setOnClickListener {
            shareer(namee,v)
        }


    }

    private fun downloader(url:String,folder:String,v:View){

        val uri:Uri = Uri.parse(url)

        val ext = MimeTypeMap.getFileExtensionFromUrl(url)


        val filename: String = url.substring(url.length - 10) + ext
        val storage: File = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) , "/Guardian/Evidences/" + folder + "/" + filename)

        if (!storage.exists()) {
            val request: DownloadManager.Request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle(filename)
            request.setDescription("Downloading File")

            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/Guardian/Evidences/" + folder + "/" + filename
            )

            val manager: DownloadManager =
                v.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val ref = manager.enqueue(request)
            manager.getUriForDownloadedFile(ref)
        }
        else{
            Toast.makeText(v.context, "The File Already Exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun opener(url:String,folder:String,v:View){

        val uri:Uri = Uri.parse(url)
        val ext = MimeTypeMap.getFileExtensionFromUrl(url)
        val filename: String = url.substring(url.length - 10) + ext
        val storage: File = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) , "/Guardian/Evidences/" + folder + "/" + filename)

        val extension: String = url.substring(url.lastIndexOf("."))
        var type:String = "*/*"
        if (extension.equals(".mp3") || extension.equals(".wav")){
            type = "audio/*"
        }
        else if(extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png")){
            type = "image/*"
        }
        else if (extension.equals(".mp4") || extension.equals(".mkv")){
            type = "video/*"
        }


        if (!storage.exists()){
            Toast.makeText(v.context, "The File is Currently Downloading", Toast.LENGTH_SHORT).show()
            downloader(url,folder,v)
        }
        else{
            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //i.setDataAndType(Uri.fromFile(storage), "*/*");

            val u:Uri = FileProvider.getUriForFile(
                v.context,
                v.context.applicationContext
                    .packageName + ".provider", storage
            )
            i.setDataAndType(u, type)

            v.context.startActivity(i)
        }


    }

    fun shareer(folder: String,v:View){
        val ii = adapterPosition
        val url = evidencelist.get(ii)
        val i = Intent(Intent.ACTION_SEND)
        i.type = "*/*"
        val filename: String = url.substring(url.length - 10)
        val storage: File = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) , "/Guardian/Evidences/" + folder + "/" + filename)

        val u:Uri = FileProvider.getUriForFile(
            v.context,
            v.context.applicationContext
                .packageName + ".provider", storage
        )

        i.putExtra(Intent.EXTRA_STREAM,u)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        v.context.startActivity(Intent.createChooser(i,"choose"))
    }

}
