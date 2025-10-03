//package com.example.fopsmart.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.fopsmart.databinding.ItemCarouselBinding
//
//class CarouselAdapter (
//    private val context: Context,
//    private val carouselItem: List<CarouselItem>,
//    private val onItemClick: ((CarouselItem, Int) -> Unit)? = null
//) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): CarouselViewHolder {
//        val binding = ItemCarouselBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return CarouselViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: CarouselAdapter.CarouselViewHolder, position: Int) {
//        holder.bind(carouselItem[position], position)
//    }
//
//    override fun getItemCount(): Int = carouselItem.size
//
//    inner class CarouselViewHolder(
//        private val binding: ItemCarouselBinding
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(item: CarouselItem, position: Int) {
//            with(binding) {
//                titleText.text = item.title
//                descriptuonText.text = item.description
//
//                Glide.with(context)
//                    .load(item.imageUrl)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.placeholder_image)
//                    .centerCrop()
//                    .into(imageView)
//
//                // Обробка кліків
//                root.setOnClickListener {
//                    val adapterPosition = adapterPosition
//                    if (adapterPosition != RecyclerView.NO_POSITION) {
//                        onItemClick?.invoke(item, adapterPosition) ?: run {
//                            // Дефолтна поведінка
//                            Toast.makeText(
//                                context,
//                                "Clicked: ${item.title}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//}
//
//data class CarouselItem(
//    val id: String = "",
//    val title: String,
//    val description: String,
//    val imageUrl: String,
//    val category: String = "",
//    val isActive: Boolean = true
//)