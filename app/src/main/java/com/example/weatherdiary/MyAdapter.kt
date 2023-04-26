package com.example.weatherdiary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter : RecyclerView.Adapter<MyViewHolder>{
    lateinit var context : Context
    var items = listOf<Item>()

    constructor(context: Context, items : List<Item>){
        this.context = context
        this.items = items
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.weatherTypeView.text = items[position].weatherType
        holder.imageView.setImageResource(items[position].image)
    }
}