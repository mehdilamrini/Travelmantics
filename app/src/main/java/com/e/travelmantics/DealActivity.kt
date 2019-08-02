package com.e.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class DealActivity : AppCompatActivity() {

    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private var deal: TravelDeal? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        deal = intent.getSerializableExtra("Deal") as? TravelDeal
        if (deal == null) {
            deal = TravelDeal()
        }

        txtTitle.setText(deal!!.title)
        txtDescription.setText(deal!!.description)
        txtPrice.setText(deal!!.price)

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

    private fun clean() {
        txtTitle.setText("")
        txtDescription.setText("")
        txtPrice.setText("")
        txtTitle.requestFocus()
    }

    private fun saveDeal() {
        /* val title = txtTitle.text.toString()
         val desc = txtDescription.text.toString()
         val price = txtPrice.text.toString()*/

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
