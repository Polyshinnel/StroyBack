package com.rbmstroy.rbmbonus.interfaces

import com.rbmstroy.rbmbonus.model.Card

interface CardInterface {
    fun onClicked(card: Card)
}