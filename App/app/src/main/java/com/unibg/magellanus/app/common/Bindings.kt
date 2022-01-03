package com.unibg.magellanus.app.common

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.unibg.magellanus.app.R

@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: Uri?) {
    Glide.with(context).load(url).centerCrop()
        .placeholder(R.drawable.ic_user_profile_placeholder)
        .into(this)
}