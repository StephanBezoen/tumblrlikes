package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import nl.acidcats.tumblrlikes.R
import nl.acidcats.tumblrlikes.R.*
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.HideFlow.ANIMATED
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.PhotoScreenContract.HideFlow.INSTANT
import nl.acidcats.tumblrlikes.ui.screens.photo_screen.viewmodels.PhotoOptionsViewModel

/**
 * Created on 01/11/2018.
 */
class PhotoActionDialog @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.btn_favorite)
    lateinit var favoriteButton: TextView
    @BindView(R.id.btn_hide)
    lateinit var hideButton: TextView
    @BindView(R.id.btn_like)
    lateinit var likeButton: TextView
    @BindView(R.id.btn_unlike)
    lateinit var unlikeButton: TextView
    @BindView(R.id.background)
    lateinit var background: View
    @BindView(R.id.txt_view_count)
    lateinit var viewCountText: TextView

    private var photoActionListener: PhotoScreenContract.PhotoActionListener? = null
    private var unbinder: Unbinder
    private lateinit var viewModel: PhotoOptionsViewModel
    private var hideAnimator: ViewPropertyAnimator? = null
    private var hideDuration = 0L

    init {
        val view = LayoutInflater.from(context).inflate(layout.popup_photo_menu, this, true)
        unbinder = ButterKnife.bind(this, view)

        favoriteButton.setOnClickListener { photoActionListener?.onUpdatePhotoFavorite(viewModel.photoId, !viewModel.isPhotoFavorite) }
        likeButton.setOnClickListener { photoActionListener?.onUpdatePhotoLike(viewModel.photoId, true) }
        unlikeButton.setOnClickListener { photoActionListener?.onUpdatePhotoLike(viewModel.photoId, false) }
        hideButton.setOnClickListener { photoActionListener?.onHidePhoto(viewModel.photoId) }

        background.setOnClickListener { hide(ANIMATED) }

        hideDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        hide(INSTANT)
    }

    fun show(viewModel: PhotoOptionsViewModel) {
        updateViewModel(viewModel)

        visibility = View.VISIBLE
        alpha = 1f
    }

    fun updateViewModel(viewModel: PhotoOptionsViewModel) {
        this.viewModel = viewModel

        updateUI()
    }

    private fun updateUI() {
        val iconId = if (viewModel.isPhotoFavorite) drawable.ic_star_black_24dp else drawable.ic_star_border_black_24dp
        favoriteButton.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0)

        if (viewModel.isPhotoLiked) {
            likeButton.text = context.getString(string.photo_action_like_count, 1)
            unlikeButton.text = context.getString(string.photo_action_unlike)
        } else {
            likeButton.text = context.getString(string.photo_action_like)
            unlikeButton.text = context.getString(string.photo_action_unlike)
        }

        viewCountText.text = context.getString(R.string.view_count, viewModel.viewCount)
    }

    fun hide(hideFlow: PhotoScreenContract.HideFlow) {
        when (hideFlow) {
            INSTANT -> visibility = View.GONE
            ANIMATED -> hideAnimator ?: startHideAnimation()
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
    }

    fun setPhotoActionListener(listener: PhotoScreenContract.PhotoActionListener) {
        photoActionListener = listener
    }

    fun onDestroyView() {
        favoriteButton.setOnClickListener(null)
        likeButton.setOnClickListener(null)
        unlikeButton.setOnClickListener(null)
        hideButton.setOnClickListener(null)

        hideAnimator = null

        unbinder.unbind()
    }
}