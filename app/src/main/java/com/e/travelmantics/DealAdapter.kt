package com.e.travelmantics

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.rv_row.view.*

class DealAdapter(callerActivity : ListActivity) : RecyclerView.Adapter<DealViewHolder>() {

    private lateinit var context: Context
    private var travelDeals = ArrayList<TravelDeal>()
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mChildListener: ChildEventListener

    init {
        FirebaseUtil.openFbReference("traveldeals",callerActivity)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        travelDeals = FirebaseUtil.mDeals
        mChildListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val td: TravelDeal = dataSnapshot.getValue(TravelDeal::class.java)!!
                Log.d("DealAdapter", td.title)
                td.id = dataSnapshot.key
                travelDeals.add(td)
                notifyItemInserted(travelDeals.size - 1)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        }
        mDatabaseReference.addChildEventListener(mChildListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        context = parent.context
        return DealViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false)
        )

    }


    override fun getItemCount(): Int {
        return travelDeals.size
    }


    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.title_text.text = travelDeals[position].title
        holder.price_text.text = travelDeals[position].price
        holder.desc_text.text = travelDeals[position].description

        holder.container.setOnClickListener {
            val intent = Intent(context,DealActivity::class.java)
            val selectedDeal = travelDeals[position]
            intent.putExtra("Deal",selectedDeal)
            context.startActivity(intent)
        }

    }


}

class DealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title_text = view.tvTitle
    val price_text = view.tvPrice
    val desc_text = view.tvDescription
    val container = view.rvCons
}