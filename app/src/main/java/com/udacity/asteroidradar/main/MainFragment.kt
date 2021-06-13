package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.entity.DatabasePictureOfDay
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel> { MainViewModel.Factory(requireActivity().application) }
    private lateinit var binding: FragmentMainBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val asteroidAdapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })
        asteroidAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding = FragmentMainBinding.inflate(inflater).apply {
            lifecycleOwner = this@MainFragment
            viewModel = this.viewModel
            asteroidRecycler.adapter = asteroidAdapter
        }

        lifecycleScope.launch { viewModel.pictureOfDay.filterNotNull().collect { updatePicture(it) } }

        lifecycleScope.launch { viewModel.asteroids.collect { asteroidAdapter.submitList(it) } }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        lifecycleScope.launch { viewModel.currentAsteroidListMode.collect { updateMenu(menu, it) } }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_saved_menu -> viewModel.setListMode(AsteroidListMode.ALL)
            R.id.show_today_menu -> viewModel.setListMode(AsteroidListMode.TODAY)
            R.id.show_week_menu -> viewModel.setListMode(AsteroidListMode.WEEK)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun updatePicture(picture: DatabasePictureOfDay) {
        binding.statusLoadingWheel.visibility = View.VISIBLE
        Picasso.with(requireContext())
            .load(picture.url)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .into(binding.activityMainImageOfTheDay, object : Callback {
                override fun onSuccess() {
                    binding.statusLoadingWheel.visibility = View.GONE
                    binding.activityMainImageOfTheDay.contentDescription =
                        requireContext().getString(R.string.nasa_picture_of_day_content_description_format, picture.title)
                }

                override fun onError() {
                    binding.statusLoadingWheel.visibility = View.GONE
                    binding.activityMainImageOfTheDay.contentDescription =
                        requireContext().getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
                }
            })
    }

    private fun updateMenu(menu: Menu, mode: AsteroidListMode) {
        menu.findItem(
            when (mode) {
                AsteroidListMode.WEEK -> R.id.show_week_menu
                AsteroidListMode.TODAY -> R.id.show_today_menu
                AsteroidListMode.ALL -> R.id.show_saved_menu
            }
        )?.isChecked = true
    }

}
