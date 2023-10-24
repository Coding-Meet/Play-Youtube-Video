package com.coding.playyoutubevideo

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class FullScreenYoutubeViewActivity : AppCompatActivity() {


    private lateinit var youTubePlayer: YouTubePlayer
    private var isFullScreen = false


    private val onBackPressedCallback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            if (isFullScreen){

                youTubePlayer.toggleFullscreen()
            }else{
                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_youtube_view)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)

        val fullScreenContainer = findViewById<ViewGroup>(R.id.fullScreenContainer)
        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                isFullScreen = true
                fullScreenContainer.visibility = View.VISIBLE
                fullScreenContainer.addView(fullscreenView)

                // Full Screen remove status bar and navigation bar
                WindowInsetsControllerCompat(window!!,findViewById(R.id.rootView)).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }

            }

            override fun onExitFullscreen() {
                isFullScreen = false
                fullScreenContainer.visibility = View.GONE
                fullScreenContainer.removeAllViews()

                // status bar and navigation bar
                WindowInsetsControllerCompat(window!!,findViewById(R.id.rootView)).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                }
                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR){
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                }

            }
        })

        val youtubePlayerListener = object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@FullScreenYoutubeViewActivity.youTubePlayer = youTubePlayer
                val videoId = "RazWA1DN0Dw"
                youTubePlayer.loadOrCueVideo(lifecycle,videoId, 0f)
            }
        }

        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1)
            .build()

        youtubePlayerView.enableAutomaticInitialization = false
        youtubePlayerView.initialize(youtubePlayerListener,iFramePlayerOptions)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (!isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (isFullScreen){
                youTubePlayer.toggleFullscreen()
            }
        }
    }
}