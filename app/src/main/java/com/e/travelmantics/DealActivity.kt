package com.e.travelmantics

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

    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference
    private var deal:TravelDeal?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseUtil.openFbReference("traveldeals")
        mFirebaseDatabase= FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        deal = intent.getSerializableExtra("Deal") as? TravelDeal
        if (deal == null ){
            deal = TravelDeal()
        }

        txtTitle.setText(deal!!.title)
        txtDescription.setText(deal!!.description)
        txtPrice.setText(deal!!.price)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.save_menu -> {
                saveDeal()
                Toast.makeText(this,"Deal saved",Toast.LENGTH_LONG).show()
                clean()
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
        val title = txtTitle.text.toString()
        val desc = txtDescription.text.toString()
        val price = txtPrice.text.toString()

        val travelDeal = TravelDeal(title,desc,price,"")
        mDatabaseReference.push().setValue(travelDeal)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.save_menu,menu)
        return true
    }
}
