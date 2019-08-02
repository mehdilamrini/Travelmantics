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
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.firebase.ui.auth.AuthUI


class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.list_activity_menu, menu)
        val insertMenu:MenuItem= menu!!.findItem(R.id.insert_menu)
        insertMenu.isVisible = FirebaseUtil.isAdmin==true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.insert_menu -> {
                val intent = Intent(this, DealActivity::class.java)
                this.startActivity(intent)
                true
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        FirebaseUtil.attachListener()
                    }
                FirebaseUtil.dettachListener()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.dettachListener()
    }

    override fun onResume() {
        super.onResume()
        val adapter = DealAdapter(this)
        rvDeals.adapter = adapter
        val dealsLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvDeals.layoutManager = dealsLayoutManager
        FirebaseUtil.attachListener()
    }

    fun showMenu(){
        invalidateOptionsMenu()
    }
}
