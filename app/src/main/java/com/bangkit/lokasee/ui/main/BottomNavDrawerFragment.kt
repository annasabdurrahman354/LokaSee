package com.bangkit.lokasee.ui.main

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bangkit.lokasee.R
import com.bangkit.lokasee.databinding.BottomNavDrawerMainBinding
import com.bangkit.lokasee.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetBehavior.from
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.abs

/**
 * A [Fragment] which acts as a bottom navigation drawer.
 */
class BottomNavDrawerFragment : Fragment(){

    /**
     * Enumeration of states in which the account picker can be in.
     */
    enum class SandwichState {

        /**
         * The account picker is not visible. The navigation drawer is in its default state.
         */
        CLOSED,

        /**
         * the account picker is visible and open.
         */
        OPEN,

        /**
         * The account picker sandwiching animation is running. The account picker is neither open
         * nor closed.
         */
        SETTLING
    }

    private lateinit var binding: BottomNavDrawerMainBinding

    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(NONE) { from(binding.containerBackground) }

    private val bottomSheetCallback = BottomNavigationDrawerCallback()

    private val sandwichSlideActions = mutableListOf<OnSandwichSlideAction>()

    private val backgroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        val backgroundContext = binding.containerBackground.context
        MaterialShapeDrawable(
            backgroundContext,
            null,
            com.google.android.material.R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                backgroundContext.themeColor(R.attr.colorPrimarySurfaceVariant)
            )
            elevation = resources.getDimension(R.dimen.plane_08)
            initializeElevationOverlay(requireContext())
        }
    }

    private val foregroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        val foregroundContext = binding.containerForeground.context
        MaterialShapeDrawable(
            foregroundContext,
            null,
            com.google.android.material.R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                foregroundContext.themeColor(com.google.android.material.R.attr.colorPrimarySurface)
            )
            elevation = resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(requireContext())
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopEdge(
                    SemiCircleEdgeCutoutTreatment(
                        resources.getDimension(R.dimen.grid_1),
                        resources.getDimension(R.dimen.grid_3),
                        0F,
                        resources.getDimension(R.dimen.navigation_drawer_profile_image_size_padded)
                    )
                )
                .build()
        }
    }

    private var sandwichState: SandwichState = SandwichState.CLOSED
    private var sandwichAnim: ValueAnimator? = null
    private val sandwichInterp by lazy(NONE) { requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent) }
    // Progress value which drives the animation of the sandwiching account picker. Responsible
    // for both calling progress updates and state updates.
    private var sandwichProgress: Float = 0F
        set(value) {
            if (field != value)  {
                onSandwichProgressChanged(value)
                val newState = when(value) {
                    0F -> SandwichState.CLOSED
                    1F -> SandwichState.OPEN
                    else -> SandwichState.SETTLING
                }
                if (sandwichState != newState) onSandwichStateChanged(newState)
                sandwichState = newState
                field = value
            }
        }

    private val closeDrawerOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeDrawerOnBackPressed)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomNavDrawerMainBinding.inflate(inflater, container, false)
        binding.containerForeground.setOnApplyWindowInsetsListener { view, windowInsets ->
            // Record the window's top inset so it can be applied when the bottom sheet is slide up
            // to meet the top edge of the screen.
            view.setTag(
                R.id.tag_system_window_inset_top,
                windowInsets.systemWindowInsetTop
            )
            windowInsets
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            containerBackground.background = backgroundShapeDrawable
            containerForeground.background = foregroundShapeDrawable

            scrimView.setOnClickListener { close() }

            bottomSheetCallback.apply {
                // Scrim view transforms
                addOnSlideAction(AlphaSlideAction(scrimView))
                addOnStateChangedAction(VisibilityStateAction(scrimView))
                // Foreground transforms
                addOnSlideAction(ForegroundSheetTransformSlideAction(
                    binding.containerForeground,
                    foregroundShapeDrawable,
                    binding.imgProfile
                ))
                // Recycler transforms
                addOnStateChangedAction(ScrollToTopStateAction(rvNavigation))
                // Close the sandwiching account picker if open
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        sandwichAnim?.cancel()
                        sandwichProgress = 0F
                    }
                })
                // If the drawer is open, pressing the system back button should close the drawer.
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        closeDrawerOnBackPressed.isEnabled = newState != STATE_HIDDEN
                    }
                })
            }

            imgProfile.setOnClickListener { toggleSandwich() }

            behavior.addBottomSheetCallback(bottomSheetCallback)
            behavior.state = STATE_HIDDEN
        }
    }

    fun toggle() {
        when {
            sandwichState == SandwichState.OPEN -> toggleSandwich()
            behavior.state == STATE_HIDDEN -> open()
            behavior.state == STATE_HIDDEN
                || behavior.state == STATE_HALF_EXPANDED
                || behavior.state == STATE_EXPANDED
                || behavior.state == STATE_COLLAPSED -> close()
        }
    }

    fun open() {
        behavior.state = STATE_HALF_EXPANDED
    }

    fun close() {
        behavior.state = STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    /**
     * Add actions to be run when the slide offset (animation progress) or the sandwiching account
     * picker has changed.
     */
    fun addOnSandwichSlideAction(action: OnSandwichSlideAction) {
        sandwichSlideActions.add(action)
    }

    /**
     * Open or close the account picker "sandwich".
     */
    private fun toggleSandwich() {
        val initialProgress = sandwichProgress
        val newProgress = when (sandwichState) {
            SandwichState.CLOSED -> {
                // Store the original top location of the background container so we can animate
                // the delta between its original top position and the top position needed to just
                // show the account picker RecyclerView, and back again.
                binding.containerBackground.setTag(
                    R.id.tag_view_top_snapshot,
                    binding.containerBackground.top
                )
                1F
            }
            SandwichState.OPEN -> 0F
            SandwichState.SETTLING -> return
        }
        sandwichAnim?.cancel()
        sandwichAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener { sandwichProgress = animatedValue as Float }
            interpolator = sandwichInterp
            duration = (abs(newProgress - initialProgress) *
                resources.getInteger(R.integer.lokasee_motion_duration_medium)).toLong()
        }
        sandwichAnim?.start()
    }

    /**
     * Called each time the value of [sandwichProgress] changes. [progress] is the state of the
     * sandwiching, with 0F being the default [SandwichState.CLOSED] state and 1F being the
     * [SandwichState.OPEN] state.
     */
    private fun onSandwichProgressChanged(progress: Float) {
        binding.run {
            val navProgress = lerp(0F, 1F, 0F, 0.5F, progress)
            val accProgress = lerp(0F, 1F, 0.5F, 1F, progress)

            containerForeground.translationY = (binding.containerForeground.height * 0.15F) * navProgress
            imgProfile.scaleX = 1F - navProgress
            imgProfile.scaleY = 1F - navProgress
            imgProfile.alpha = 1F - navProgress
            containerForeground.alpha = 1F - navProgress
            rvAccount.alpha = accProgress

            foregroundShapeDrawable.interpolation = 1F - navProgress

            // Animate the translationY of the backgroundContainer so just the account picker is
            // peeked above the BottomAppBar.
            containerBackground.translationY = progress *
                ((scrimView.bottom - rvAccount.height
                    - resources.getDimension(R.dimen.bottom_app_bar_height)) -
                    (containerBackground.getTag(R.id.tag_view_top_snapshot) as Int))
        }

        // Call any actions which have been registered to run on progress changes.
        sandwichSlideActions.forEach { it.onSlide(progress) }
    }

    /**
     * Called when the [SandwichState] of the sandwiching account picker has changed.
     */
    private fun onSandwichStateChanged(state: SandwichState) {
        // Change visibility/clickability of views which obstruct user interaction with
        // the account list.
        when (state) {
            SandwichState.OPEN -> {
                binding.run {
                    containerForeground.visibility = View.GONE
                    imgProfile.isClickable = false
                }
            }
            else -> {
                binding.run {
                    containerForeground.visibility = View.VISIBLE
                    imgProfile.isClickable = true
                }
            }
        }
    }
}