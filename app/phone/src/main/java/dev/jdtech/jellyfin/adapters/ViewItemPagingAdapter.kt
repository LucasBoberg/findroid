package dev.jdtech.jellyfin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.jdtech.jellyfin.R
import dev.jdtech.jellyfin.databinding.BaseItemBinding
import dev.jdtech.jellyfin.models.JellyfinEpisodeItem
import dev.jdtech.jellyfin.models.JellyfinItem
import dev.jdtech.jellyfin.models.isDownloaded

class ViewItemPagingAdapter(
    private val onClickListener: OnClickListener,
    private val fixedWidth: Boolean = false,
) : PagingDataAdapter<JellyfinItem, ViewItemPagingAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private var binding: BaseItemBinding, private val parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: JellyfinItem, fixedWidth: Boolean) {
            binding.item = item
            binding.itemName.text =
                if (item is JellyfinEpisodeItem) item.seriesName else item.name
            binding.itemCount.visibility =
                if (item.unplayedItemCount != null && item.unplayedItemCount!! > 0) View.VISIBLE else View.GONE
            if (fixedWidth) {
                binding.itemLayout.layoutParams.width =
                    parent.resources.getDimension(R.dimen.overview_media_width).toInt()
                (binding.itemLayout.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = 0
            }

            binding.downloadedIcon.isVisible = item.isDownloaded()

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<JellyfinItem>() {
        override fun areItemsTheSame(oldItem: JellyfinItem, newItem: JellyfinItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JellyfinItem, newItem: JellyfinItem): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BaseItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.itemView.setOnClickListener {
                onClickListener.onClick(item)
            }
            holder.bind(item, fixedWidth)
        }
    }

    class OnClickListener(val clickListener: (item: JellyfinItem) -> Unit) {
        fun onClick(item: JellyfinItem) = clickListener(item)
    }
}
