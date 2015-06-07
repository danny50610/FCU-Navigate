package iecs.fcu_navigate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import iecs.fcu_navigate.database.MarkerContract;

public class ListFragment extends Fragment {

    public static final String Bundle_KEY_String_List = "StringList";

    private onItemClickCallBacks mCallback;

    private int level = 99;

    public MarkerContract.Item[] mItems = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);
        String[] arr = new String[]{"無"};

        Bundle args = this.getArguments();
        if (args != null) {
            arr = args.getStringArray(Bundle_KEY_String_List);
            if (arr == null) {
                arr = new String[]{"無"};
            }

            level = args.getInt(MarkerSelectorActivity.Bundle_KEY_LISTVIEW_LEVEL);

            mItems = (MarkerContract.Item[]) args.getSerializable("Items");
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, arr);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onItemClick(parent, view, position, id);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onItemClickCallBacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement onItemClickCallBacks.");
        }
    }

    public int getLevel() {
        return level;
    }

    public static interface onItemClickCallBacks extends AdapterView.OnItemClickListener{
    }
}
