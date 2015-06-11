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

import java.io.Serializable;

import iecs.fcu_navigate.database.MarkerContract;

public class ListFragment extends Fragment {

    public static final String Bundle_KEY_LISTVIEW_LEVEL = "ListView_Level";

    public static final String Bundle_KEY_ITEM_List = "ItemList";

    private onItemClickCallBacks mCallback;

    private int level = 99;

    public ListItem[] mItems = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listView = (ListView)view.findViewById(R.id.listView);

        Bundle args = this.getArguments();
        if (args != null) {
            level = args.getInt(Bundle_KEY_LISTVIEW_LEVEL);

            mItems = (ListItem[]) args.getSerializable(Bundle_KEY_ITEM_List);
        }

        if (mItems == null || mItems.length == 0) {
            mItems = new MarkerContract.Item[]{MarkerContract.ItemNothing};
        }

        ArrayAdapter<ListItem> adapter =
                new ArrayAdapter<ListItem>(getActivity(),
                        android.R.layout.simple_list_item_1, mItems);
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

    public static interface ListItem extends Serializable {
        public boolean isVirtual();
    }
}
