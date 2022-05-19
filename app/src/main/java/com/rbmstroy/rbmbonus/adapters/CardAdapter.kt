package com.rbmstroy.rbmbonus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.databinding.CardUnitBinding
import com.rbmstroy.rbmbonus.interfaces.CardInterface
import com.rbmstroy.rbmbonus.model.Card

class CardAdapter(private val actionListener: CardInterface): RecyclerView.Adapter<CardAdapter.CardHolder>(), View.OnClickListener {
    var cardList = ArrayList<Card>()
    class CardHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = CardUnitBinding.bind(item)
        fun bind(card: Card) = with(binding){
            itemImage.setImageResource(card.imgId)
            itemTitle.text = card.title
        }
    }

    override fun onClick(view: View) {
        val card = view.tag as Card
        actionListener.onClicked(card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_unit,parent,false)
        view.rootView.setOnClickListener(this)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = cardList[position]
        holder.itemView.tag = card
        holder.bind(card)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun addCard(card: Card){
        cardList.add(card)
        notifyDataSetChanged()
    }
}