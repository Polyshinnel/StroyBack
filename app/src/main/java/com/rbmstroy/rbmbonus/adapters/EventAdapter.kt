package com.rbmstroy.rbmbonus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.databinding.EventUnitBinding
import com.rbmstroy.rbmbonus.model.Event

class EventAdapter: RecyclerView.Adapter<EventAdapter.EventHolder>() {
    var eventList = ArrayList<Event>()
    class EventHolder(item:View): RecyclerView.ViewHolder(item) {
        private val binding = EventUnitBinding.bind(item)
        fun bind(event: Event) = with(binding){
            eventImg.setImageResource(event.imgId)
            eventTitle.text = event.title
            eventText.text = event.eventText
            eventPrice.text = event.eventPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_unit,parent,false)
        return EventHolder(view)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    fun addEvent(event: Event){
        eventList.add(event)
        notifyDataSetChanged()
    }

    fun deleteItem(){
        eventList.clear()
        notifyDataSetChanged()
    }
}