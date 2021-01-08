package fr.skkay.instabus.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.skkay.instabus.R
import fr.skkay.instabus.adapters.StationAdapter
import fr.skkay.instabus.dataclass.StationItem
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = generateDummyList(100)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = StationAdapter(list)
        }
    }

    companion object {
        /**
         * @return A new instance of fragment ListFragment.
         */
        @JvmStatic
        fun newInstance() = ListFragment()
    }

    private fun generateDummyList(size: Int): List<StationItem> {
        val list = ArrayList<StationItem>()

        for (i in 0 until size) {
            val item = StationItem("Item $i")
            list += item
        }

        return list
    }
}