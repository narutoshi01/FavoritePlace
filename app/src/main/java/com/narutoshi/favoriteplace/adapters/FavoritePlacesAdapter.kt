package com.narutoshi.favoriteplace.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.narutoshi.favoriteplace.DefaultImage
import com.narutoshi.favoriteplace.R
import com.narutoshi.favoriteplace.models.FavoritePlaceModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_favorite_place.view.*

class FavoritePlacesAdapter(
    private val context: Context,
    private val list: RealmResults<FavoritePlaceModel>
) : RecyclerView.Adapter<FavoritePlacesAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_favorite_place, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        val itemView = holder.itemView

        itemView.tv_title.text = model?.title
        itemView.tv_date.text = model?.date
        itemView.tv_description.text = model?.description

        if(model?.imageString == DefaultImage.STRING) {
            itemView.iv_place.setImageResource(DefaultImage.RESOURCE)
        } else {
            val imageUri = Uri.parse(model?.imageString)
            itemView.iv_place.setImageURI(imageUri)
        }

        itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onCLick(model!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onCLick(model: FavoritePlaceModel)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}