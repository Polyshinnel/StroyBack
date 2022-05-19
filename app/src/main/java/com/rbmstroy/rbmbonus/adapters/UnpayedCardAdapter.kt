package com.rbmstroy.rbmbonus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.databinding.UnpayedUnitBinding
import com.rbmstroy.rbmbonus.interfaces.UnpayedCardInterface
import com.rbmstroy.rbmbonus.model.UnpayedCard

class UnpayedCardAdapter(private val actionListener: UnpayedCardInterface): RecyclerView.Adapter<UnpayedCardAdapter.CardHolder>(), View.OnClickListener {
    var unpayedList = ArrayList<UnpayedCard>()
    class CardHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = UnpayedUnitBinding.bind(item)
        fun bind(card: UnpayedCard) = with(binding){
            unpayedTitle.text = card.UnPayedTitle
            unpayedSumm.text = card.UnPayedSum
            unpayedImg.setImageResource(card.ImgId)
        }
    }

    override fun onClick(view: View) {
        val card = view.tag as UnpayedCard
        actionListener.onClicked(card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.unpayed_unit,parent,false)
        view.rootView.setOnClickListener(this)
        return CardHolder(view)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val card = unpayedList[position]
        holder.itemView.tag = card
        holder.bind(card)
    }

    override fun getItemCount(): Int {
        return unpayedList.size
    }

    fun addCard(card: UnpayedCard){
        unpayedList.add(card)
        notifyDataSetChanged()
    }

    fun deleteCard(){
        unpayedList.clear()
        notifyDataSetChanged()
    }
}