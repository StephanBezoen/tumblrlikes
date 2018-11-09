package nl.acidcats.tumblrlikes.ui.screens.setup_screen

import nl.acidcats.tumblrlikes.BuildConfig
import nl.acidcats.tumblrlikes.core.usecases.appsetup.TumblrBlogUseCase
import nl.acidcats.tumblrlikes.ui.Broadcasts
import nl.acidcats.tumblrlikes.ui.screens.base.BasePresenterImpl
import javax.inject.Inject

/**
 * Created on 30/10/2018.
 */
class SetupScreenPresenter @Inject constructor() : BasePresenterImpl<SetupScreenContract.View>(), SetupScreenContract.Presenter {

    @Inject
    lateinit var tumblrBlogUseCase: TumblrBlogUseCase

    override fun onViewCreated() {
        registerSubscription(
                tumblrBlogUseCase
                        .getTumblrBlog()
                        .subscribe {
                            var blog = it

                            @Suppress("SENSELESS_COMPARISON")
                            if (blog == null && BuildConfig.DEBUG && BuildConfig.BLOG != null && !BuildConfig.BLOG.isEmpty()) {
                                blog = BuildConfig.BLOG
                            }

                            if (blog == null) {
                                getView()?.setTumblrBlogText("")
                                getView()?.enableOkButton(false)
                            } else {
                                if (blog.endsWith(SetupScreenContract.BLOG_EXT)) {
                                    blog = blog.replace(SetupScreenContract.BLOG_EXT, "")
                                }

                                getView()?.setTumblrBlogText(blog)
                                getView()?.enableOkButton(true)
                            }
                        }
        )
    }

    override fun onBlogTextChanged(blog: String) {
        getView()?.enableOkButton(!blog.isEmpty())
    }

    override fun onSetupDone(blog: String) {
        getView()?.enableOkButton(false)

        registerSubscription(
                tumblrBlogUseCase.setTumblrBlog(blog).subscribe { notify(Broadcasts.SETUP_COMPLETE) }
        )
    }
}
