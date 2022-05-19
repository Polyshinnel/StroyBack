package com.rbmstroy.rbmbonus.tabs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rbmstroy.rbmbonus.CardShop
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.adapters.CardAdapter
import com.rbmstroy.rbmbonus.interfaces.CardInterface
import com.rbmstroy.rbmbonus.model.Card


class CardTab : Fragment() {
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: CardAdapter
    private val imageIdList = listOf(R.drawable.auchan,R.drawable.sportmaster,R.drawable.letual,R.drawable.mvideo,R.drawable.eldorado)
    private val titleList = listOf("Ашан","Спортмастер","Лэтуаль","М.Видео","Эльдорадо")
    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewCard =  inflater.inflate(R.layout.fragment_card_tab, container, false)
        recyclerView = viewCard.findViewById(R.id.cardRv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CardAdapter(object : CardInterface{
            override fun onClicked(card: Card) {
                Log.d("MyLog1","${card.imgId} ${card.title}")
                val intent = Intent(context,CardShop::class.java)

                val imgId = card.imgId.toString().toInt()
                intent.putExtra("imgId", imgId)
                intent.putExtra("title", "${card.title}")

                startActivity(intent)
            }

        })
        recyclerView.adapter = adapter


        for (index in 0..4){
            var card = Card(imageIdList[index],titleList[index])
            adapter.addCard(card)
            counter++
        }

        return viewCard


    }


}