package com.example.gs
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.gs.Pages.CollectionFragment
import com.example.gs.Pages.HomeFragment
import com.example.gs.Pages.SearchFragment

class PagesAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    // Numero di pagine del ViewPager
    override fun getItemCount(): Int = 3

    // Fragment da inserire in ogni pagina del ViewPager2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> HomeFragment()
        1 -> SearchFragment()
        else -> CollectionFragment()
    }
}

// Estensione di ViewPager2 per abilitare/disabilitare lo swipe
class CustomViewPager2(context: Context, attrs: AttributeSet? = null) : ViewPager2(context, attrs) {
    private var isPagingEnabled = true

    init {
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (!isPagingEnabled) {
                    // Consuma l'evento di scroll se lo swipe è disabilitato
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
        })
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isPagingEnabled = enabled
        this.isUserInputEnabled = enabled  // ViewPager2 ha questa proprietà built-in
    }
}