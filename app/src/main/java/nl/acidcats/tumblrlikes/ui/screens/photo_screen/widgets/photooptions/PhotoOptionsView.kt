package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.popup_photo_menu.view.*
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.core.viewmodels.ValidPhotoViewModel
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets.photooptions.PhotoOptionsContract.HideFlow
import nl.acidcats.tumblrlikes.ui.viewmodels.PhotoScreenViewModel

/**
 * Created on 03/12/2018.
 */
class PhotoOptionsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr),
        PhotoOptionsContract.View {
    private var hideAnimator: ViewPropertyAnimator? = null
    private var showAnimator: ViewPropertyAnimator? = null
    private var hideDuration = 0L
    private var showDuration = 0L
    private lateinit var optionSelectedListener: OptionSelectedListener

    init {
        LayoutInflater.from(context).inflate(R.layout.popup_photo_menu, this, true)

        favoriteButton.setOnClickListener { optionSelectedListener.invoke(PhotoOptionsContract.Option.FAVORITE) }
        likeButton.setOnClickListener { optionSelectedListener.invoke(PhotoOptionsContract.Option.LIKE) }
        hideButton.setOnClickListener { optionSelectedListener.invoke(PhotoOptionsContract.Option.HIDE) }
        cameraButton.setOnClickListener { optionSelectedListener.invoke(PhotoOptionsContract.Option.SAVE) }

        container.setOnClickListener { hide(HideFlow.ANIMATED) }

        hideDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        showDuration = hideDuration

        hide(HideFlow.INSTANT)
    }

    override fun initViewModel(viewModel: PhotoScreenViewModel, lifecycleOwner: LifecycleOwner) {
        viewModel.getPhoto().observe(lifecycleOwner, Observer { updateUI(it) })
    }

    private fun updateUI(viewModel: ValidPhotoViewModel) {
        val favoriteIconId = if (viewModel.isFavorite) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp
        favoriteButton.setCompoundDrawablesWithIntrinsicBounds(favoriteIconId, 0, 0, 0)

        val likedIconId = if (viewModel.isLiked) R.drawable.ic_thumb_up_black_24dp else R.drawable.ic_thumbs_up_down_black_24dp
        likeButton.setCompoundDrawablesWithIntrinsicBounds(likedIconId, 0, 0, 0)
        likeButton.text = context.getString(if (viewModel.isLiked) R.string.photo_action_liked else R.string.photo_action_like)

        viewCountText.text = context.getString(R.string.view_count, viewModel.viewCount)
    }

    override fun show() {
        hideAnimator?.cancel()
        hideAnimator = null

        visibility = View.VISIBLE
        showAnimator ?: startShowAnimation()
    }

    override fun hide(hideFlow: HideFlow) {
        showAnimator?.cancel()
        showAnimator = null

        when (hideFlow) {
            HideFlow.INSTANT -> {
                visibility = View.GONE
                alpha = 0f
            }
            HideFlow.ANIMATED -> hideAnimator ?: startHideAnimation()
        }
    }

    private fun startHideAnimation() {
        hideAnimator = animate()
                .alpha(0f)
                .setDuration(hideDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE

                        hideAnimator = null
                    }
                })

        cardContainer.animate()
                .translationY(20f)
                .setDuration(showDuration)
    }

    private fun startShowAnimation() {
        showAnimator = animate()
                .alpha(1f)
                .setDuration(showDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        showAnimator = null
                    }
                })

        cardContainer.translationY = -20f
        cardContainer.animate()
                .translationY(0f)
                .setDuration(showDuration)
    }

    override fun setOptionSelectedListener(listener: OptionSelectedListener) {
        optionSelectedListener = listener
    }

    override fun onDestroyView() {
        favoriteButton.setOnClickListener(null)
        likeButton.setOnClickListener(null)
        hideButton.setOnClickListener(null)
        cameraButton.setOnClickListener(null)

        hideAnimator = null
        showAnimator = null
    }
}