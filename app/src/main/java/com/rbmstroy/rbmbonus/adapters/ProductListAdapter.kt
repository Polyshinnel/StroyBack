package com.rbmstroy.rbmbonus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rbmstroy.rbmbonus.R
import com.rbmstroy.rbmbonus.databinding.ProductCardUnitBinding
import com.rbmstroy.rbmbonus.model.Product
import com.squareup.picasso.Picasso

class ProductListAdapter: RecyclerView.Adapter<ProductListAdapter.ProductHolder>() {
    var productList = ArrayList<Product>()
    class ProductHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = ProductCardUnitBinding.bind(item)
        fun bind(product: Product) = with(binding){
            Picasso.get().load(product.productImg).into(productImg)
            productName.text = product.productName
            productBrand.text = product.productBrand
            productPrice.text = product.productPrice
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_card_unit,parent,false)
        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun addProducts(product: Product){
        productList.add(product)
        notifyDataSetChanged()
    }


}