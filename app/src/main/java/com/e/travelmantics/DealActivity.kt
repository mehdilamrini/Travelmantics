package com.e.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.Intent.createChooser
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.data.model.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*



class DealActivity : AppCompatActivity() {

    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private var PICTURE_RESULT = 42
    private var deal: TravelDeal? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        deal = intent.getSerializableExtra("Deal") as? TravelDeal
        if (deal == null) {
            deal = TravelDeal()
        }

        txtTitle.setText(deal!!.title)
        txtDescription.setText(deal!!.description)
        txtPrice.setText(deal!!.price)

        if (!deal?.imageUrl.isNullOrBlank())
        showImage(deal?.imageUrl)

        if(FirebaseUtil.isAdmin)
        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true)
            startActivityForResult(
                createChooser(intent,"Insert Picture"),
                PICTURE_RESULT
            )
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show()
                clean()
                true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                backToList()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICTURE_RESULT  && resultCode == Activity.RESULT_OK  ){
            val imageUri : Uri = data!!.data!!
            val ref:StorageReference= FirebaseUtil.mStorageRef.child(imageUri.lastPathSegment!!)
            val uploadTask= ref.putFile(imageUri)

            progress_upload.visibility = View.VISIBLE

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                // Continue with the task to get the download URL
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val downloadURL = downloadUri!!.toString()
                    deal?.imageUrl = downloadURL
                    val pictureName= task.result!!.path
                    deal?.imageName = pictureName
                    Log.d("Upload imageName",pictureName)
                    Log.d("Upload imageUrl",downloadURL)

                    showImage(downloadURL)
                    progress_upload.visibility = View.GONE

                } else {
                    // Handle failures
                    // ...
                    progress_upload.visibility = View.GONE

                }
            }

        }




    }

    private fun clean() {
        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")
        txtTitle.requestFocus()
    }

    private fun saveDeal() {
        deal?.title = txtTitle.text.toString()
        deal?.description = txtDescription.text.toString()
        deal?.price = txtPrice.text.toString()

        if (deal?.id == null) {
            mDatabaseReference.push().setValue(deal)
        } else {
            mDatabaseReference.child(deal!!.id).setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the deal before delete it ", Toast.LENGTH_LONG).show()
            return
        }
        if (deal?.imageName!!.isNotEmpty()) {
                val picRef = FirebaseUtil.mStorageRef.child(deal?.imageName!!.substringAfterLast("deals_pictures"))
            picRef.delete().addOnCanceledListener {
                Log.d("Delete Image","Image Successfully Deleted")

            }
                .addOnFailureListener {
                    Log.d("Delete Image"," Delete Image Failed $it")

                }
            }
        mDatabaseReference.child(deal!!.id).removeValue()
    }

    private fun backToList() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)

    }

    private fun enableEditTexts(boolean:Boolean){
        txtTitle.isEnabled = boolean
        txtPrice.isEnabled = boolean
        txtDescription.isEnabled = boolean
    }

    private fun showImage(url :String?){
        if (url!!.isNotEmpty()){
            val width:Int= Resources.getSystem().displayMetrics.widthPixels
            Picasso.with(this)
                .load(url)
                .resize(width,width*2/3)
                .centerCrop()
                .into(image)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.save_menu, menu)

        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).isVisible = true
            menu.findItem(R.id.save_menu).isVisible = true
            enableEditTexts(true)
        }
        else {
            menu.findItem(R.id.delete_menu).isVisible = false
            menu.findItem(R.id.save_menu).isVisible = false
            enableEditTexts(false)
        }

            return true
    }
}
