package me.varoa.sad.ui.screen.story.list

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import me.varoa.sad.R
import me.varoa.sad.core.data.parcelize
import me.varoa.sad.databinding.FragmentListStoryBinding
import me.varoa.sad.ui.adapter.GenericLoadStateAdapter
import me.varoa.sad.ui.base.BaseEvent.ShowErrorMessage
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.snackbar
import me.varoa.sad.ui.ext.viewBinding
import me.varoa.sad.ui.screen.auth.AuthEvent

class ListStoryFragment : BaseFragment(R.layout.fragment_list_story) {
    private val binding by viewBinding<FragmentListStoryBinding>()
    private val viewModel by viewModels<ListStoryViewModel>()

    private lateinit var storyAdapter: StoryAdapter

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    AuthEvent.LoggedOut -> {
                        snackbar(getString(R.string.info_logged_out))
                        findNavController().navigate(ListStoryFragmentDirections.actionToLogin())
                    }
                    is ShowErrorMessage -> {
                        logcat { "Error : ${event.message}" }
                        modifyErrorLayout("Error : ${event.message}", "🙏")
                        snackbar("Error : ${event.message}", anchorView = binding.bottomAppBar)
                        toggleErrorLayout(true)
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun bindView() {
        // configuring recycler view
        storyAdapter = StoryAdapter(imageLoader) { story, views ->
            val extras = FragmentNavigatorExtras(
                views[0] to "name-${story.id}",
                views[1] to "photo-${story.id}"
            )
            findNavController().navigate(
                ListStoryFragmentDirections.actionToDetailStory(story.parcelize()),
                extras
            )
        }
        storyAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter.withLoadStateHeaderAndFooter(
                header = GenericLoadStateAdapter(storyAdapter::retry),
                footer = GenericLoadStateAdapter(storyAdapter::retry)
            )
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        // collecting flow into recyclerview
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stories.collectLatest(storyAdapter::submitData)
        }

        // handle error state within recyclerview
        viewLifecycleOwner.lifecycleScope.launch {
            storyAdapter.loadStateFlow.map { it.refresh }
                .distinctUntilChanged()
                .collect {
                    if (it is LoadState.NotLoading) {
                        toggleLoading(false)
                        if (storyAdapter.itemCount < 1) {
                            modifyErrorLayout(getString(R.string.err_empty_list), "🤌")
                            toggleErrorLayout(true)
                        } else {
                            toggleErrorLayout(false)
                        }
                    } else if (it is LoadState.Loading) {
                        toggleLoading(true)
                    } else if (it is LoadState.Error) {
                        toggleLoading(false)
                        modifyErrorLayout("Error : ${it.error.message}", "🙏")
                        snackbar("Error : ${it.error.message}", anchorView = binding.bottomAppBar)
                        toggleErrorLayout(true)
                    }
                }
        }

        with(binding) {
            // configuring swipe refresh layout
            swipeRefresh.setOnRefreshListener(storyAdapter::refresh)

            // configuring fab
            fabAdd.setOnClickListener {
                findNavController().navigate(ListStoryFragmentDirections.actionToAddStory())
            }

            // configuring bottom bar
            bottomAppBar.apply {
                this.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_maps -> {
                            findNavController().navigate(
                                ListStoryFragmentDirections.actionToMaps()
                            )
                            true
                        }
                        R.id.action_settings -> {
                            findNavController().navigate(
                                ListStoryFragmentDirections.actionToSettings()
                            )
                            true
                        }
                        R.id.action_logout -> {
                            viewModel.onLogout()
                            true
                        }
                        else -> true
                    }
                }
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun modifyErrorLayout(message: String, emoji: String) {
        binding.layoutError.apply {
            tvMessage.text = message
            tvEmoji.text = emoji
        }
    }

    private fun toggleErrorLayout(flag: Boolean) {
        binding.layoutError.root.isVisible = flag
        binding.recyclerView.isVisible = !flag
    }
}
