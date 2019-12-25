package tk.kvakva.beeoptions

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tk.kvakva.beeoptions.databinding.PassUserFragmentBinding


class PassUser : Fragment() {

    companion object {
        fun newInstance() = PassUser()
    }

    private lateinit var viewModel: PassUserViewModel
    private lateinit var binding: PassUserFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.pass_user_fragment,container,false)
        binding.fpu=viewModel

        return binding.root
//        return inflater.inflate(R.layout.pass_user_fragment, container, false)
    }

  /*  override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PassUserViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

    /**
     * Called to do initial creation of a fragment.  This is called after
     * [.onAttach] and before
     * [.onCreateView].
     *
     *
     * Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see [.onActivityCreated].
     *
     *
     * Any restored child fragments will be created before the base
     * `Fragment.onCreate` method returns.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[PassUserViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }
}
