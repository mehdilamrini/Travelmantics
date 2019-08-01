package com.e.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val adapter = DealAdapter()
        rvDeals.adapter = adapter
        val dealsLayoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        rvDeals.layoutManager = dealsLayoutManager
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.insert_menu -> {
                val intent = Intent(this,DealActivity::class.java)
                this.startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

}
