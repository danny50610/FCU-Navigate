package iecs.fcu_navigate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFragment extends Fragment {

    public static final String Bundle_KEY_String_List = "StringList";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        String[] arr = new String[]{"無"};
        if (savedInstanceState != null) {
            savedInstanceState.getStringArray(Bundle_KEY_String_List);
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,arr);
        listView.setAdapter(adapter);
        return view;

    }
}
