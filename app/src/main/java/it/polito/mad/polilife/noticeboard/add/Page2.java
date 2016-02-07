package it.polito.mad.polilife.noticeboard.add;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polito.mad.polilife.R;
import it.polito.mad.polilife.db.parcel.PNoticeData;
import it.polito.mad.polilife.noticeboard.NoticeUpdater;

public class Page2 extends Fragment implements NoticeUpdater {

    private static final int CAPTURE_PHOTO_REQUEST_CODE = 1;
    private static final int SELECT_PHOTO_FROM_GALLERY_REQUEST_CODE = 2;
    private static final int MAX_GALLERY_SIZE = 10;

    private GridView tagsView;
    private ImageButton chooseButton, newTagButton;
    private EditText newTagEditText;
    private ImageView mPreview;

    private List<Bitmap> imagesData = new ArrayList<>();
    private List<String> tagsData = new ArrayList<>();

    private GalleryAdapter mGalleryAdapter = new GalleryAdapter();

    @Override
    public void update(PNoticeData data) {
        for (int i=0;i<imagesData.size();i++){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagesData.get(i).compress(Bitmap.CompressFormat.JPEG, 90, stream);
            data.newPhoto("photo_"+i, stream.toByteArray());
        }
        for (String tag : tagsData){
            data.newTag(tag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_notice_page2, container, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gallery gallery = (Gallery) view.findViewById(R.id.gallery);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPreview.setImageBitmap(imagesData.get(position));
            }
        });
        gallery.setAdapter(mGalleryAdapter);
        gallery.setSpacing(1);

        mPreview = (ImageView) view.findViewById(R.id.preview);
        chooseButton = (ImageButton) view.findViewById(R.id.newPhotoButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = getActivity().getResources().getStringArray(R.array.choose_photo_titles);
                final Integer[] icons = new Integer[] {
                        android.R.drawable.ic_menu_camera,
                        android.R.drawable.stat_notify_sdcard,
                        android.R.drawable.ic_menu_close_clear_cancel
                };
                ListAdapter adapter = new ArrayAdapterWithIcon(getActivity(), items, icons);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.details);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    //builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAPTURE_PHOTO_REQUEST_CODE);
                                break;
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(
                                        Intent.createChooser(galleryIntent, "Select File"), SELECT_PHOTO_FROM_GALLERY_REQUEST_CODE);
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });


        tagsView = (GridView) view.findViewById(R.id.tags_grid);
        tagsView.setAdapter(new TagsAdapter());
        newTagEditText = (EditText) view.findViewById(R.id.new_tag_edit_text);
        newTagButton = (ImageButton) view.findViewById(R.id.new_tag_confirm);
        newTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedTag = newTagEditText.getText().toString();
                String errorMsg = null;
                if (typedTag.isEmpty()) {
                    errorMsg = getString(R.string.empty_tag);
                }
                else if (tagsData.contains(typedTag)){
                    errorMsg = "Tag '" + typedTag + "' already set";
                }
                if (errorMsg != null){
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                tagsData.add(newTagEditText.getText().toString());
                ((BaseAdapter) tagsView.getAdapter()).notifyDataSetChanged();
                newTagEditText.getText().clear();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null){
            outState.putStringArrayList("tags", new ArrayList<>(tagsData));
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null){
            tagsData = savedInstanceState.getStringArrayList("tags");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddNoticeActivity.RESULT_OK) {
            if (requestCode == CAPTURE_PHOTO_REQUEST_CODE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imagesData.add(thumbnail);
                mGalleryAdapter.notifyDataSetChanged();
                mPreview.setImageBitmap(thumbnail);
            }
            else if (requestCode == SELECT_PHOTO_FROM_GALLERY_REQUEST_CODE) {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                cursor.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap bm = BitmapFactory.decodeFile(imagePath, options);
                // Now we need to set the GUI ImageView data with data read fillFrom the picked file.
                imagesData.add(bm);
                mGalleryAdapter.notifyDataSetChanged();
                mPreview.setImageBitmap(bm);
            }
        }
    }



    class TagsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tagsData.size();
        }

        @Override
        public String getItem(int position) {
            return tagsData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.layout_tags_grid_item, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.tag_value)).setText("#"+getItem(position));
            convertView.findViewById(R.id.cancel_tag).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tagsData.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    class GalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imagesData.size();
        }

        @Override
        public Bitmap getItem(int position) {
            return imagesData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = new ImageView(getActivity());
            }
            ImageView iv = (ImageView) convertView;
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setImageBitmap(getItem(position));
            return convertView;
        }
    }

    public class ArrayAdapterWithIcon extends ArrayAdapter<String> {
        private Integer[] images;
        public ArrayAdapterWithIcon(Context context, String[] items, Integer[] images) {
            super(context, android.R.layout.select_dialog_item, items);
            this.images = images;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesWithIntrinsicBounds(images[position], 0, 0, 0);
            //textView.setCompoundDrawablePadding(
              //      (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
            return view;
        }

    }


    public class MyGridView extends GridView {
        public MyGridView(Context context) {
            super(context);
        }

        public MyGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int heightSpec;

            if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
                // The great Android "hackatlon", the love, the magic.
                // The two leftmost bits in the height measure spec have
                // a special meaning, hence we can't use them to describe height.
                heightSpec = MeasureSpec.makeMeasureSpec(
                        Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            }
            else {
                // Any other height should be respected as is.
                heightSpec = heightMeasureSpec;
            }

            super.onMeasure(widthMeasureSpec, heightSpec);
        }
    }
}