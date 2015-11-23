package it.polito.mad.polilife;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;


/**
 * Created by luigi on 05/05/15.
 */
public class PoliLifeNavigationDrawer {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onHeaderClick(View view);
        void onFooterClick(View view);
    }

    static class SimpleOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(View view, int position) {}
        @Override
        public void onHeaderClick(View view) {}
        @Override
        public void onFooterClick(View view) {}
    }

    private OnItemClickListener listener;
    private NavigationAdapter mAdapter;

    private View mRootView;
    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;


    public PoliLifeNavigationDrawer(String[] titles, int[] icons, DrawerLayout drawerLayout,
                                   OnItemClickListener itemClickListener){
        if (drawerLayout == null){
            throw new RuntimeException("Params cannot be null");
        }
        mDrawerLayout = drawerLayout;
        //mRootView = drawerLayout.getChildAt(1);
        mRootView = drawerLayout.findViewById(R.id.drawer_root);
        listener = itemClickListener;
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(drawerLayout.getContext()));
        mAdapter = new NavigationAdapter(titles, icons, listener);
        mRecyclerView.setAdapter(mAdapter);
    }

    public PoliLifeNavigationDrawer(String[] titles, int[] icons, DrawerLayout drawerLayout){
        this(titles, icons, drawerLayout, null);
    }

    public View getRootView() {
        return mRootView;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mAdapter.listener = listener;
    }

    /*
     * Sets user name and mail on the header of the navigation drawer.
     * Profile image is provided by a resource ID
     */
    public void setUserData(String username, String mail, int profileResourceID){
        mAdapter.name = username;
        mAdapter.email = mail;
        mAdapter.profileResourceID = profileResourceID;
        mAdapter.profileBitmap = null;
        mAdapter.notifyItemChanged(0);
    }

    /*
     * Sets user name and mail on the header of the navigation drawer.
     * Profile image is provided by a Bitmap
     */
    public void setUserData(String username, String mail, Bitmap bitmap){
        mAdapter.name = username;
        mAdapter.email = mail;
        mAdapter.profileResourceID = 0;
        mAdapter.profileBitmap = bitmap;
        mAdapter.notifyItemChanged(0);
    }

    public void toggle(){
        if (mDrawerLayout.isDrawerOpen(mRootView)){
            mDrawerLayout.closeDrawer(mRootView);
        } else {
            mDrawerLayout.openDrawer(mRootView);
        }
    }

    public void open(){
        if (!mDrawerLayout.isDrawerOpen(mRootView)){
            mDrawerLayout.openDrawer(mRootView);
        }
    }

    public void close(){
        if (mDrawerLayout.isDrawerOpen(mRootView)){
            mDrawerLayout.closeDrawer(mRootView);
        }
    }

}