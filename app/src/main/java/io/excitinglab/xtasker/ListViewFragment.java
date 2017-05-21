package io.excitinglab.xtasker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;

public class ListViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private int mParam2;

    private OnFragmentInteractionListener mListener;

    public ListViewFragment() {

    }

    public static ListViewFragment newInstance(int param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);

//        selectedID = param2;

        fragment.setArguments(args);
        return fragment;
    }

    private static int selectedID;
    private boolean SHOW_COMPLETED;
    private int SORTLIST = -1;

    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
//            selectedID = mParam2;
        }
    }

    MyAdapter adapter;
    SwipeActionAdapter adapter1;

    int s;

    @Override
    public void onStart(){
        super.onStart();

//        Log.e("SORTLIST", String.valueOf(SORTLIST));

        mDatabaseHelper = mDatabaseHelper.getInstance(getActivity());
        mListView = (ListView) getActivity().findViewById(R.id.listView);

        Lists list = mDatabaseHelper.getListByID(selectedID);
        final ArrayList<Task> tasks = new ArrayList<>();

//        tasks.addAll(mDatabaseHelper.getActiveTasks(list));
        if (SORTLIST == -1) {
            SORTLIST = list.getSort();
            tasks.addAll(mDatabaseHelper.getActiveTasksNoSort(list, SORTLIST));

        }
        else {
            tasks.addAll(mDatabaseHelper.getActiveTasks(list, SORTLIST));
        }

        s = tasks.size();

        if (SHOW_COMPLETED) {
            tasks.addAll(mDatabaseHelper.getUnactiveTasks(list));
        }

        adapter = new MyAdapter(getActivity(),tasks);
        mListView.setAdapter(adapter);

        adapter1 = new SwipeActionAdapter(adapter);
        adapter1.setListView(mListView);
        mListView.setAdapter(adapter1);

        adapter1.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);


        adapter1.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(direction.isLeft()) return true; // Change this to false to disable left swipes
                if(direction.isRight()) return true;
                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){

                return direction == SwipeDirection.DIRECTION_FAR_LEFT || direction == SwipeDirection.DIRECTION_FAR_RIGHT;
            }

            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    final int position = positionList[i];
                    String dir = "";

                    if (direction == SwipeDirection.DIRECTION_FAR_LEFT) {

//                        new AlertDialog.Builder(getActivity())
//                                .setTitle("Delete task")
//                                .setMessage("Are you sure you want to delete this task?")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        mDatabaseHelper.deleteTask(tasks.get(position).getId());
//                                        tasks.remove(position);
//                                        if (position < s) s--;
//                                    }
//                                })
//                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                })
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();


                        mDatabaseHelper.deleteTask(tasks.get(position).getId());
                        tasks.remove(position);
                        if (position < s) s--;

                    }

                    if (direction == SwipeDirection.DIRECTION_FAR_RIGHT) {
                        if (SHOW_COMPLETED) {
//                            Log.e("hey!", "it starts here");
                            if (position >= s) {
//                                Log.e("HEY!", "IT WORKED!!!");
                                mDatabaseHelper.returnTask(tasks.get(position));
                                onStart();
                            }
                            else {
                                mDatabaseHelper.completeTask(tasks.get(position));
                                tasks.remove(position);
                                onStart();

                            }
                        }
                        else {
                            mDatabaseHelper.completeTask(tasks.get(position));
                            tasks.remove(position);
                            s--;
                        }
                    }
//                    onStart();
                    adapter1.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
//                adapter1.notifyDataSetChanged();
//                adapter.notifyDataSetChanged();
            }
        });
    }


    private void toastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int myValue = this.getArguments().getInt("id");
        boolean myValue2 = this.getArguments().getBoolean("show_completed");
        int sort = this.getArguments().getInt("sort");
        SHOW_COMPLETED = myValue2;
        selectedID = myValue;
        SORTLIST = sort;
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}