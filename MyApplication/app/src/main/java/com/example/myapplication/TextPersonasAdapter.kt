package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TextPersonasAdapter(private val personas: List<persona>): RecyclerView.Adapter<TextPersonasAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.personas_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = personas[position]
        holder.bind(person)
    }

    override fun getItemCount(): Int = personas.size

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {


        private val full = view.findViewById<TextView>(R.id.fullname)
        private val phon = view.findViewById<TextView>(R.id.phone)
        fun bind(persona: persona) {
            full.text = persona.fullname
            phon.text = persona.phone
        }
    }

}